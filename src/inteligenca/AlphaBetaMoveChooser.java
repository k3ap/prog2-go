package inteligenca;

import java.util.Collections;
import java.util.List;

import logika.FieldColor;
import logika.Grid;
import logika.Igra;
import logika.Index;
import logika.PlayerColor;
import splosno.Poteza;

public class AlphaBetaMoveChooser extends MoveChooser {
	
	private int maxDepth;
	private GridEstimator estimator;
	
	private final double INFINITY = 1e6;
	
	public AlphaBetaMoveChooser(int maxDepth, GridEstimator estimator) {
		super();
		this.maxDepth = maxDepth;
		this.estimator = estimator;
	}

	@Override
	public String name() {
		return "betago-alphabeta-"+maxDepth+"-"+estimator.name;
	}
	
	private double alphabetaEstimate(Grid grid, int depth, PlayerColor player, double alpha, double beta) {
		
		if (grid.hasColorLost(player.next().field())) {
			return INFINITY-depth;
		} else if (grid.hasColorLost(player.field())) {
			return -INFINITY+depth;
		}
		
		if (depth >= maxDepth) {
			double e = estimator.estimateGrid(grid, player);
			return e;
		}
		
		Index forcedMove = grid.forcedMove(player);
		
		if (forcedMove != null) {
			grid.placeColor(forcedMove, player.field());
			double estimate = -alphabetaEstimate(grid, depth, player.next(), -beta, -alpha);
			grid.placeColor(forcedMove, FieldColor.EMPTY);
			return estimate;
		}

		Double bestValue = null;

		List<Index> interesting = grid.interestingFields();
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
		Poteza best = igra.forcedMove();
		
		if (best == null) { // there are no forced moves
			
			double bestEst = 0;
			double alpha = -INFINITY;
			double beta = INFINITY;
			
			// copy the grid to be used for move determining
			Grid newGrid = igra.grid.deepcopy();
			
			// find interesting moves
			var interesting = igra.interestingMoves();
			Collections.shuffle(interesting);
			for (Poteza poteza : interesting) {
				
				// play the given move and recursievely estimate outcome
				
				newGrid.placeColor(new Index(poteza), igra.playerTurn().field());
				
				double estimate = 
					-alphabetaEstimate(newGrid, 1, igra.playerTurn().next(), -beta, -alpha);
				
				// we should now undo the change
				newGrid.placeColor(new Index(poteza), FieldColor.EMPTY);
				
				if (best == null || bestEst < estimate) {
					best = poteza;
					bestEst = estimate;
				}
				
				if (alpha < estimate)
					alpha = estimate;
			}
		}
		return best;
	}

}
