package inteligenca;

import logika.Igra;
import splosno.Poteza;

/**
 * The abstract class providing a function for choosing the next move.
 */
public abstract class MoveChooser {
	
	/**
	 * The name of the chooser, it's used as the name for the intelligence.
	 * @return The name of the chooser
	 */
	public abstract String name();
	
	/**
	 * Choose the next move.
	 * @param igra The state of the game for which the move is chosen.
	 * @return Poteza, that the chooser has chosen.
	 */
	public abstract Poteza chooseMove(Igra igra); 
}
