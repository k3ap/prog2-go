package vodja;

/**
 * An answer of the ManagedGame class to the Panel class regarding the played move.
 * @see #PLAY
 * @see #WAIT
 * @see #INVALID
 * @see #ERROR
 * @see #BLACKWINS
 * @see #WHITEWINS
 */
public enum MoveResult {
	/**
	 * When it's a player's turn to make a move.
	 */
	PLAY,
	/**
	 * The player should wait as the computer is processing it's next move. 
	 */
	WAIT,
	/**
	 * The move submitted by the player was invalid, they must chose some other move.
	 */
	INVALID,
	/**
	 * The code that calculates moves has thrown an error.
	 */
	ERROR,
	/**
	 * The black player has won.
	 */
	BLACKWINS,
	/**
	 * The white player has won.
	 */
	WHITEWINS,
	/**
	 * Both sides are computers.
	 */
	ALLCOMPUTERS;
	
	public boolean isWonGame() {
		return this == BLACKWINS || this == WHITEWINS;
	}
}
