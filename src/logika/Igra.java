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
		// funkcija odigra dano potezo, če je možna
		// vrne true, če je bila poteza možna, sicer false
		// Poteza(0,0) je levo zgoraj, Poteza(8,8) pa desno spodaj
		Indeks idx = new Indeks(poteza);
		if (mreza.barvaPolja(idx) != BarvaPolja.PRAZNA) {
			return false;
		}
		mreza.postaviBarvo(idx, BarvaPolja.igralcevaBarva(naslednjiIgralec));
		naslednjiIgralec = BarvaIgralca.novaBarva(naslednjiIgralec);
		if (mreza.jeBarvaIzgubila( BarvaPolja.igralcevaBarva(naslednjiIgralec) )) {
			System.out.println("poteza je bila zmagajoca");
			return true;
		}
		if (mreza.jeBarvaIzgubila( BarvaPolja.igralcevaBarva( BarvaIgralca.novaBarva(naslednjiIgralec) ) )) {
			System.out.println("poteza je bila samomor");
			return true;
		}
		return true;
	}
}
