package logika;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Basic implementation of a unionfind datastructure
 * @param <T> The type to be stored. We assume inserted elements are different according to the .equals() method of T
 */
public class UFDS<T, M extends SetMapping> {
	/**
	 * Keeps track of the parent of a node
	 */
	private Map<T, T> parent;
	
	/**
	 * Keeps track of the size of a certain component.
	 * Only valid for top-level nodes   
	 */
	private Map<T, Integer> sizes;
	
	/**
	 * Upper bound for tree depth, used to ensure performance
	 * Only valid for top-level nodes
	 */
	private Map<T, Integer> rank;
	
	/**
	 * A map keeping the top-level nodes and their mapped properties
	 */
	private Map<T, M> toplevels;
	
	public UFDS() {
		parent = new HashMap<>();
		sizes = new HashMap<>();
		rank = new HashMap<>();
		toplevels = new HashMap<>();
	}
	
	/**
	 * Get the representative element of the set. Changes the structure.
	 * @param element Element of the disjoint set structure
	 * @return The indirect parent of element
	 */
	private T getRepresentative(T element) {
		if (parent.get(element).equals(element)) return element;
		else {
			T par = getRepresentative(parent.get(element));
			parent.put(element, par);
			return par;
		}
	}
	
	/**
	 * @param el1
	 * @param el2
	 * @return true, if el1 and el2 are in the same set.
	 */
	public boolean isSameSet(T el1, T el2) {
		return getRepresentative(el1).equals(getRepresentative(el2));
	}
	
	/**
	 * Perform a union of two sets.
	 * @param el1
	 * @param el2
	 */
	public void doUnion(T el1, T el2) {
		if (isSameSet(el1, el2)) return;
		
		T p1 = getRepresentative(el1);
		T p2 = getRepresentative(el2);
		int r1 = rank.get(p1);
		int r2 = rank.get(p2);
		
		if (r1 < r2) {
			parent.put(p1, p2);
			toplevels.get(p2).joinWith(toplevels.get(p1));
			toplevels.remove(p1);
			sizes.put(p2, sizes.get(p2) + sizes.get(p1));
		} else if (r2 < r1) {
			parent.put(p2, p1);
			toplevels.get(p1).joinWith(toplevels.get(p2));
			toplevels.remove(p2);
			sizes.put(p1, sizes.get(p1) + sizes.get(p2));
		} else { // r2 == r1
			parent.put(p1, p2);
			toplevels.get(p2).joinWith(toplevels.get(p1));
			toplevels.remove(p1);
			sizes.put(p2, sizes.get(p2) + sizes.get(p1));
			rank.put(p2, r2+1);
		}
	}
	
	/**
	 * Insert an element into the structure 
	 * @param element
	 */
	public void insert(T element, M property) {
		parent.put(element, element);
		sizes.put(element, 1);
		rank.put(element, 0);
		toplevels.put(element, property);
	}
	
	public Set<T> getToplevels() {
		return toplevels.keySet();
	}
	
	/**
	 * Return the mapped type for the set the given element is in
	 * @param element
	 * @return
	 */
	public M get(T element) {
		return toplevels.get(getRepresentative(element));
	}
	
	/**
	 * Get the size of the set the given element is in
	 * @param element
	 * @return
	 */
	public int getSize(T element) {
		return sizes.get(getRepresentative(element));
	}
}
