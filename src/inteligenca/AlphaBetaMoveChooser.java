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
			Grid newGrid = grid.placeColorAndReference(forcedMove, player.field());
			forcedTime += System.nanoTime() - before;
			double estimate = -alphabetaEstimate(newGrid, depth, player.next(), -beta, -alpha);
			newGrid.undoMove(forcedMove);
			return estimate;
		}
		forcedTime += System.nanoTime() - before;
		
		Double najbolje = null;
		before = System.nanoTime();
		var interesting = grid.interestingFields();
		interestingTime += System.nanoTime() - before;
		for (Index move : interesting) {
			before = System.nanoTime();
			Grid newGrid = grid.placeColorAndReference(move, player.field());
			copyTime += System.nanoTime() - before;
			double value;
			before = System.nanoTime();
			if (newGrid.hasColorLost(player.next().field())) {
				value = INFINITY-depth;
				hasLostTime += System.nanoTime() - before;
			} else if (newGrid.hasColorLost(player.field())) {
				value = -INFINITY+depth;
				hasLostTime += System.nanoTime() - before;
			} else {
				hasLostTime += System.nanoTime() - before;
				value = -alphabetaEstimate(newGrid, depth+1, player.next(), -beta, -alpha);
			}
			if (najbolje == null || value > najbolje)
				najbolje = value;
			
			if (alpha < value)
				alpha = value;

			newGrid.undoMove(move);
			
			if (beta < alpha) break;
		}
		return najbolje;
	}

	private double allTime;
	private double copyTime;
	private double forcedTime;
	private double interestingTime;
	private double estimateTime;
	private double hasLostTime;
	
	@Override
	public Poteza chooseMove(Igra igra) {
        allTime = 0.0;
        copyTime = 0.0;
        forcedTime = 0.0;
        interestingTime = 0.0;
        estimateTime = 0.0;
        hasLostTime = 0.0;
        
        double fb = System.nanoTime();
		Poteza best = igra.forcedMove();
		forcedTime += System.nanoTime() - fb;
		double alpha = -INFINITY;
		if (best == null) {
			// there are no forced moves
			double bestEst = 0;
			double beta = INFINITY;
			double ib = System.nanoTime();
			var interesting = igra.interestingMoves();
			interestingTime += System.nanoTime() - ib;
			System.out.println(interesting.size() + " interesting.");
			for (Poteza poteza : interesting) {
				double cb = System.nanoTime();
				Grid newGrid = igra.grid.placeColorAndCopy(new Index(poteza), igra.playerTurn().field());
				copyTime += System.nanoTime() - cb;
				double ocena = -alphabetaEstimate(newGrid, 1, igra.playerTurn().next(), -beta, -alpha);
				if (best == null || bestEst < ocena) {
					best = poteza;
					bestEst = ocena;
				}
				if (alpha < ocena)
					alpha = ocena;
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
