package inteligenca;

import logika.Igra;
import splosno.KdoIgra;
import splosno.Poteza;

public class Inteligenca extends KdoIgra {
	
	private MoveChooser dolocevalec;
	
	/**
	 * Konstruktor z določenim določevalcem poteze
	 * @param dolocevalec
	 */
	public Inteligenca(MoveChooser dolocevalec) {
		super(dolocevalec.name());
		this.dolocevalec = dolocevalec;
	}
	
	/**
	 * Konstruktor, ki uporablja najboljšega določevalca.
	 * Je tu za namene tekmovanja.
	 */
	public Inteligenca() {
		this(new AlphaBetaMoveChooser(3, new WeightedGridEstimator()));
		this.ime = "betago";
	}
	
	public Poteza izberiPotezo(Igra igra) {		
		return dolocevalec.chooseMove(igra);
	}
	
	@Override
	public String toString() {
		return ime;
	}
}
