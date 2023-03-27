package logika;

import java.util.LinkedList;

public abstract class IskanjeVSirino extends Iskanje {
	
	public IskanjeVSirino(Mreza mreza, PodatkiIskanja podatki) {
		super(mreza, podatki);
		this.indeksiZaIskanje = new LinkedList<Indeks>();
	}

	@Override
	protected Indeks naslednji() {
		return ((LinkedList<Indeks>)indeksiZaIskanje).removeFirst();
	}

}
