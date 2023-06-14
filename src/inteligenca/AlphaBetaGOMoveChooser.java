package inteligenca;

import java.util.Collections;
import java.util.List;

import logika.GridGo;
import logika.Igra;
import logika.Index;
import logika.PlayerColor;
import splosno.Poteza;

/**
 * Move chooser for GO using the alphabeta algorithm.
 */
public class AlphaBetaGOMoveChooser extends MoveChooser {
	private int maxDepth;
	private GridEstimator estimator;
	
	private final double INFINITY = 1e6;
	
	public AlphaBetaGOMoveChooser(int maxDepth, GridEstimator estimator) {
		super();
		this.maxDepth = maxDepth;
		this.estimator = estimator;
	}

	@Override
	public String name() {
		return "betago-alphabeta-"+maxDepth+"-"+estimator.name;
	}
	
	private double alphabetaEstimate(GridGo grid, int depth, PlayerColor player, double alpha, double beta) {
		
		if (grid.consecutivePasses()) {
			if (grid.hasColorLost(player.field())) {
				return -INFINITY;
			} else {
				return INFINITY;
			}
		}
		
		if (depth >= maxDepth) {
			return estimator.estimateGrid(grid, player);
		}

		Double bestValue = null;

		List<Index> interesting = grid.interestingFields(player.field());
		Collections.shuffle(interesting);
		for (Index move : interesting) {
			
			GridGo newGrid = (GridGo) grid.deepcopy();
			
			newGrid.placeColor(move, player.field());
			newGrid.captureComponentsOf(player.next().field());
			double value = -alphabetaEstimate(newGrid, depth+1, player.next(), -beta, -alpha);
			
			if (bestValue == null || value > bestValue)
				bestValue = value;
			
			if (alpha < value)
				alpha = value;
			
			if (beta < alpha) {
				break;
			}
		}
		
		// pass move
		switch(player) {
		case BLACK:
			grid.blackPass = true;
			break;
		case WHITE:
			grid.whitePass = true;
			break;
		}
		double value = -alphabetaEstimate(grid, depth+1, player.next(), -beta, -alpha);
		if (value > bestValue)
			bestValue = value;
		
		return bestValue;
	}
	
	@Override
	public Poteza chooseMove(Igra igra) {
		double bestEst = 0;
		Poteza best = null;
		double alpha = -INFINITY;
		double beta = INFINITY;

		// find interesting moves
		var interesting = igra.interestingMoves();
		Collections.shuffle(interesting);
		for (Poteza poteza : interesting) {
				
			// play the given move and recursievely estimate outcome
			GridGo newGrid = (GridGo) igra.grid.deepcopy();
			newGrid.placeColor(new Index(poteza), igra.playerTurn().field());
		
			double estimate =
				-alphabetaEstimate(newGrid, 1, igra.playerTurn().next(), -beta, -alpha);
				
			if (best == null || bestEst < estimate) {
				best = poteza;
				bestEst = estimate;
			}
				
			if (alpha < estimate)
				alpha = estimate;
		}

		// pass move
		GridGo newGrid = (GridGo) igra.grid.deepcopy();
		switch(igra.playerTurn()) {
		case BLACK:
			newGrid.blackPass = true;
			break;
		case WHITE:
			newGrid.whitePass = true;
			break;
		}
		double value = -alphabetaEstimate(newGrid, 1, igra.playerTurn().next(), -beta, -alpha);
		if (value > bestEst)
			best = new Poteza(-1, -1);
		
		return best;
	}
}
