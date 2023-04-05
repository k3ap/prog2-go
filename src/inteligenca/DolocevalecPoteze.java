package inteligenca;

import logika.Igra;
import splosno.Poteza;

/**
 * Razred za določitev izbire naslednje poteze.
 */
public abstract class DolocevalecPoteze {
	
	/**
	 * Ime določevalca; to ime se uporablja za ime inteligence.
	 * @return Ime določevalca
	 */
	public abstract String ime();
	
	/**
	 * Določevanje naslednje poteze
	 * @param igra Igra, ki ji moramo določiti potezo
	 * @return Poteza, ki jo bo igralec odigral
	 */
	public abstract Poteza dolociPotezo(Igra igra); 
}
