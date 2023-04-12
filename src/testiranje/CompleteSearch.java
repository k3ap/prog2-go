package testiranje;

import logika.BarvaIgralca;
import logika.BarvaPolja;
import logika.Indeks;
import logika.Mreza;

/**
 * Popolna preiskava nekega stanja polja v namen testiranja
 */
public class CompleteSearch {
	
	private record DveStevili(int a, int b) {}
	
	private static DveStevili search(Mreza mreza, BarvaIgralca igralec, int globina) {
		int zmage = 0;
		int zgube = 0;
		for (Indeks idx : mreza.prostaPolja()) {
			mreza.postaviBarvo(idx, BarvaPolja.igralcevaBarva(igralec));
			mreza.izpisi();
			if (mreza.jeBarvaIzgubila(BarvaPolja.igralcevaBarva(BarvaIgralca.novaBarva(igralec)))) {
				System.out.println("Zmaga za " + igralec);
				zmage++;
			} else if (mreza.jeBarvaIzgubila(BarvaPolja.igralcevaBarva(igralec))) {
				System.out.println("Izguba za " + igralec);
				zgube++;
			} else {
				System.out.format("Branchpoint (depth=%d).\n", globina);
				DveStevili ns = search(mreza, BarvaIgralca.novaBarva(igralec), globina+1);
				zmage += ns.b;
				zgube += ns.a;
				System.out.format("Branchpoint (depth=%d) backtrack. Zmage/izgube za %s: %d/%d\n", globina, igralec.toString(), ns.b, ns.a);
			}
			mreza.postaviBarvo(idx, BarvaPolja.PRAZNA);
		}
		return new DveStevili(zmage, zgube);
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
