package logika;

public class Mreza {
	public static enum BarvaPolja {
		PRAZNA, CRNA, BELA
	}
	
	private BarvaPolja mreza[][];
	
	public Mreza(int visina, int sirina) {
		mreza = new BarvaPolja[visina][sirina];
	}
}
