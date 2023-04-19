package logika;

import java.util.LinkedList;

/**
 * Abstract depth first search
 * the same as BreadthSearch except that it uses a stack instead of a queue.
 */
public abstract class DepthSearch extends Search {
	
	private LinkedList<Index> indexToSearch;
	
	@Override
	protected void markToSearch(Index idx) {
		indexToSearch.add(idx);
	}
	
	@Override
	protected void beginning(Index startIdx) {
		indexToSearch.add(startIdx);
	}
	
	@Override
	protected boolean isNext() {
		return !indexToSearch.isEmpty();
	}
	
	@Override
	protected Index next() {
		return indexToSearch.removeLast();
	}
	
	public DepthSearch(Grid grid, SearchData data) {
		super(grid, data);
		this.indexToSearch = new LinkedList<Index>();
	}
}
