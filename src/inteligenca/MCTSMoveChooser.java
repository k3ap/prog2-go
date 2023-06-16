package inteligenca;

import logika.Igra;
import logika.GridGo;
import splosno.Poteza;

/**
 * Move chooser which uses the MCTS algorithm.
 * Only available for GO.
 */
public class MCTSMoveChooser extends MoveChooser {
	
	/**
	 * The number of runs the algorithm performs.
	 * Should probably be dynamic.
	 */
	private final int NUM_RUNS = 100;

	@Override
	public String name() {
		return "mcts";
	}

	@Override
	public Poteza chooseMove(Igra igra) {
		MCTSTreeNode root = new MCTSTreeNode((GridGo) igra.grid, igra.playerTurn());
		for (int i = 0; i < NUM_RUNS; i++) {
			root.singleRun();
		}
		return root.getBestMove();
	}

}
