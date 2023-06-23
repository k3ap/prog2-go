package logika;

/**
 * What game we're playing, either GO or FCGO.
 */
public enum GoGameType {
	FCGO, GO;
	
	public String toString() {
		switch (this) {
		case FCGO:
			return "First capture Go";
		case GO:
			return "Go";
		default:
			return null;
		}
	}
}
