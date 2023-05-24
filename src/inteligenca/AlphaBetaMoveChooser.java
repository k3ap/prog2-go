package inteligenca;

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
		
		double before = System.nanoTime();
		if (grid.hasColorLost(player.next().field())) {
			hasLostTime += System.nanoTime() - before;
			return INFINITY-depth;
		} else if (grid.hasColorLost(player.field())) {
			hasLostTime += System.nanoTime() - before;
			return -INFINITY+depth;
		}
		hasLostTime += System.nanoTime() - before;
		
		if (depth >= maxDepth) {
			before = System.nanoTime();
			double e = estimator.estimateGrid(grid, player);
			estimateTime += System.nanoTime() - before;
			return e;
		}
		
		before = System.nanoTime();
		Index forcedMove = grid.forcedMove(player);
		
		if (forcedMove != null) {
			grid.placeColor(forcedMove, player.field());
			forcedTime += System.nanoTime() - before;
			double estimate = -alphabetaEstimate(grid, depth, player.next(), -beta, -alpha);
			grid.placeColor(forcedMove, FieldColor.EMPTY);
			return estimate;
		}
		
		forcedTime += System.nanoTime() - before;
		
		Double bestValue = null;
		
		before = System.nanoTime();
		List<Index> interesting = grid.interestingFields();
		interestingTime += System.nanoTime() - before;
		
		for (Index move : interesting) {
			
			before = System.nanoTime();
			grid.placeColor(move, player.field());
			copyTime += System.nanoTime() - before;
			
			double value;
			
			before = System.nanoTime();
			if (grid.hasColorLost(player.next().field())) {
				value = INFINITY-depth;
				hasLostTime += System.nanoTime() - before;
			} else if (grid.hasColorLost(player.field())) {
				value = -INFINITY+depth;
				hasLostTime += System.nanoTime() - before;
			} else {
				hasLostTime += System.nanoTime() - before;
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

	private double allTime;
	private double copyTime;
	private double forcedTime;
	private double interestingTime;
	private double estimateTime;
	private double hasLostTime;
	
	@Override
	public Poteza chooseMove(Igra igra) {
		
		// timers
        allTime = 0.0;
        copyTime = 0.0;
        forcedTime = 0.0;
        interestingTime = 0.0;
        estimateTime = 0.0;
        hasLostTime = 0.0;
        
        // check for forced moves
        double fb = System.nanoTime();
		Poteza best = igra.forcedMove();
		forcedTime += System.nanoTime() - fb;
		
		if (best == null) { // there are no forced moves
			
			double bestEst = 0;
			double alpha = -INFINITY;
			double beta = INFINITY;
			
			// copy the grid to be used for move determining
			double cb = System.nanoTime();
			Grid newGrid = igra.grid.deepcopy();
			copyTime += System.nanoTime() - cb;
			
			// find interesting moves
			double ib = System.nanoTime();
			var interesting = igra.interestingMoves();
			interestingTime += System.nanoTime() - ib;
			
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
		allTime += System.nanoTime() - fb;
		allTime /= 1e9;
        copyTime /= 1e9;
        forcedTime /= 1e9;
        interestingTime /= 1e9;
        estimateTime /= 1e9;
        hasLostTime /= 1e9;
		System.out.println(
				"All time: " + allTime +
				"\nCopy time: " + copyTime +
				"\nForced time: " + forcedTime +
				"\nInteresting time: " + interestingTime +
				"\nEstimateTime: " + estimateTime +
				"\nHas lost time: " + hasLostTime +
				"\n----------"
        );
		return best;
	}

}
