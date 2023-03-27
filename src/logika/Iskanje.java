package logika;

import java.util.Queue;

public abstract class Iskanje {
	protected Queue<Indeks> indeksiZaIskanje;
	protected PodatkiIskanja podatki;
	protected Mreza mreza;
	
	// Vrni naslednji element, ki ga moramo preiskati, 
	// ter ga odstrani iz indeksiZaIskanje
	protected abstract Indeks naslednji();
	
	protected abstract void vstopnaAkcija(Indeks idx);
	protected abstract void izstopnaAkcija(Indeks idx);
	
	private final int sosedi[][] = new int[][] {
		{-1, 0},
		{1, 0},
		{0, 1},
		{0, -1}
	};
	
	private void korakIskanja() {
		Indeks idx = naslednji();
		vstopnaAkcija(idx);
		
		// Preišči vse sosede trenutnega vozlišča
		for (int x = 0; x < 4; x++) {
			int i = idx.i() + sosedi[x][0];
			int j = idx.j() + sosedi[x][1];
			
			if (i < 0 || j < 0 || i >= mreza.visina() || j >= mreza.sirina())
				continue;
			
			Indeks novIdx = new Indeks(i, j);
			podatki.oznaciPreiskano(novIdx);
		}
		
		izstopnaAkcija(idx);
	}
	
	public Iskanje(Mreza mreza, PodatkiIskanja podatki) {
		this.mreza = mreza;
		this.podatki = podatki;
	}
	
	public void pozeni(Indeks zacetniIdx) {
		indeksiZaIskanje.add(zacetniIdx);
		while (!indeksiZaIskanje.isEmpty()) {
			korakIskanja();
		}
	}
}
