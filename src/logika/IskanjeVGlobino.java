package logika;

import java.util.LinkedList;

/**
 * Abstraktno iskanje v globino
 * Enako kot IskanjeVSirino, le da uporablja sklad namesto vrste
 */
public abstract class IskanjeVGlobino extends Iskanje {
	
	private LinkedList<Indeks> indeksiZaIskanje;
	
	@Override
	protected void oznaciZaPreiskati(Indeks idx) {
		indeksiZaIskanje.add(idx);
	}
	
	@Override
	protected void zacetek(Indeks zacetniIdx) {
		indeksiZaIskanje.add(zacetniIdx);
	}
	
	@Override
	protected boolean jeNaslednji() {
		return !indeksiZaIskanje.isEmpty();
	}
	
	@Override
	protected Indeks naslednji() {
		return indeksiZaIskanje.removeLast();
	}
	
	public IskanjeVGlobino(Mreza mreza, PodatkiIskanja podatki) {
		super(mreza, podatki);
		this.indeksiZaIskanje = new LinkedList<Indeks>();
	}
}
