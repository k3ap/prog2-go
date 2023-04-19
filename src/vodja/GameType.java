package vodja;

/**
 * Type of game that is played in regards to which sides are controlled by the computer.
 * @see #HUMCOM
 * @see #COMHUM
 * @see #HUMHUM
 */
public enum GameType {
	/**
	 * The human is white, the computer is black.
	 */
	HUMCOM,
	/**
	 * The computer is white, the human is black.
	 */
	COMHUM,
	/**
	 * Humans cotroll both sides.
	 */
	HUMHUM,
}
