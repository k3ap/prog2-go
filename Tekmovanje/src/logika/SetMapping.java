package logika;

/**
 * Helper interface for UFDS's mapping capability
 */
public interface SetMapping {
	
	/**
	 * Merge the other object's data with this object's data, invalidating the other object 
	 * @param other The object whose data we're merging into this' data
	 */
	public void joinWith(SetMapping other);
}
