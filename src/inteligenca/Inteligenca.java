package inteligenca;

import logika.Igra;
import splosno.KdoIgra;
import splosno.Poteza;

public class Inteligenca extends KdoIgra {
	
	private DolocevalecPoteze dolocevalec;
	
	/**
	 * Konstruktor z določenim določevalcem poteze
	 * @param dolocevalec
	 */
	public Inteligenca(DolocevalecPoteze dolocevalec) {
		super(dolocevalec.ime());
		this.dolocevalec = dolocevalec;
	}
	
	/**
	 * Konstruktor, ki uporablja najboljšega določevalca.
	 * Je tu za namene tekmovanja.
	 */
	public Inteligenca() {
		this(new NakljucniDolocevalecPoteze());
	}
	
	public Poteza izberiPotezo(Igra igra) {		
		return dolocevalec.dolociPotezo(igra);
	}
}
