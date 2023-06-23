package inteligenca;

import java.util.Collections;
import java.util.List;

import logika.FieldColor;
import logika.GridFirstCapture;
import logika.Igra;
import logika.Index;
import logika.PlayerColor;
import splosno.Poteza;

/**
 * A move chooser using the alphabeta algorithm.
 * Assumes we're playing FCGO.
 */
public class AlphaBetaFCMoveChooser extends MoveChooser {
	
	private int maxDepth;
	private GridEstimator estimator;
	
	private final double INFINITY = 1e6;
	
	public AlphaBetaFCMoveChooser(int maxDepth, GridEstimator estimator) {
		super();
		this.maxDepth = maxDepth;
		this.estimator = estimator;
	}

	@Override
	public String name() {
		return "betago-alphabeta-"+maxDepth+"-"+estimator.name;
	}
	
	/**
	 * Perform a recursive position estimation.
	 * @param grid The current grid state
	 * @param depth The current depth of the search tree
	 * @param player The player whose turn it is
	 * @param alpha
	 * @param beta
	 * @return A position estimation, with high values being better for the given player
	 */
	private double alphabetaEstimate(GridFirstCapture grid, int depth, PlayerColor player, double alpha, double beta) {
		
		// check if the game's concluded
		if (grid.hasColorLost(player.next().field())) {
			return INFINITY-depth;
		} else if (grid.hasColorLost(player.field())) {
			return -INFINITY+depth;
		}
		
		// if we're deep enough in the tree, switch to the estimator
		if (depth >= maxDepth) {
			return estimator.estimateGrid(grid, player);
		}
		
		// if we have forced moves, we play them immediately as
		// there is nothing to be gained by checking other moves
		Index forcedMove = grid.forcedMove(player.field());
		if (forcedMove != null) {
			grid.placeColor(forcedMove, player.field());
			double estimate = -alphabetaEstimate(grid, depth, player.next(), -beta, -alpha);
			grid.placeColor(forcedMove, FieldColor.EMPTY);
			return estimate;
		}

		// we now check the possible moves recursively
		// we only check the "interesting" fields, whatever that might mean
		Double bestValue = null;
		List<Index> interesting = grid.interestingFields(player.field());
		
		// shuffle the list for performance reasons
		Collections.shuffle(interesting);
		
		for (Index move : interesting) {
			
			grid.placeColor(move, player.field());
			double value;
			
			if (grid.hasColorLost(player.next().field())) {
				value = INFINITY-depth;
			} else if (grid.hasColorLost(player.field())) {
				value = -INFINITY+depth;
			} else {
				value = -alphabetaEstimate(grid, depth+1, player.next(), -beta, -alpha);
			}
			
			// undo the change
			grid.placeColor(move, FieldColor.EMPTY);
			
			if (bestValue == null || value > bestValue)
				bestValue = value;
			
			if (alpha < value)
				alpha = value;
			
			if (beta < alpha) {
				break;
			}
		}
		return bestValue;
	}
	
	@Override
	public Poteza chooseMove(Igra igra) {
       
        // check for forced moves
		Index forced = ((GridFirstCapture)igra.grid).forcedMove(igra.playerTurn().field());
		if (forced != null)
			return forced.poteza();
		
			
		Poteza best = null;
		double bestEst = 0;
		double alpha = -INFINITY;
		double beta = INFINITY;
			
		// copy the grid to be used for move determining
		// we only do this once per call to chooseMove, and use the copy
		// in the recursive search, modifying it in-place
		GridFirstCapture newGrid = (GridFirstCapture) igra.grid.deepcopy();

		var interesting = igra.interestingMoves();
		Collections.shuffle(interesting);
		for (Poteza poteza : interesting) {
			newGrid.placeColor(new Index(poteza), igra.playerTurn().field());
			
			double estimate = 
				-alphabetaEstimate(newGrid, 1, igra.playerTurn().next(), -beta, -alpha);

			newGrid.placeColor(new Index(poteza), FieldColor.EMPTY);

			if (best == null || bestEst < estimate) {
				best = poteza;
				bestEst = estimate;
			}

			if (alpha < estimate)
				alpha = estimate;
		}
		return best;
	}

}
