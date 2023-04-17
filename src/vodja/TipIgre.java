package vodja;

/**
 * Vrsta igre, glede na to katere barve je AI, ali pa sta oba igralca človeka.
 * @see #CLORAC
 * @see #RACCLO
 * @see #CLOCLO
 */
public enum TipIgre {
	/**
	 * Človek je črn, računalnik pa bel.
	 */
	CLORAC,
	/**
	 * Računalnik je bel, človek pa črn.
	 */
	RACCLO,
	/**
	 * Človek nadzira črnega in belega.
	 */
	CLOCLO,
}
