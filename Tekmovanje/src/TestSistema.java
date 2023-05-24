import inteligenca.Inteligenca;
import logika.Igra;

public class TestSistema {
	
	public static void main(String[] args) {
		Igra igra = new Igra();
		Inteligenca i1 = new Inteligenca();
		Inteligenca i2 = new Inteligenca();
		
		System.out.println(i1.ime());
		System.out.println(i2.ime());
		
		int poteze = 0;
		double zacetek = System.nanoTime();
		
		Inteligenca naslednji = i1;
		while (igra.winner() == null) {
			igra.odigraj(naslednji.izberiPotezo(igra));
			poteze++;
			if (naslednji == i1) {
				naslednji = i2;
			} else {
				naslednji = i1;
			}
		}
		
		double konec = System.nanoTime();
		System.out.format("avg time: %f\n", (konec-zacetek) / 1e9 / poteze);
	}
}
