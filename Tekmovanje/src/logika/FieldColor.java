package logika;

/**
 * The color of a field in the grid, either EMPTY, BLACK, or WHITE.
 */
public enum FieldColor {
	EMPTY, BLACK, WHITE;
	
	/**
	 * Turns a PlayerColor into the corresponding FieldColor.
	 * @param color Color of the player.
	 * @return FieldColor, equivalent to the player color.
	 */
	public static FieldColor playersColor(PlayerColor color) {
		switch (color) {
		case BLACK:
			return FieldColor.BLACK;
		case WHITE:
			return FieldColor.WHITE;
		}
		assert false;
		return null;
	}
}