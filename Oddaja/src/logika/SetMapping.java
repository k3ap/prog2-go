package logika;

/**
 * Helper interface for UFDS's mapping capability
 */
public interface SetMapping {
	
	/**
	 * Merge the other object's data with this object's data, returning the result 
	 * @param other
	 */
	public SetMapping joinWith(SetMapping other);
}
