package logika;

import java.util.HashMap;
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
	 * Get the representative element of the set.
	 * This method does NOT perform path compression
	 * @param element Element of the disjoint set structure
	 * @return The toplevel parent of element
	 */
	public T getRepresentative(T element) {
		if (parent.get(element).equals(element)) return element;
		else return getRepresentative(parent.get(element));
	}
	
	/**
	 * Get the representative element of the set.
	 * This method performs path compression.
	 * @param element
	 * @return
	 */
	private T pathcompress(T element) {
		if (parent.get(element).equals(element)) return element;
		else {
			T prevParent = parent.get(element);
			T par = pathcompress(prevParent);
			parent.put(element, par);
			if (!prevParent.equals(par))
				sizes.put(prevParent, sizes.get(prevParent) - sizes.get(element));
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
	 * Perform a union of two sets. Does not perform memory operations.
	 * Changes structure non-recoverably.
	 * @param el1
	 * @param el2
	 */
	@SuppressWarnings("unchecked")
	public void doUnion(T el1, T el2) {
		if (isSameSet(el1, el2)) return;
		
		T p1 = pathcompress(el1);
		T p2 = pathcompress(el2);
		int r1 = rank.get(p1);
		int r2 = rank.get(p2);
		
		if (r1 < r2) {
			parent.put(p1, p2);
			toplevels.put(p2, (M) toplevels.get(p2).joinWith(toplevels.get(p1)));
			toplevels.remove(p1);
			sizes.put(p2, sizes.get(p2) + sizes.get(p1));
		} else if (r2 < r1) {
			parent.put(p2, p1);
			toplevels.put(p1, (M) toplevels.get(p1).joinWith(toplevels.get(p2)));
			toplevels.remove(p2);
			sizes.put(p1, sizes.get(p1) + sizes.get(p2));
		} else { // r2 == r1
			parent.put(p1, p2);
			toplevels.put(p2, (M) toplevels.get(p2).joinWith(toplevels.get(p1)));
			toplevels.remove(p1);
			sizes.put(p2, sizes.get(p2) + sizes.get(p1));
			rank.put(p2, r2+1);
		}
	}
	
	/**
	 * Insert an element into the structure.
	 * If it already exists, reparent to itself and set the new mapping.
	 * This may invalidate other mappings, so be careful!
	 * Any (possibly indirect) child nodes of the element will be kept as children. 
	 * @param element
	 */
	public void insert(T element, M property) {
		if (parent.containsKey(element)) {
			if (!parent.get(element).equals(element)) {
				T prevParent = pathcompress(element);
				parent.put(element, element);
				sizes.put(prevParent, sizes.get(prevParent) - sizes.get(element));
			}
		} else {
			parent.put(element, element);
			sizes.put(element, 1);
			rank.put(element, 0);
		}
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
