package logika;

/**
 * Color of a player. Different than FieldColor since a field can also be empty.
 */
public enum PlayerColor {
	BLACK, WHITE;
	
	/**
	 * Returns the complement of oldColor.
	 * @param oldColor The color to complement.
	 * @return Barva The opposite color.
	 */
	public static PlayerColor newColor(PlayerColor oldColor) {
		switch(oldColor) {
		case BLACK: return WHITE;
		case WHITE: return BLACK;
		}
		assert false;
		return null;
	}
}
