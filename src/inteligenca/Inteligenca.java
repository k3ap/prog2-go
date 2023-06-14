package inteligenca;

import logika.Igra;
import splosno.KdoIgra;
import splosno.Poteza;

public class Inteligenca extends KdoIgra {
	
	private MoveChooser dolocevalec;
	private boolean isHuman;
	
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
		this(new AlphaBetaFCMoveChooser(4, new WeightedGridEstimator()));
		this.ime = "betago";
	}
	
	/**
	 * Inteligenca, brez dolocevalca z danim imenom.
	 * @param ime Ime inteligence.
	 * @param isHuman Če je ta inteligenca človek.
	 */
	public Inteligenca(String ime, boolean isHuman) {
		super(ime);
		this.isHuman = isHuman;
		this.dolocevalec = null;
	}
	
	public boolean isHuman() {
		return this.isHuman;
	}
	
	public Poteza izberiPotezo(Igra igra) {		
		return dolocevalec.chooseMove(igra);
	}
	
	@Override
	public String toString() {
		return ime;
	}
}
