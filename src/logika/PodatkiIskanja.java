package logika;

import java.util.HashSet;
import java.util.Set;

public class PodatkiIskanja {
	protected Set<Indeks> preiskano;
	
	public PodatkiIskanja() {
		this.preiskano = new HashSet<Indeks>();
	}
	
	public boolean zaPreiskati(Indeks idx) {
		return preiskano.contains(idx);
	}
	
	public void oznaciPreiskano(Indeks idx) {
		preiskano.add(idx);
	}
}
