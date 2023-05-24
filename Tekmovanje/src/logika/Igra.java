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

	/**
	 * Creates a new game with a 9x9 grid.
	 */
	public Igra() {
		this(9);
	}

	/**
	 * Creates a new game with the given grid size.
	 * @param size Width and height of the new grid.
	 */
	public Igra(int size) {
		grid = new Grid(size, size);
		winner = null; // null means no one has won yet
		nextPlayer = PlayerColor.BLACK;
	}
	
	/**
	 * Plays the given move.
	 * Returns `true` if the move is valid, else `false`.
	 * Where Poteza(0,0) is the upper left corner, Poteza(1,0) is in the second row,
	 * Poteza(8,8) is the bottom right corner (in a 9x9 grid).
	 * @param poteza Index for the move (row, col), counted from 0
	 * @return Is the move valid.
	 */
	public boolean odigraj(Poteza poteza) {
		Index idx = new Index(poteza);
		if (grid.colorOfField(idx) != FieldColor.EMPTY) {
			return false;
		}
		grid.placeColor(idx, FieldColor.playersColor(nextPlayer));
		PlayerColor thisPlayer = nextPlayer;
		nextPlayer = PlayerColor.newColor(nextPlayer);
		if (grid.hasColorLost( FieldColor.playersColor(nextPlayer) )) {
			// thisPlayer has made a winning move
			winner = thisPlayer;
			return true;
		}
		if (grid.hasColorLost( FieldColor.playersColor( PlayerColor.newColor(nextPlayer) ) )) {
			// tihisPlayer has made a suicidal Poteza
			winner = nextPlayer;
			return true;
		}
		return true;
	}
	
	/**
	 * Search for all valid moves that nextPlayer can make.
	 * @return A list of valid moves.
	 */
	public List<Poteza> validMoves() {
		ArrayList<Poteza> res = new ArrayList<>();
		for (Index idx : grid.freeFields()) {
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
		for (Index idx : grid.interestingFields()) {
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
	 * Who's turn is it.
	 * @return The color of the player whose turn it is.
	 */
	public PlayerColor playerTurn() { return nextPlayer; }
	public PlayerColor winner() { return winner; }
	/**
	 * @see Grid#losingComponent
	 */
	public Index[] losingComponent() { return grid.losingComponent(winner.next().field()); }
}
