package logika;

public enum GoGameType {
	FCGO, GO;
	
	public String toString() {
		switch (this) {
		case FCGO:
			return "Prvi zavzem Go";
		case GO:
			return "Go";
		default:
			return null;
		}
	}
}
