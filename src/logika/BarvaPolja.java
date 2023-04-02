package logika;

/**
 * Predstavitev barve polja v mreži.
 */
public enum BarvaPolja {
	PRAZNA, CRNA, BELA;
	
	/**
	 * Pretvori barvo igralca v barvo polja 
	 * @param barva Barva igralca.
	 * @return Barva polja, enaka igralčevi.
	 */
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