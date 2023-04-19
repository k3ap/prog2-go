package logika;

/**
 * The color of a field in the grid, either EMPTY, BLACKFIELD, or WHITEFIELD.
 */
public enum FieldColor {
	EMPTY, BLACKFIELD, WHITEFIELD;
	
	/**
	 * Turns a PlayerColor into the corresponding FieldColor.
	 * @param color Color of the player.
	 * @return FieldColor, equivalent to the player color.
	 */
	public static FieldColor playersColor(PlayerColor color) {
		switch (color) {
		case BLACK:
			return FieldColor.BLACKFIELD;
		case WHITE:
			return FieldColor.WHITEFIELD;
		}
		assert false;
		return null;
	}
}