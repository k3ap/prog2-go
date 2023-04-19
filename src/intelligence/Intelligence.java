package intelligence;

import logika.Game;
import splosno.KdoIgra;
import splosno.Poteza;

public class Intelligence extends KdoIgra {
	public Intelligence() {
		super("betago");
	}
	
	public Poteza chooseMove(Game game) {
		// TODO: vrni najboljšo rešitev glede na dano igro
		// čas reševanja bo v tekmi omejen na 5s
		return null;
	}
}
