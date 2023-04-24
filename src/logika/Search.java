package logika;

/**
 * Abstract class that represents a search through the grid.
 * The class implements the basic abstraction common to DFS and BFS.
 * For actual use see DepthSearch and BreadthSearch,
 */
public abstract class Search {
	
	/**
	 * Data associated with the search.
	 */
	protected SearchData data;
	
	/**
	 * The grid to search through.
	 */
	protected Grid grid;
	
	/**
	 * Prepare the structure so search from the given index.
	 * @param startIdx Index where we begin the search.
	 */
	protected abstract void startAtIndex(Index start);
	
	/**
	 * Check if there is any new index to search.
	 * @return `true`, if next() is going to return a sensible value, `false` otherwise.
	 */
	protected abstract boolean hasNext();

	/**
	 * Function that dictates the search order through the graph.
	 * @return The next vertex that we have to search.
	 */
	protected abstract Index next();
	
	/**
	 * Action that happens at the start of processing a vertex.
	 * @param idx Index of the vertex we are looking at.
	 * @param startIdx where the search started.
	 */
	protected abstract void entryAction(Index idx, Index startIdx);
	
	/**
	 * Action that happens at the end of processing a vertex.
	 * @param idx Index of the vertex we are looking at.
	 * @param startIdx where the search started.
	 */
	protected abstract void exitAction(Index idx, Index startIdx);
	
	/**
	 * Action that happens, before we start processing a neighbor vertex.  
	 * (even if decide we will not process it)
	 * @param neighbor Vertex we will be looking at.
	 * @param parent Vertex, from which we are looking.
	 * @param startIdx where the search started.
	 */
	protected abstract void noticeAction(Index neighbor, Index parent, Index startIdx);
	
	/**
	 * Function returns true, if have to process the neighbor.
	 * @param neighbor Vertex we will be looking at.
	 * @param parent Vertex, from which we are looking.
	 * @return Do we have to process the neighbor?
	 */
	protected boolean addNeighbor(Index parent, Index neighbor) {
		return data.toSearch(neighbor);
	}
	
	/**
	 * Mark (add to a queue/stack/...), that we have to search the given index
	 * @param idx Index
	 */
	protected abstract void markToSearch(Index idx);
	
	/**
	 * Helper array that holds the indices of the neighbors.
	 */
	private final int neighbors[][] = new int[][] {
		{-1, 0}, {1, 0}, {0, 1}, {0, -1}
	};
	
	/**
	 * Function that takes a step in the direction of the search.
	 * @param oldIdx Index on which the search started.
	 */
	private void searchStep(Index oldIdx) {
		Index idx = next();
		entryAction(idx, oldIdx);
		
		// Search all the neighbors of the current vertex
		for (int x = 0; x < 4; x++) {
			int i = idx.i() + neighbors[x][0];
			int j = idx.j() + neighbors[x][1];
			
			if (i < 0 || j < 0 || i >= grid.height() || j >= grid.width())
				continue;
			
			Index newIdx = new Index(i, j);
			noticeAction(newIdx, idx, oldIdx);
			if (addNeighbor(idx, newIdx)) {
				markToSearch(newIdx);
				data.markSearched(newIdx);
			}
		}
		exitAction(idx, oldIdx);
	}
	
	/**
	 * Creates a new search instance.
	 * @param grid The grid which we are searching
	 * @param data data we are searching through
	 */
	public Search(Grid grid, SearchData data) {
		this.grid = grid;
		this.data = data;
	}
	
	/**
	 * Runs the search starting with the given index.
	 * @param beginInx Index where we begin the search.
	 */
	public void run(Index beginInx) {
		this.startAtIndex(beginInx);
		while (this.hasNext()) {
			searchStep(beginInx);
		}
	}
	
	/**
	 * Run the search on all parts of the grid.
	 */
	public void runAll() {
		data.simplify();
		for (int i = 0; i < grid.height(); i++) {
			for (int j = 0; j < grid.width(); j++) {
				Index idx = new Index(i, j);
				if (data.toSearch(idx)) {
					run(idx);
				}
			}
		}
	}
}
