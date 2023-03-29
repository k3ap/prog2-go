package logika;

import splosno.Poteza;

public class Igra {
	
	private Mreza mreza;
	private BarvaIgralca naslednjiIgralec;

	public Igra() {
		this(9);
	}

	public Igra(int velikost) {
		mreza = new Mreza(velikost, velikost);
		naslednjiIgralec = BarvaIgralca.CRNA;
	}
	
	public boolean odigraj(Poteza poteza) {
		// TODO: funkcija odigra dano potezo, če je možna
		// vrne true, če je bila poteza možna, sicer false
		// Poteza(0,0) je levo zgoraj, Poteza(8,8) pa desno spodaj
		return false;
	}
}
