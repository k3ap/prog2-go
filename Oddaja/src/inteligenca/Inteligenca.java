package inteligenca;

import logika.Igra;
import splosno.KdoIgra;
import splosno.Poteza;

/**
 * A container class for MoveChooser
 */
public class Inteligenca extends KdoIgra {
	
	private MoveChooser moveChooser;
	
	/**
	 * If this is set to true, the intelligence is just a placeholder
	 * for a human player.
	 */
	private boolean isHuman;
	
	public Inteligenca(MoveChooser chooser) {
		super(chooser.name());
		this.isHuman = false;
		this.moveChooser = chooser;
	}
	
	/**
	 * The constructor to be used in the competition.
	 */
	public Inteligenca() {
		this(new AlphaBetaFCMoveChooser(4, new WeightedGridEstimator()));
		
		// override the name (we don't want to be known as betago-alphabeta-4-weighted)
		this.ime = "betago";
	}
	
	/**
	 * This is really a placeholder for a human player 
	 * @param name An arbitrary name
	 * @param isHuman Must be true.
	 */
	public Inteligenca(String name, boolean isHuman) {
		super(name);
		assert isHuman;
		this.isHuman = isHuman;
		this.moveChooser = null;
	}
	
	public boolean isHuman() {
		return this.isHuman;
	}
	
	public Poteza izberiPotezo(Igra igra) {
		assert !isHuman;
		return moveChooser.chooseMove(igra);
	}
	
	@Override
	public String toString() {
		return ime;
	}
}
