package inteligenca;

import logika.BarvaIgralca;
import logika.Igra;
import logika.Indeks;
import logika.Mreza;
import splosno.Poteza;

public class AlphaBetaDolocevalecPoteze extends DolocevalecPoteze {
	
	private int maxGlobina;
	private OcenjevalecMreze ocenjevalec;
	
	public AlphaBetaDolocevalecPoteze(int maxGlobina, OcenjevalecMreze ocenjevalec) {
		super();
		this.maxGlobina = maxGlobina;
		this.ocenjevalec = ocenjevalec;
	}

	@Override
	public String ime() {
		return "betago-alphabeta-"+maxGlobina+"-"+ocenjevalec.ime;
	}
	
	private double alphabetaOceni(Mreza mreza, int globina, BarvaIgralca igralec, double alpha, double beta) {
		if (mreza.jeBarvaIzgubila(igralec.naslednji().polje())) {
			return Double.POSITIVE_INFINITY;
		} else if (mreza.jeBarvaIzgubila(igralec.polje())) {
			return Double.NEGATIVE_INFINITY;
		}
		
		if (globina >= maxGlobina) return ocenjevalec.oceniMrezo(mreza, igralec);
		
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
				vrednost = -alphabetaOceni(nova, globina+1, igralec.naslednji(), -beta, -alpha);
			}
			if (najbolje == null || vrednost > najbolje)
				najbolje = vrednost;
			
			if (alpha < vrednost) 
				alpha = vrednost;
			
			if (beta < alpha) break;
		}
		return najbolje;
	}

	@Override
	public Poteza dolociPotezo(Igra igra) {
		Poteza najboljsa = null;
		double najocena = 0;
		double alpha = Double.NEGATIVE_INFINITY, beta = Double.POSITIVE_INFINITY;
		for (Poteza poteza : igra.veljavnePoteze()) {
			Mreza nova = igra.mreza.deepcopy();
			nova.postaviBarvo(new Indeks(poteza), igra.naslednjiIgralec.polje());
			double ocena = -alphabetaOceni(nova, 1, igra.naslednjiIgralec.naslednji(), -beta, -alpha);
			if (najboljsa == null || najocena < ocena) {
				najboljsa = poteza;
				najocena = ocena;
			}
			if (alpha < ocena)
				alpha = ocena;
		}
		return najboljsa;
	}

}
