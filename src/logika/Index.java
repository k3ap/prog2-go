package logika;

import splosno.Poteza;

/**
 * Represents the index in a grid.
 * Entirely equivalent to the `Poteza` class but it's separated for semantic reasons.
 */
public record Index(int i, int j) {
	Index(Poteza poteza) {
		this(poteza.x(), poteza.y());
	}
	
	/**
	 * Convert an index into a Poteza
	 * @return Poteza with the same coordinates
	 */
	public Poteza poteza() {
		return new Poteza(i, j);
	}
}
