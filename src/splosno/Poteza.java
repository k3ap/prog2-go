package splosno;

public record Poteza (int x, int y, boolean isPass) {
	public Poteza(int x, int y) {
		this(x, y, false);
	}
}
	