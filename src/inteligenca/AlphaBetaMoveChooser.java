package inteligenca;

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
		
		if (depth >= maxDepth)
			return estimator.estimateGrid(grid, player);
		
		Poteza forcedMove = grid.forcedMove(player);
		if (forcedMove != null) {
			grid.placeColor(new Index(forcedMove), player.field());
			return -alphabetaEstimate(grid, depth, player.next(), -beta, -alpha);
		}
		
		Double najbolje = null;
		for (Index move : grid.interestingFields()) {
			Grid newGrid = grid.placeColorAndCopy(move, player.field());
			double value;
			if (grid.hasColorLost(player.next().field())) {
				value = INFINITY-depth;
			} else if (grid.hasColorLost(player.field())) {
				value = -INFINITY+depth;
			} else {
				value = -alphabetaEstimate(newGrid, depth+1, player.next(), -beta, -alpha);
			}
			if (najbolje == null || value > najbolje)
				najbolje = value;
			
			if (alpha < value)
				alpha = value;
			
			if (beta < alpha) break;
		}
		return najbolje;
	}

	@Override
	public Poteza chooseMove(Igra igra) {
		Poteza best = igra.forcedMove();
		double alpha = -INFINITY;
		if (best == null) {
			// there are no forced moves
			double bestEst = 0;
			double beta = INFINITY;
			for (Poteza poteza : igra.interestingMoves()) {
				Grid newGrid = igra.grid.placeColorAndCopy(new Index(poteza), igra.playerTurn().field());
				double ocena = -alphabetaEstimate(newGrid, 1, igra.playerTurn().next(), -beta, -alpha);
				if (best == null || bestEst < ocena) {
					best = poteza;
					bestEst = ocena;
				}
				if (alpha < ocena)
					alpha = ocena;
			}
		}
		return best;
	}

}
