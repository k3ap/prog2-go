package inteligenca;

import logika.BarvaIgralca;
import logika.Mreza;

public class PrimitivniOcenjevalecMreze extends OcenjevalecMreze {
	
	public PrimitivniOcenjevalecMreze() {
		super("primitivni");
	}

	@Override
	double oceniMrezo(Mreza mreza, BarvaIgralca igralec) {
		int mojeTocke = mreza.minSteviloSvobodBarve(igralec.polje());
		int njegoveTocke = mreza.minSteviloSvobodBarve(igralec.naslednji().polje());
		return mojeTocke - njegoveTocke;
	}
}
