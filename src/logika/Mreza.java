package logika;

public class Mreza {
	public static enum BarvaPolja {
		PRAZNA, CRNA, BELA
	}
	
	private BarvaPolja mreza[][];
	private int visina, sirina;
	
	public Mreza(int visina, int sirina) {
		mreza = new BarvaPolja[visina][sirina];
		this.visina = visina;
		this.sirina = sirina;
	}
	
	public int visina() { return visina; }
	public int sirina() { return sirina; }
}
