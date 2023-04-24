package inteligenca;

import logika.Igra;
import splosno.KdoIgra;
import splosno.Poteza;

public class Inteligenca extends KdoIgra {
	
	private MooveChooser dolocevalec;
	
	/**
	 * Konstruktor z določenim določevalcem poteze
	 * @param dolocevalec
	 */
	public Inteligenca(MooveChooser dolocevalec) {
		super(dolocevalec.name());
		this.dolocevalec = dolocevalec;
	}
	
	/**
	 * Konstruktor, ki uporablja najboljšega določevalca.
	 * Je tu za namene tekmovanja.
	 */
	public Inteligenca() {
		this(new RandomMoveChooser());
	}
	
	public Poteza izberiPotezo(Igra igra) {		
		return dolocevalec.chooseMove(igra);
	}
}
