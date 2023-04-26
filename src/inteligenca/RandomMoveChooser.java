package inteligenca;

import java.util.List;
import java.util.Random;

import logika.Igra;
import splosno.Poteza;

/**
 * The simplest move choosing algorithm, chooses a random valid move.
 */
public class RandomMoveChooser extends MoveChooser {
	
	private static Random rng = new Random();

	@Override
	public Poteza chooseMove(Igra igra) {
		List<Poteza> validMoves = igra.validMoves();
		int idx = Math.abs(rng.nextInt()) % validMoves.size();
		return validMoves.get(idx);
	}

	@Override
	public String name() {
		return "betago-random";
	}
}
