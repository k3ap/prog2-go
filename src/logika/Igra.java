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
			case GO -> null;  // TODO: switch this to proper GO grid
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
		Index idx = new Index(poteza);
		
		if (!grid.isPlacementValid(idx, nextPlayer.field())) {
			return false;
		}
		
		grid.placeColor(idx, nextPlayer.field());
		PlayerColor thisPlayer = nextPlayer;
		nextPlayer = nextPlayer.next();
		
		if (gameType.equals(GoGameType.FCGO)) {
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
		} else {
			// TODO
		}
		return true;
	}
	
	/**
	 * Search for all valid moves that nextPlayer can make.
	 * @return A list of valid moves.
	 */
	public List<Poteza> validMoves() {
		// TODO: comptibility with GO
		// Should we make a validPlacements method in Grid, which functions like freeFields
		// uses isPlacementValid instead of isFree?
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
	 * Whose turn is it.
	 * @return The color of the player whose turn it is.
	 */
	public PlayerColor playerTurn() { return nextPlayer; }
	public PlayerColor winner() { return winner; }
	/**
	 * @see GridFirstCapture#losingComponent
	 */
	public Index[] losingComponent() { return grid.losingComponent(winner.next().field()); }
}
