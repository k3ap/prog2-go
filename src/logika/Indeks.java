package logika;

import splosno.Poteza;

/**
 * Predstavlja indeks v mreži.
 * Povsem ekvivalentno razredu splosno.Poteza, vendar je ločen razred iz semantičnih razlogov.
 */
public record Indeks(int i, int j) {
	Indeks(Poteza poteza) {
		this(poteza.x(), poteza.y());
	}
}
