package vodja;

/**
 * Calculated outcome of a finished game.
 */
public enum GameOutcome {
	/**
	 * When one side is a computer and the other is a human.
	 * The player has won.
	 */
	HUMWON,
	/**
	 * When one side is a computer and the other is a human.
	 * The computer has won.
	 */
	COMWON,
	/**
	 * When both sides are computers.
	 * The black player has won.
	 */
	COMBLACKWON,
	/**
	 * When both sides are computers.
	 * The white player has won.
	 */
	COMWHITEWON,
	/**
	 * When both sides are humans.
	 * The black player has won.
	 */
	HUMBLACKWON,
	/**
	 * When both sides are humans.
	 * The white player has won.
	 */
	HUMWHITEWON,
}
