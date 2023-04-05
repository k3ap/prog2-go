package inteligenca;

import java.util.List;
import java.util.Random;

import logika.Igra;
import splosno.Poteza;

/**
 * Najbolj neumen določevalec poteze, ki naslednjo potetzo izbere naključno.
 */
public class NakljucniDolocevalecPoteze extends DolocevalecPoteze {
	
	private static Random rng = new Random();

	@Override
	public Poteza dolociPotezo(Igra igra) {
		List<Poteza> moznePoteze = igra.veljavnePoteze();
		int idx = rng.nextInt() % moznePoteze.size();
		return moznePoteze.get(idx);
	}

	@Override
	public String ime() {
		return "betago-nakljucni";
	}
}
