package logika;

public enum BarvaIgralca {
	CRNA, BELA;
	
	public static BarvaIgralca novaBarva(BarvaIgralca staraBarva) {
		switch(staraBarva) {
		case CRNA: return BELA;
		case BELA: return CRNA;
		}
		assert false;
		return null;
	}
}
