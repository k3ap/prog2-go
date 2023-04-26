package logika;

import java.util.HashSet;
import java.util.Set;

/**
 * Helper class that holds all data related to searching.
 */
public class SearchData {
	
	/**
	 * Set of indices which we have already searched.
	 */
	protected Set<Index> searched;

	public SearchData() {
		this.searched = new HashSet<Index>();
	}

	/**
	 * Check, if the given index still needs to be searched.
	 * @param idx The index of interest.
	 * @return true, if we still need to look at this index.
	 */
	public boolean hasNotSearched(Index idx) {
		return !searched.contains(idx);
	}
	
	/**
	 * Mark that the given index has been searched.
	 * @param idx Index
	 */
	public void markSearched(Index idx) {
		searched.add(idx);
	}
	
	/**
	 * Clear all data.
	 * Subclasses should copy this method and call super.simplify.
	 */
	public void clear() {
		searched.clear();
	}
}
