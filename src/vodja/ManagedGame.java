package vodja;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import gui.Window;
import inteligenca.Inteligenca;
import logika.PlayerColor;
import logika.FieldColor;
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
	private MoveResult status;
	private Window window;

	/**
	 * Create a new managed game with the given game type.
	 * @param gameType See the enum {@link GameType}
	 */
	public ManagedGame(GameType gameType, Window window) {
		game = new Igra();
		this.window = window;
		dealWithType(gameType);
	}

	/**
	 * Create a new managed game with the given game type and grid size.
	 * @param gameType See the enum {@link GameType}
	 * @param size Size (width and height) of the game grid, default is 9.
	 */
	public ManagedGame(GameType gameType, int size) {
		game = new Igra(size);
		dealWithType(gameType);
	}

	private void dealWithType(GameType gameType) {
		this.gameType = gameType;
		switch (gameType) {
		case HUMHUM:
			intelligence = null;
			status = MoveResult.PLAY;
			break;
		case HUMCOM:
			intelligence = new Inteligenca();
			status = MoveResult.PLAY; // it's the human's turn first
			break;
		case COMHUM:
			intelligence = new Inteligenca();
			status = MoveResult.WAIT; // it's the computer's turn first
			getMoveFromIntelligence();
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
		if (
				(gameType == GameType.HUMCOM || gameType == GameType.COMHUM) && // the game type has the computer making moves 
				status == MoveResult.PLAY // no one has won yet
				) {
			// When the human has made their move it's the coputer's turn.
			status = MoveResult.WAIT;
			getMoveFromIntelligence();
		}
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
					move = get();
				}
				catch (ExecutionException | InterruptedException e) {
					// set the status to error and finish
					e.printStackTrace();
					status = MoveResult.ERROR;
					window.update();
					return;
				}

				if (move == null) {
					// Returning null is an error on Intelligence's part.
					System.out.println("Intelligence.getMove je vrnila null.");
					status = MoveResult.ERROR;
					window.update();
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

	/**
	 * Get the current game status.
	 * @return Current status as {@link MoveResult}.
	 */
	public MoveResult gameStatus() { return status; }
	
	public String intelligenceName() {
		assert gameType == GameType.COMHUM || gameType == GameType.HUMCOM;
		return intelligence.ime();
	}

	// Expose useful methods from Game
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
	 * Who's turn is it?
	 * @return {@link PlayerColor}, of the player who's move it is.
	 */
	public PlayerColor playerTurn() { return game.playerTurn(); }
}
