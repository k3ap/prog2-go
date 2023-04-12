package testiranje;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import logika.BarvaPolja;
import logika.Indeks;
import logika.Mreza;

public class RandomMrezaGenerator {
	
	private static Random RNG = new Random();
	
	public static Indeks nakljucniIdx(Set<Indeks> taken, int n) {
		Indeks idx = new Indeks(RNG.nextInt(n), RNG.nextInt(n));
		while (taken.contains(idx)) {
			idx = new Indeks(RNG.nextInt(n), RNG.nextInt(n));
		}
		return idx;
	}
	
	public static BarvaPolja nakljucnaBarva() {
		if (RNG.nextInt() % 2 == 0) {
			return BarvaPolja.BELA;
		} else {
			return BarvaPolja.CRNA;
		}
	}
	
	public static Mreza nakljucnaMreza(int n, double fullness) {
		Mreza mreza = new Mreza(n, n);
		HashSet<Indeks> taken = new HashSet<Indeks>();
		int polja = n*n;
		int c = 0;
		while (c < fullness * polja) {
			c++;
			Indeks idx = nakljucniIdx(taken, n);
			BarvaPolja barva = nakljucnaBarva();
			mreza.postaviBarvo(idx, barva);
			taken.add(idx);
		}
		
		return mreza;
	}
}
