package inteligenca;

import logika.BarvaIgralca;
import logika.Igra;
import logika.Indeks;
import logika.Mreza;
import splosno.Poteza;

/**
 * Določevalec poteze, ki uporablja algoritem MiniMax
 */
public class MiniMaxDolocevalecPoteze extends DolocevalecPoteze {
	
	/**
	 * Največja globina, do katere razvijamo drevo; na tej globini že 
	 * uporabimo ocenjevalno funkcijo
	 */
	private int maxGlobina;
	
	public MiniMaxDolocevalecPoteze(int maxGlobina) {
		super();
		this.maxGlobina = maxGlobina;
	}
	
	/**
	 * Ocenjevalna funkcija za trenutno stanje mreže.
	 * Oceni naj najboljši možni izid za igralca (ki je trenutno na potezi)
	 * @param mreza Trenutno stanje mreže
	 * @param igralec Igralec, ki je sedaj na vrsti
	 * @return Ocena najboljšega izida igre. Večja številka pomeni bolje.
	 */
	private double oceniStanje(Mreza mreza, BarvaIgralca igralec) {
		// Trenutno je ocena preprosta razlika med številom svobod
		int mojeTocke = mreza.minSteviloSvobodBarve(igralec.polje());
		int njegoveTocke = mreza.minSteviloSvobodBarve(igralec.naslednji().polje());
		return mojeTocke - njegoveTocke;
	}
	
	/**
	 * Rekurzivno oceni najboljši rezultat za igralca pri danem stanju mreže,
	 * z uporabo algoritma minimax
	 * @param mreza Trenutno stanje mreže
	 * @param globina Trenutna globina iskanja (največ maxGlobina)
	 * @param igralec Igralec, ki je trenutno na potezi
	 * @return Številska vrednost, ki pove, kako dobro je stanje za trenutnega igralca. Večja številka pomeni boljši položaj za trenutnega igralca.
	 */
	private double miniMaxOceni(Mreza mreza, int globina, BarvaIgralca igralec) {
		
		// če je kdo že izgubil, nimamo nič za preverjati
		if (mreza.jeBarvaIzgubila(igralec.naslednji().polje())) {
			return Double.POSITIVE_INFINITY;
		} else if (mreza.jeBarvaIzgubila(igralec.polje())) {
			return Double.NEGATIVE_INFINITY;
		}
		
		if (globina >= maxGlobina) return oceniStanje(mreza, igralec);
		
		Double najbolje = null;
		for (Indeks poteza : mreza.prostaPolja()) {
			Mreza nova = mreza.deepcopy();
			nova.postaviBarvo(poteza, igralec.polje());
			double vrednost;
			if (mreza.jeBarvaIzgubila(igralec.naslednji().polje())) {
				vrednost = Double.POSITIVE_INFINITY;
			} else if (mreza.jeBarvaIzgubila(igralec.polje())) {
				vrednost = Double.NEGATIVE_INFINITY;
			} else {
				vrednost = -miniMaxOceni(nova, globina+1, igralec.naslednji());
			}
			if (najbolje == null || vrednost > najbolje)
				najbolje = vrednost;
		}
		return najbolje;
	}

	@Override
	public String ime() {
		return "betago-minimax-"+maxGlobina;
	}

	@Override
	public Poteza dolociPotezo(Igra igra) {
		Poteza najboljsa = null;
		double najocena = 0;
		for (Poteza poteza : igra.veljavnePoteze()) {
			Mreza nova = igra.mreza.deepcopy();
			nova.postaviBarvo(new Indeks(poteza), igra.naslednjiIgralec.polje());
			double ocena = -miniMaxOceni(nova, 1, igra.naslednjiIgralec.naslednji());
			if (najboljsa == null || najocena < ocena) {
				najboljsa = poteza;
				najocena = ocena;
			}
		}
		return najboljsa;
	}

}
