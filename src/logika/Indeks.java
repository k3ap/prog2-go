package logika;

import splosno.Poteza;

public record Indeks(int i, int j) {
	Indeks(Poteza poteza) {
		this(poteza.x(), poteza.y());
	}
}
