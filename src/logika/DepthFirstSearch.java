package logika;

import java.util.LinkedList;

/**
 * Abstract depth first search
 * the same as BreadthFirstSearch except that it uses a stack instead of a queue.
 */
public abstract class DepthFirstSearch extends Search {
	
	private LinkedList<Index> indiciesToSearch;
	
	@Override
	protected void markToSearch(Index idx) {
		indiciesToSearch.add(idx);
	}
	
	@Override
	protected void startAtIndex(Index start) {
		indiciesToSearch.add(start);
	}
	
	@Override
	protected boolean hasNext() {
		return !indiciesToSearch.isEmpty();
	}
	
	@Override
	protected Index next() {
		return indiciesToSearch.removeLast();
	}
	
	public DepthFirstSearch(GridFirstCapture grid, SearchData data) {
		super(grid, data);
		this.indiciesToSearch = new LinkedList<Index>();
	}
}
