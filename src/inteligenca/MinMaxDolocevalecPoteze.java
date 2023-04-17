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
		//System.out.format("svobode (%s): %d - %d\n", naslednjiIgralec, mojeTocke, njegoveTocke);
		return mojeTocke - njegoveTocke;
	}
	
	private double minMaxOceni(Mreza mreza, int globina, BarvaIgralca igralec) {
		if (mreza.jeBarvaIzgubila(igralec.naslednji().polje())) {
			return Double.POSITIVE_INFINITY;
		} else if (mreza.jeBarvaIzgubila(igralec.polje())) {
			return Double.NEGATIVE_INFINITY;
		}
		if (globina == maxGlobina) return oceniStanje(mreza, igralec);
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
				//System.out.println("rekurzija");
				//nova.izpisi();
				vrednost = -minMaxOceni(nova, globina+1, igralec.naslednji());
			}
			if (najbolje == null || vrednost > najbolje)
				najbolje = vrednost;
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
			Mreza nova = igra.mreza.deepcopy();
			nova.postaviBarvo(new Indeks(poteza), igra.naslednjiIgralec.polje());
			double ocena = -minMaxOceni(nova, 1, igra.naslednjiIgralec.naslednji());
			//System.out.format("Igralec %s oceni %d,%d za %f\n", igra.naslednjiIgralec.toString(), poteza.x(), poteza.y(), ocena);
			if (najboljsa == null || najocena < ocena) {
				najboljsa = poteza;
				najocena = ocena;
			}
		}
		return najboljsa;
	}

}
