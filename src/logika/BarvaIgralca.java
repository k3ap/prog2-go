package logika;

/**
 * Barva igralca. Se razlikuje od barve polja v tem, da je polje lahko prazno.
 */
public enum BarvaIgralca {
	CRNA, BELA;
	
	/**
	 * Vrne komplement staraBarva.
	 * @param staraBarva Trenutna barva aktivnega igralca.
	 * @return Barva naslednjega aktivnega igralca.
	 */
	public static BarvaIgralca novaBarva(BarvaIgralca staraBarva) {
		switch(staraBarva) {
		case CRNA: return BELA;
		case BELA: return CRNA;
		}
		assert false;
		return null;
	}
}
