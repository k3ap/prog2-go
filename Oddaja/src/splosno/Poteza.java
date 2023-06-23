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
	
	/**
	 * Does this object represent a pass?
	 * @return true iff this is a pass move.
	 */
	public boolean isPass() {
		return x == -1 && y == -1;
	}
	
	/**
	 * Get a Poteza object representing a "pass" move,
	 * only valid for Go. 
	 * @return A pass Poteza.
	 */
	public static Poteza pass() {
		return new Poteza(-1, -1);
	}
}
	