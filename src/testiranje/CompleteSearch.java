package testiranje;

import java.io.FileNotFoundException;
import java.io.IOException;

import logika.BarvaIgralca;
import logika.BarvaPolja;
import logika.Indeks;
import logika.Mreza;

/**
 * Popolna preiskava nekega stanja polja v namen testiranja
 */
public class CompleteSearch {
	
	private record DveStevili(int a, int b) {}
	
	private static DveStevili search(Mreza mreza, BarvaIgralca igralec, int globina, boolean doOutput) {
		int zmage = 0;
		int zgube = 0;
		for (Indeks idx : mreza.prostaPolja()) {
			mreza.postaviBarvo(idx, BarvaPolja.igralcevaBarva(igralec));
			if (doOutput)
				mreza.izpisi();
			if (mreza.jeBarvaIzgubila(BarvaPolja.igralcevaBarva(BarvaIgralca.novaBarva(igralec)))) {
				if (doOutput)
					System.out.println("Zmaga za " + igralec);
				zmage++;
			} else if (mreza.jeBarvaIzgubila(BarvaPolja.igralcevaBarva(igralec))) {
				if (doOutput)
					System.out.println("Izguba za " + igralec);
				zgube++;
			} else {
				if (doOutput)
					System.out.format("Branchpoint (depth=%d).\n", globina);
				DveStevili ns = search(mreza, BarvaIgralca.novaBarva(igralec), globina+1, doOutput);
				zmage += ns.b;
				zgube += ns.a;
				if (doOutput)
					System.out.format("Branchpoint (depth=%d) backtrack. Zmage/izgube za %s: %d/%d\n", globina, igralec.toString(), ns.b, ns.a);
			}
			mreza.postaviBarvo(idx, BarvaPolja.PRAZNA);
		}
		return new DveStevili(zmage, zgube);
	}
	
	/*private static final int N = 9;
	private static final Indeks[] zacetnePoteze = {
			new Indeks(0, 1), new Indeks(1, 2), new Indeks(1, 1), new Indeks(2, 1),
			new Indeks(2, 2), new Indeks(2, 3), new Indeks(3, 2)
	};*/

	public static void main(String[] args) {
		/*Mreza mreza = new Mreza(N, N);
		BarvaIgralca barva = BarvaIgralca.CRNA;
		for (Indeks idx : zacetnePoteze) {
			mreza.postaviBarvo(idx, BarvaPolja.igralcevaBarva(barva));
			barva = BarvaIgralca.novaBarva(barva);
		}
		*/
		Mreza mreza = RandomMrezaGenerator.nakljucnaMreza(9, 0.7);
		BarvaIgralca barva = BarvaIgralca.CRNA;
		mreza.izpisi();
		mreza.izpisiVDatoteko("autogen-primeri/najnovejsi.m9");
		System.out.println();
		System.out.format("minSS za crno: %d\n", mreza.minSteviloSvobodBarve(BarvaPolja.CRNA));
		System.out.format("minSS za belo: %d\n", mreza.minSteviloSvobodBarve(BarvaPolja.BELA));
		DveStevili ns = search(mreza, barva, 0, false);
		System.out.format("Zmage/izgube za %s: %d/%d\n", barva.toString(), ns.a, ns.b);
		
		/*Mreza mreza = null;
		try {
			mreza = Mreza.preberiIzDatoteke("mreza.m9", 9);
		} catch (Exception e) {
			System.out.println("error");
			System.exit(1);
		}
		mreza.izpisi();*/
	}

}
