package logika;

import java.util.LinkedList;

/**
 * Abstract breadth first search
 * to use create a subclass that implements the other required methods.
 * @see Grid.java
 */
public abstract class BreadthFirstSearch extends Search {
	
	private LinkedList<Index> indiciesToSearch;
	
	@Override
	protected void markToSearch(Index idx) {
		indiciesToSearch.add(idx);
	}
	
	@Override
	protected void startAtIndex(Index start) {
		indiciesToSearch.add(start);
		data.markSearched(start);
	}
	
	@Override
	protected boolean hasNext() {
		return !indiciesToSearch.isEmpty();
	}
	
	@Override
	protected Index next() {
		return indiciesToSearch.removeFirst();
	}
	
	public BreadthFirstSearch(Grid grid, SearchData data) {
		super(grid, data);
		this.indiciesToSearch = new LinkedList<Index>();
	}
}
