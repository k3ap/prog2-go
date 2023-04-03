package logika;

import java.util.HashSet;
import java.util.Set;

/**
 * Pomožni razred, ki hrani vse podatke, povezane z iskanjem
 */
public class PodatkiIskanja {
	
	/**
	 * Množica indeksov, ki smo jih že preiskali
	 */
	protected Set<Indeks> preiskano;

	public PodatkiIskanja() {
		this.preiskano = new HashSet<Indeks>();
	}

	/**
	 * Preveri, če je dani indeks še potrebno preiskati
	 * @param idx Indeks, za katerega nas zanima
	 * @return true, če je indeks še treba preiskati
	 */
	public boolean zaPreiskati(Indeks idx) {
		return !preiskano.contains(idx);
	}
	
	/**
	 * Označi, da je dani indeks že preiskan
	 * @param idx Indeks
	 */
	public void oznaciPreiskano(Indeks idx) {
		preiskano.add(idx);
	}
	
	/**
	 * Ponastavi celotno zbirko podatkov.
	 * Podrazredi naj to metodo prepišejo in kličejo super.ponastavi()
	 */
	public void ponastavi() {
		preiskano.clear();
	}
}
