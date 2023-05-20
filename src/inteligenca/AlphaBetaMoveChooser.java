package inteligenca;

import logika.Grid;
import logika.Igra;
import logika.Index;
import logika.PlayerColor;
import splosno.Poteza;

public class AlphaBetaMoveChooser extends MoveChooser {
	
	private int maxDepth;
	private GridEstimator estimator;
	
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
			return Double.POSITIVE_INFINITY;
		} else if (grid.hasColorLost(player.field())) {
			return Double.NEGATIVE_INFINITY;
		}
		
		if (depth >= maxDepth) return estimator.estimateGrid(grid, player);
		
		Double najbolje = null;
		for (Index move : grid.interestingFields()) {
			Grid newGrid = grid.deepcopy();
			newGrid.placeColor(move, player.field());
			double value;
			if (grid.hasColorLost(player.next().field())) {
				value = Double.POSITIVE_INFINITY;
			} else if (grid.hasColorLost(player.field())) {
				value = Double.NEGATIVE_INFINITY;
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
		double before = System.nanoTime();
		Poteza best = null;
		double bestEst = 0;
		double alpha = Double.NEGATIVE_INFINITY, beta = Double.POSITIVE_INFINITY;
		for (Poteza poteza : igra.interestingMoves()) {
			Grid newGrid = igra.grid.deepcopy();
			newGrid.placeColor(new Index(poteza), igra.playerTurn().field());
			double ocena = -alphabetaEstimate(newGrid, 1, igra.playerTurn().next(), -beta, -alpha);
			if (best == null || bestEst < ocena) {
				best = poteza;
				bestEst = ocena;
			}
			if (alpha < ocena)
				alpha = ocena;
		}
		double timeTaken = (System.nanoTime() - before) / 1000000000;
		// System.out.println("Apha: " + alpha + "\nTime taken: " + timeTaken + "s\nDepth: " + maxDepth + "\n------");
		return best;
	}

}
