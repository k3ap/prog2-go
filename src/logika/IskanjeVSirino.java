package logika;

import java.util.LinkedList;

/**
 * Abstraktno iskanje v širino
 * Za uporabo ustvari podrazred, ki implementira ostale metode iskanja
 * @see Mreza.java
 */
public abstract class IskanjeVSirino extends Iskanje {
	
	private LinkedList<Indeks> indeksiZaIskanje;
	
	@Override
	protected void oznaciZaPreiskati(Indeks idx) {
		indeksiZaIskanje.add(idx);
	}
	
	@Override
	protected void zacetek(Indeks zacetniIdx) {
		indeksiZaIskanje.add(zacetniIdx);
		podatki.oznaciPreiskano(zacetniIdx);
	}
	
	@Override
	protected boolean jeNaslednji() {
		return !indeksiZaIskanje.isEmpty();
	}
	
	@Override
	protected Indeks naslednji() {
		return indeksiZaIskanje.removeFirst();
	}
	
	public IskanjeVSirino(Mreza mreza, PodatkiIskanja podatki) {
		super(mreza, podatki);
		this.indeksiZaIskanje = new LinkedList<Indeks>();
	}
}
