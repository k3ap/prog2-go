package logika;

import java.util.LinkedList;

/**
 * Abstract breadth first search
 * to use create a subclass that implements the other required methods.
 * @see Grid.java
 */
public abstract class BreadthSearch extends Search {
	
	private LinkedList<Index> indexToSearch;
	
	@Override
	protected void markToSearch(Index idx) {
		indexToSearch.add(idx);
	}
	
	@Override
	protected void beginning(Index zacetniIdx) {
		indexToSearch.add(zacetniIdx);
		data.markSearched(zacetniIdx);
	}
	
	@Override
	protected boolean isNext() {
		return !indexToSearch.isEmpty();
	}
	
	@Override
	protected Index next() {
		return indexToSearch.removeFirst();
	}
	
	public BreadthSearch(Grid grid, SearchData data) {
		super(grid, data);
		this.indexToSearch = new LinkedList<Index>();
	}
}
