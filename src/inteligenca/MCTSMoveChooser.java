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
		int simDepth = igra.grid.width() * igra.grid.height() / 5;
		
		double boardsize = igra.grid.width();
		long allotedTime = (long) (boardsize*boardsize*boardsize/48. - 13. * boardsize*boardsize / 16. + 551.*boardsize/48. - 760. / 16.);
		allotedTime *= 1000000000;
		
		MCTSTreeNode root = new MCTSTreeNode((GridGo) igra.grid, igra.playerTurn(), simDepth);
		long startTime = System.nanoTime();
		while (System.nanoTime() - startTime < allotedTime) {
			root.singleRun();
		}
		return root.getBestMove();
	}

}
