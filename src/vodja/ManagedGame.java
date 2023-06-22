package vodja;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingWorker;

import gui.IntelligencePair;
import gui.Window;
import inteligenca.Inteligenca;
import logika.PlayerColor;
import logika.FieldColor;
import logika.GoGameType;
import logika.GridGo;
import logika.Igra;
import logika.Index;
import splosno.Poteza;

/**
 * A wrapper around Game and Intelligence that handles the interactions between these. 
 */
public class ManagedGame {
	private Igra game;
	private GameType gameType;
	private Inteligenca intelligence;
	private Inteligenca intelligence2; // only used when 2 computers are playing against each other
	private MoveResult status;
	/**
	 * Which spaces can the player play on.
	 */
	private boolean[][] validity;
	private boolean outdatedValidity;
	private Window window;
	
	class Sleeper extends Thread {
		public void run() {
			try {
				TimeUnit.MILLISECONDS.sleep(window.getCompDelay());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	private Sleeper sleeper = new Sleeper();
	
	/**
	 * Create a new managed game.
	 * @param managedGameType
	 * @param window
	 * @param goGameType
	 */
	public ManagedGame(GameType managedGameType, Window window, GoGameType goGameType, int gameSize) {
		this.game = new Igra(gameSize, goGameType);
		validity = new boolean[gameSize][gameSize];
		for (int i = 0; i < gameSize; i++) {
			for (int j = 0; j < gameSize; j++) {
				validity[i][j] = true;
			}
		}
		outdatedValidity = false;
		this.window = window;
		handleType(managedGameType);
	}
	
	/**
	 * Set the intelligence in a single-computer game
	 * @param intelligence
	 */
	public void setIntelligence(Inteligenca intelligence) {
		assert gameType.equals(GameType.COMHUM) || gameType.equals(GameType.HUMCOM);
		this.intelligence = intelligence;
	}
	
	/**
	 * Set the intelligences to be used in a computer-computer game
	 * @param p
	 */
	public void setIntelligences(IntelligencePair p) {
		assert gameType.equals(GameType.COMCOM);
		this.intelligence = p.black();
		this.intelligence2 = p.white();
	}

	private void handleType(GameType gameType) {
		this.gameType = gameType;
		switch (gameType) {
		case HUMHUM:
			status = MoveResult.PLAY;
			break;
		case HUMCOM:
			status = MoveResult.PLAY; // it's the human's turn first
			break;
		case COMHUM:
			status = MoveResult.WAIT; // it's the computer's turn first
			getMoveFromIntelligence();
			break;
		case COMCOM:
			comComLoop();
			break;
		}
	}

	/**
	 * Checks if the given move is valid and makes it if it is.
	 * Updates the status based on validity / move result.
	 * @param move Instance of the class {@link Poteza}
	 * @see #statusIgre
	 */
	public void play(Poteza move) {
		if (!game.odigraj(move)) {
			// If the given move is invalid we don't do anything else.
			status = MoveResult.INVALID;
			return;
		}
		
		status = winnerToGameStatus(game.winner());
		if ((gameType == GameType.HUMCOM || gameType == GameType.COMHUM) // the game type has the computer making moves 
			&& status.canMakeMove()) // no one has won yet 
		{
			// When the human has made their move it's the computer's turn.
			status = MoveResult.WAIT;
			for (int i = 0; i < height(); i++) {
				for (int j = 0; j < width(); j++) {
					validity[i][j] = false;
					
				}
			}
			getMoveFromIntelligence();
		}
		outdatedValidity = true;
	}

	private void getMoveFromIntelligence() {
		SwingWorker<Poteza, Void> worker = new SwingWorker<Poteza, Void> () {
			@Override
			protected Poteza doInBackground() {
				return intelligence.izberiPotezo(game);
			}
			@Override
			protected void done () {
				// try to read Intelligence's decision
				Poteza move = null;
				try {
					sleeper.run();
					move = get();
					sleeper.join();
				}
				catch (ExecutionException | InterruptedException e) {
					// set the status to error and finish
					e.printStackTrace();
					status = MoveResult.ERROR;
					window.update();
					return;
				}

				if (move == null) {
					nullReturnError(intelligence);
					return;
				}

				game.odigraj(move);
				status = winnerToGameStatus(game.winner());
				window.update();
			}
		};
		status = MoveResult.WAIT;
		worker.execute();
	}
	
	/**
	 * The loop that plays two intelligences against each other.
	 */
	private void comComLoop() {
		SwingWorker<PlayerColor, Void> worker = new SwingWorker<PlayerColor, Void> () {
			private Inteligenca switchIntelligence(Inteligenca current) {
				if (current == intelligence) {
					return intelligence2;
				}
				return intelligence;
			}
			@Override
			protected PlayerColor doInBackground() throws InterruptedException {
				Inteligenca active = intelligence;
				
				while (true) {
					sleeper.run();
					Poteza move = active.izberiPotezo(game);
					// if izberiPotezo took less than the sleeper's sleeping time than
					// wait for the sleeper to finish to slow the computers' game down
					sleeper.join();
					if (move == null) {
						nullReturnError(active);
						break;
					}
					
					game.odigraj(move);
					if (game.winner() != null) {
						break;
					}
					window.update();
					active = switchIntelligence(active);
				}
				
				return game.winner(); // can also be null if the game ended due to an exception
			}
			@Override
			protected void done () {
				PlayerColor winner = null;
				try {
					winner = get();
				}
				catch (ExecutionException | InterruptedException e) {
					// set the status to error and finish
					e.printStackTrace();
					status = MoveResult.ERROR;
					window.update();
					return;
				}

				if (winner == null) {
					// game ended due to an exception
					return;
				}

				status = winnerToGameStatus(winner);
				window.update();
			}
		};
		status = MoveResult.ALLCOMPUTERS;
		worker.execute();
	}
	
	private MoveResult winnerToGameStatus(PlayerColor winner) {
		if (winner == null) {
			// this means no one has won yet
			return MoveResult.PLAY;
		}
		switch (game.winner()) {
		case WHITE:
			return MoveResult.WHITEWINS;
		case BLACK:
			return MoveResult.BLACKWINS;
		default:
			assert false;
			return MoveResult.PLAY;
		}
	}
	
	private void nullReturnError(Inteligenca perpetrator) {
		// Returning null is an error on Intelligence's part.
		System.out.println("Intelligence.getMove od " + perpetrator.ime() + " je vrnila null.");
		status = MoveResult.ERROR;
		window.update();
	}

	/**
	 * Get the current game status.
	 * @return Current status as {@link MoveResult}.
	 */
	public MoveResult gameStatus() { return status; }
	
	public String intelligenceName() {
		assert gameType.mixedGame();
		return intelligence.ime();
	}
	public String intelligence1Name() {
		assert gameType == GameType.COMCOM;
		return intelligence.ime();
	}
	public String intelligence2Name() {
		assert gameType == GameType.COMCOM;
		return intelligence2.ime();
	}
	
	public GameType gameType() { return gameType; }

	/**
	 * Can only be called once the game is finished.
	 * @return the outcome of a game as the type {@link GameOutcome}
	 */
	public GameOutcome getOutcome() {
		assert status.isWonGame();
		if (gameType.mixedGame()) {
			if (status == MoveResult.BLACKWINS && gameType == GameType.COMHUM)
				return GameOutcome.COMWON;
			if (status == MoveResult.WHITEWINS && gameType == GameType.HUMCOM)
				return GameOutcome.COMWON;
			if (status == MoveResult.BLACKWINS && gameType == GameType.HUMCOM)
				return GameOutcome.HUMWON;
			if (status == MoveResult.WHITEWINS && gameType == GameType.COMHUM)
				return GameOutcome.HUMWON;
		}
		else if (gameType == GameType.HUMHUM) {
			if (status == MoveResult.WHITEWINS)
				return GameOutcome.HUMWHITEWON;
			return GameOutcome.HUMBLACKWON;
		}
		else {
			if (status == MoveResult.WHITEWINS)
				return GameOutcome.COMWHITEWON;
			return GameOutcome.COMBLACKWON;
		}
		
		return null;
	}

	// Expose useful methods from Igra
	/**
	 * @return Width of the current game grid.
	 */
	public int width() { return game.width(); }
	/**
	 * @return Height of the current game grid.
	 */
	public int height() { return game.height(); }
	/**
	 * @param idx {@link Index} Index of the field we want to check.
	 * @return The {@link FieldColor} at the given index.
	 */
	public FieldColor fieldColor(Index idx) { return game.fieldColor(idx); }
	/**
	 * Whose turn is it?
	 * @return {@link PlayerColor}, of the player who's move it is.
	 */
	public PlayerColor playerTurn() { return game.playerTurn(); }
	/**
	 * Get the component which has no liberties and has lost its owner the game.
	 * @return An array of indices that the component occupies.
	 */
	public Index[] losingComponent() { return game.losingComponent(); }
	public boolean isValid(Index idx) {
		if (outdatedValidity) {
			for (int i = 0; i < height(); i++) {
				for (int j = 0; j < width(); j++) {
					validity[i][j] = game.isValid(new Index(i, j));
					
				}
			}
			outdatedValidity = false;
		}
		return validity[idx.i()][idx.j()];
	}

	public int stoneCount(PlayerColor player) {
		int count = 0;
		for (int i = 0; i < game.height(); i++) {
			for (int j = 0; j < game.width(); j++) {
				if (game.grid.colorOfField(new Index(i, j)) == player.field())
					count++;
			}
		}
		return count;
	}
	public int captured(PlayerColor player) {
		assert game.goGameType() == GoGameType.GO;
		switch (player) {
		case BLACK:
			return ((GridGo) game.grid).blackCaptured;
		case WHITE:
			return ((GridGo) game.grid).whiteCaptured;
		}
		return -1;
	}
	public int calculatePoints(PlayerColor player) {
		return ((GridGo) game.grid).calculatePoints(player.field());
	}
	public Set<Index> controlledZones(PlayerColor player) { return game.controlledZones(player); }
	public Set<Index> prisonersOf(PlayerColor player) { return game.prisonersOf(player); }
	
	public GoGameType goGameType() { return game.goGameType(); }
	
	public boolean didPass(PlayerColor player) { return game.didPass(player); }
	public Poteza lastMove() { return game.lastMove(); }
}
