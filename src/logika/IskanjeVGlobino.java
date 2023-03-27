package logika;

import java.util.LinkedList;

public abstract class IskanjeVGlobino extends Iskanje {
	
	public IskanjeVGlobino(Mreza mreza, PodatkiIskanja podatki) {
		super(mreza, podatki);
		this.indeksiZaIskanje = new LinkedList<Indeks>();
	}

	@Override
	protected Indeks naslednji() {
		return ((LinkedList<Indeks>)indeksiZaIskanje).removeLast();
	}

}
