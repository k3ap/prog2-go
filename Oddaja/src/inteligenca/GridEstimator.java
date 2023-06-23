package inteligenca;

import logika.Grid;
import logika.PlayerColor;

/**
 * An abstract class providing a function to perform grid estimation
 * for the alphabeta algoritm.
 */
public abstract class GridEstimator {
	
	/**
	 * The name of the alphabeta move chooser contains this string 
	 */
	public final String name;
	
	public GridEstimator(String name) {
		this.name = name;
	}
	
	/**
	 * Perform an estimation of the grid position.
	 * Higher numbers mean that this is a better position for the given player,
	 * which is about to perform a move.
	 * @param grid The grid we're estimating
	 * @param player The player on turn
	 * @return A value in the range [-infinity, infinity], with infinity meaning certain victory for the given player
	 */
	abstract double estimateGrid(Grid grid, PlayerColor player);
}
