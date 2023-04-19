package logika;

import splosno.Poteza;

/**
 * Represents the index in a grid.
 * Entirely equivalent to the `Poteza` class but it's separated due to semantic reasons.
 */
public record Index(int i, int j) {
	Index(Poteza poteza) {
		this(poteza.x(), poteza.y());
	}
}
