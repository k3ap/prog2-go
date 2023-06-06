package logika;

import java.util.ArrayList;
import java.util.List;

import splosno.Poteza;

/**
 * Represents the current state of the board.
 */
public class Igra {
	
	public Grid grid;
	private PlayerColor nextPlayer;
	private PlayerColor winner;
	private GoGameType gameType;

	/**
	 * Creates a new game with a 9x9 grid and FC rules.
	 * Exists here for the competition.
	 */
	public Igra() {
		this(9, GoGameType.FCGO);
	}

	/**
	 * Creates a new game with the given grid size.
	 * @param size Width and height of the new grid.
	 */
	public Igra(int size, GoGameType gameType) {
		this.gameType = gameType; 
		grid = switch(gameType) {
			case FCGO -> new GridFirstCapture(size, size);
			case GO -> new GridGo(size, size);
		};
		
		winner = null; // null means no one has won yet
		nextPlayer = PlayerColor.BLACK;
	}
	
	/**
	 * Plays the given move.
	 * Returns `true` if the move is valid, else `false`.
	 * Where Poteza(0,0) is the upper left corner, Poteza(1,0) is in the second row,
	 * Poteza(8,8) is the bottom right corner (in a 9x9 grid).
	 * @param poteza Index for the move (row, col), counted from 0
	 * @return Whether the move was valid.
	 */
	public boolean odigraj(Poteza poteza) {
		// mark passes
		switch (nextPlayer) {
		case BLACK:
			grid.blackPass = poteza.isPass();
			break;
		case WHITE:
			grid.whitePass = poteza.isPass();
			break;
		}
		
		if (!poteza.isPass()) {
			// an actual move was played
			Index idx = new Index(poteza);
			
			if (!grid.isValid(idx, nextPlayer.field())) {
				return false;
			}
			
			grid.placeColor(idx, nextPlayer.field());
		}
		PlayerColor thisPlayer = nextPlayer;
		nextPlayer = nextPlayer.next();
		
		if (gameType.equals(GoGameType.FCGO)) {
			if (grid.hasColorLost( FieldColor.playersColor(nextPlayer) )) {
				// thisPlayer has made a winning move
				winner = thisPlayer;
				return true;
			}
			if (grid.hasColorLost( FieldColor.playersColor( thisPlayer ) )) {
				// tihisPlayer has made a suicidal Poteza
				winner = nextPlayer;
				return true;
			}
		} else {
			// Because of the suicide rule a player can't play a move
			// that would result in their own components being captured.
			grid.captureComponentsOf(FieldColor.playersColor(nextPlayer));
		}
		if (grid.consecutivePasses()) {
			// Passes are only allowed in Go.
			// Once both players pass one after another the game is over.
			// In Go, once the game is finished, one of the player has won.
			if (grid.hasColorLost(FieldColor.BLACK)) {
				winner = PlayerColor.WHITE;
				return true;
			}
			else {
				winner = PlayerColor.BLACK;
				return true;
			}
		}
		return true;
	}
	
	/**
	 * Search for all valid moves that nextPlayer can make.
	 * @return A list of valid moves.
	 */
	public List<Poteza> validMoves(FieldColor player) {
		// TODO: comptibility with GO
		// Should we make a validPlacements method in Grid, which functions like freeFields
		// uses isPlacementValid instead of isFree?
		ArrayList<Poteza> res = new ArrayList<>();
		for (Index idx : grid.validFields(player)) {
			res.add(idx.poteza());
		}
		return res;
	}
	
	/**
	 * Search for all interesting moves that nextPlayer can make.
	 * @return A list of valid moves.
	 */
	public List<Poteza> interestingMoves() {
		ArrayList<Poteza> res = new ArrayList<>();
		for (Index idx : grid.interestingFields(nextPlayer.field())) {
			res.add(idx.poteza());
		}
		assert res.size() >= 1;
		return res;
	}
	
	/**
	 * A forced move is one that either wins the game if made or
	 * prevents you from immediately looses the game if not played.
	 * @return A Poteza of the forced move, null if there is none.
	 */
	public Poteza forcedMove() {
		Index idx = grid.forcedMove(nextPlayer);
		if (idx == null) return null;
		return idx.poteza();
	}

	public int width() { return grid.width(); }
	public int height() { return grid.height(); }
	public FieldColor fieldColor(Index idx) { return grid.colorOfField(idx); }
	/**
	 * Whose turn is it.
	 * @return The color of the player whose turn it is.
	 */
	public PlayerColor playerTurn() { return nextPlayer; }
	public PlayerColor winner() { return winner; }
	/**
	 * @see GridFirstCapture#losingComponent
	 */
	public Index[] losingComponent() { return grid.libertylessFields(winner.next().field()); }

	public boolean isValid(Index idx) { return grid.isValid(idx, nextPlayer.field()); }
}
