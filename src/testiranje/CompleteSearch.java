package testiranje;

import logika.BarvaIgralca;
import logika.BarvaPolja;
import logika.Indeks;
import logika.Mreza;

/**
 * Popolna preiskava nekega stanja polja v namen testiranja
 */
public class CompleteSearch {
	
	private static void search(Mreza mreza, BarvaIgralca igralec, int globina) {
		for (Indeks idx : mreza.prostaPolja()) {
			mreza.postaviBarvo(idx, BarvaPolja.igralcevaBarva(igralec));
			mreza.izpisi();
			if (mreza.jeBarvaIzgubila(BarvaPolja.igralcevaBarva(BarvaIgralca.novaBarva(igralec)))) {
				System.out.println("Zmaga za " + igralec);
			} else if (mreza.jeBarvaIzgubila(BarvaPolja.igralcevaBarva(igralec))) {
				System.out.println("Izguba za " + igralec);
			} else {
				System.out.format("Branchpoint (depth=%d).", globina);
				search(mreza, BarvaIgralca.novaBarva(igralec), globina+1);
			}
			mreza.postaviBarvo(idx, BarvaPolja.PRAZNA);
		}
	}
	
	private static final int N = 4;
	private static final Indeks[] zacetnePoteze = {
			new Indeks(0, 1), new Indeks(1, 2), new Indeks(1, 1), new Indeks(2, 1),
			new Indeks(2, 2), new Indeks(2, 3), new Indeks(3, 2)
	};

	public static void main(String[] args) {
		Mreza mreza = new Mreza(N, N);
		BarvaIgralca barva = BarvaIgralca.CRNA;
		for (Indeks idx : zacetnePoteze) {
			mreza.postaviBarvo(idx, BarvaPolja.igralcevaBarva(barva));
			barva = BarvaIgralca.novaBarva(barva);
		}
		mreza.izpisi();
		System.out.println();
		search(mreza, barva, 0);
	}

}
