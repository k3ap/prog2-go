package splosno;

public record Poteza (int x, int y) implements Comparable<Poteza> {

	@Override
	public int compareTo(Poteza other) {
		if (x < other.x) return -1;
		if (x > other.x) return 1;
		if (y < other.y) return -1;
		if (y > other.y) return 1;
		return 0;
	}
}
	