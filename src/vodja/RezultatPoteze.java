package vodja;

/**
 * Odgovor razreda VodenaIgra razredu Platno o tem, kako je potekla
 * izbrana poteza.
 * @see #IGRAMO
 * @see #POCAKAJ
 * @see #NEVELJAVNA
 * @see #NAPAKA
 */
public enum RezultatPoteze {
	/**
	 * Če je tip igre človek - človek, bo takoj ko bo en naredil potezo lahko drug naredil potezo.
	 */
	IGRAMO,
	/**
	 * Če je katerikoli od igralcev računalnik, mora človek, ko naredi potezo,
	 * počakati, da se računalnik odloči preden spet igra.
	 */
	POCAKAJ,
	/**
	 * Ta poteza je bila neveljavna, naredi kako drugo.
	 */
	NEVELJAVNA,
	/**
	 * Inteligenca za izbiranje potez se je sesula.
	 */
	NAPAKA,
}
