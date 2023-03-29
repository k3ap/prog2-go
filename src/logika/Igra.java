package logika;

import splosno.Poteza;

/**
 * Predstavlja trenutno stanje igre.
 */
public class Igra {
	
	private Mreza mreza;
	private BarvaIgralca naslednjiIgralec;

	/**
	 * Ustvari novo igro na polju 9x9.
	 */
	public Igra() {
		this(9);
	}

	/**
	 * Ustvari novo igro dane velikosti.
	 * @param velikost Širina in višina polja.
	 */
	public Igra(int velikost) {
		mreza = new Mreza(velikost, velikost);
		naslednjiIgralec = BarvaIgralca.CRNA;
	}
	
	/**
	 * Funkcija odigra dano potezo.
	 * Če je bila poteza možna, vrne true, sicer false.
	 * Pri tem je Poteza(0,0) levo zgoraj, Poteza(1,0) v drugi vrstici,
	 * Poteza(8,8) pa desno spodaj (v 9x9 mreži).
	 * @param poteza Indeksi za potezo (row, col), šteto od 0
	 * @return Če je bila poteza možna
	 */
	public boolean odigraj(Poteza poteza) {
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

	public int sirina() {
		return mreza.sirina();
	}

	public int visina() {
		return mreza.visina();
	}
}
