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
		return "betago-mcts";
	}

	@Override
	public Poteza chooseMove(Igra igra) {
		int simDepth = igra.grid.width() * igra.grid.height() / 5;
		
		// the allowed simulation time for the standard board sizes is:
		// 9x9 - 5s
		// 13x13 - 10s
		// 17x17 - 15s
		// 19x19 - 20s
		// on large boards, the algorithm takes a lot of time, but doesn't play as well
		// the time to perform a single run of the simulation increases with the square of
		// the board size, but the allowed time increases linearly so that we don't have games which
		// take way too long
		double boardsize = igra.grid.width();
		long allotedTime = (long) (boardsize*boardsize*boardsize/48. - 13. * boardsize*boardsize / 16. + 551.*boardsize/48. - 760. / 16.);
		
		// System.nanoTime() uses nanoseconds
		allotedTime *= 1000000000;
		
		MCTSTreeNode root = new MCTSTreeNode((GridGo) igra.grid, igra.playerTurn(), simDepth);
		long startTime = System.nanoTime();
		while (System.nanoTime() - startTime < allotedTime) {
			root.singleRun();
		}
		return root.getBestMove();
	}

}
