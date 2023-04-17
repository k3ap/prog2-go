package logika;

import splosno.Poteza;

/**
 * Predstavlja indeks v mreži.
 * Povsem ekvivalentno razredu splosno.Poteza, vendar je ločen razred iz semantičnih razlogov.
 */
public record Indeks(int i, int j) {
	public Indeks(Poteza poteza) {
		this(poteza.x(), poteza.y());
	}
	
	/**
	 * Pretvori indeks v potezo
	 * @return Poteza z enakimi koordinatami
	 */
	public Poteza poteza() {
		return new Poteza(i, j);
	}
}
