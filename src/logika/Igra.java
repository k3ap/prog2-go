package logika;

import java.util.ArrayList;
import java.util.List;

import splosno.Poteza;

/**
 * Predstavlja trenutno stanje igre.
 */
public class Igra {
	
	private Mreza mreza;
	private BarvaIgralca naslednjiIgralec;
	private BarvaIgralca zmagovalec;

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
		zmagovalec = null; // null pomeni, da nihče ni zmagal
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
		BarvaIgralca taIgralec = naslednjiIgralec;
		naslednjiIgralec = BarvaIgralca.novaBarva(naslednjiIgralec);
		if (mreza.jeBarvaIzgubila( BarvaPolja.igralcevaBarva(naslednjiIgralec) )) {
			// taIgralec je igral zmagujočo potezo
			zmagovalec = taIgralec;
			return true;
		}
		if (mreza.jeBarvaIzgubila( BarvaPolja.igralcevaBarva( BarvaIgralca.novaBarva(naslednjiIgralec) ) )) {
			// ta igralec je naredil samomorilno potezo
			zmagovalec = naslednjiIgralec;
			return true;
		}
		return true;
	}

	public int sirina() { return mreza.sirina(); }
	public int visina() { return mreza.visina(); }
	public BarvaPolja barvaPolja(Indeks idx) { return mreza.barvaPolja(idx); }
	public BarvaIgralca naPotezi() { return naslednjiIgralec; }
	public BarvaIgralca zmagovalec() { return zmagovalec; }
	
	/**
	 * Poišči veljavne poteze za naslednjega igralca
	 * @return Seznam veljavnih potez
	 */
	public List<Poteza> veljavnePoteze() {
		ArrayList<Poteza> res = new ArrayList<>();
		for (Indeks idx : mreza.prostaPolja()) {
			res.add(idx.poteza());
		}
		return res;
	}

}
