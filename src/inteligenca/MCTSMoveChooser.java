package inteligenca;

import logika.Igra;
import logika.GridGo;
import splosno.Poteza;

/**
 * Move chooser which uses the MCTS algorithm.
 * Only available for GO.
 */
public class MCTSMoveChooser extends MoveChooser {

	@Override
	public String name() {
		return "mcts";
	}

	@Override
	public Poteza chooseMove(Igra igra) {
		int simDepth = igra.grid.width() * igra.grid.height() / 4;
		int numRuns = igra.grid.width() * igra.grid.height() * 2;
		MCTSTreeNode root = new MCTSTreeNode((GridGo) igra.grid, igra.playerTurn(), simDepth);
		for (int i = 0; i < numRuns; i++) {
			root.singleRun();
			System.out.format("Completed run %d of %d\n", i+1, numRuns);
		}
		return root.getBestMove();
	}

}
