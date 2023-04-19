package inteligenca;

import logika.BarvaIgralca;
import logika.Mreza;

public abstract class OcenjevalecMreze {
	public final String ime;
	
	public OcenjevalecMreze(String ime) {
		this.ime = ime;
	}
	
	abstract double oceniMrezo(Mreza mreza, BarvaIgralca igralec);
}
