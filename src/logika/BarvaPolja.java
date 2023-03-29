package logika;

public enum BarvaPolja {
	PRAZNA, CRNA, BELA;
		
	public static BarvaPolja igralcevaBarva(BarvaIgralca barva) {
		switch (barva) {
		case CRNA:
			return BarvaPolja.CRNA;
		case BELA:
			return BarvaPolja.BELA;
		}
		assert false;
		return null;
	}
}