package inteligenca;

import logika.BarvaIgralca;
import logika.Igra;
import logika.Indeks;
import logika.Mreza;
import splosno.Poteza;

public class MinMaxDolocevalecPoteze extends DolocevalecPoteze {
	
	private int maxGlobina;
	
	public MinMaxDolocevalecPoteze(int maxGlobina) {
		super();
		this.maxGlobina = maxGlobina;
	}
	
	private double oceniStanje(Mreza mreza, BarvaIgralca naslednjiIgralec) {
		int mojeTocke = mreza.minSteviloSvobodBarve(naslednjiIgralec.polje());
		int njegoveTocke = mreza.minSteviloSvobodBarve(naslednjiIgralec.naslednji().polje());
		return mojeTocke - njegoveTocke;
	}
	
	private double minMaxOceni(Mreza mreza, int globina, BarvaIgralca igralec) {
		if (mreza.jeBarvaIzgubila(igralec.naslednji().polje())) {
			return Double.POSITIVE_INFINITY;
		} else if (mreza.jeBarvaIzgubila(igralec.polje())) {
			return Double.NEGATIVE_INFINITY;
		}
		if (globina == maxGlobina) return oceniStanje(mreza, igralec);
		double najbolje = 0;
		for (Indeks poteza : mreza.prostaPolja()) {
			Mreza nova = mreza.deepcopy();
			nova.postaviBarvo(poteza, igralec.polje());
			double vrednost;
			if (mreza.jeBarvaIzgubila(igralec.naslednji().polje())) {
				vrednost = Double.POSITIVE_INFINITY;
			} else if (mreza.jeBarvaIzgubila(igralec.polje())) {
				vrednost = Double.NEGATIVE_INFINITY;
			} else {
				vrednost = minMaxOceni(nova, globina+1, igralec.naslednji());
			}
			najbolje = Math.max(-najbolje, vrednost);
		}
		return najbolje;
	}

	@Override
	public String ime() {
		return "betago-minmax-"+maxGlobina;
	}

	@Override
	public Poteza dolociPotezo(Igra igra) {
		Poteza najboljsa = null;
		double najocena = 0;
		for (Poteza poteza : igra.veljavnePoteze()) {
			double ocena = minMaxOceni(igra.mreza, 1, igra.naslednjiIgralec);
			if (najboljsa == null || najocena < ocena) {
				najboljsa = poteza;
			}
		}
		return najboljsa;
	}

}
