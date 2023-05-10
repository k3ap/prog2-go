package vodja;

/**
 * Type of game that is played in regards to which sides are controlled by the computer.
 * @see #HUMCOM
 * @see #COMHUM
 * @see #HUMHUM
 * @see #COMCOM
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
	 * Humans control both sides.
	 */
	HUMHUM,
	/**
	 * Computers control both sides.
	 */
	COMCOM;
	
	/**
	 * The GameType is one of computer-human or human-computer
	 * @return the truthfulness of the above statement
	 */
	public boolean mixedGame() {
		return this == COMHUM || this == HUMCOM;
	}
}
