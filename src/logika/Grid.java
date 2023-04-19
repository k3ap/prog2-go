package logika;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Representation of the playing field.
 */
public class Grid {
	
	/**
	 * Helper class for finding connected components in the graph.
	 * Holds the "color" of the vertices; vertices at the same color, when they are in the same component
	 */
	private class ComponentSearchData extends SearchData {
		private int numberOfColors = 0;
		
		public int nextColor() {
			return ++numberOfColors;
		}
		
		@Override
		public void simplify() {
			super.simplify();
			numberOfColors = 0;
		}
	}
	
	/**
	 * Helper class that finds connected components in a graph.
	 * Sets the values in grid.connectedComponents while searching.
	 */
	private class ComponetSearch extends BreadthSearch {

		public ComponetSearch(Grid grid, ComponentSearchData data) {
			super(grid, data);
		}

		@Override
		protected void entryAction(Index idx, Index startIdx) {
			if (idx.equals(startIdx)) {
				grid.connectedComponents[idx.i()][idx.j()] = 
						((ComponentSearchData)data).nextColor();
			} else {
				grid.connectedComponents[idx.i()][idx.j()] = 
						grid.connectedComponents[startIdx.i()][startIdx.j()];
			}
		}

		@Override
		protected void exitAction(Index idx, Index zacetniIdx) {}

		@Override
		protected void noticeAction(Index idx, Index stars, Index zacetniIdx) {}

		@Override
		protected boolean addNeighbor(Index stars, Index neighbor) {
			return super.addNeighbor(stars, neighbor) 
					&& grid.colorOfField(neighbor).equals(grid.colorOfField(stars));
		}
	}
	
	/**
	 * Helper class for keeping data on liberties while searching.
	 */
	private class LipertiesSearchData extends SearchData {
		private HashMap< Integer, HashSet<Index> > liberties;
		
		public LipertiesSearchData() {
			super();
			this.liberties = new HashMap<>();
		}
		
		@Override
		public void simplify() {
			super.simplify();
			liberties.clear();
		}
		
		public int numberOfLiberties(int connectedColor) {
			if (!liberties.containsKey(connectedColor)) {
				return 0;
			}
			return liberties.get(connectedColor).size();
		}
		
		public void markAsLiberty(int color, Index idx) {
			if (liberties.containsKey(color)) {
				liberties.get(color).add(idx);
			} else {
				liberties.put(color, new HashSet<>());
				liberties.get(color).add(idx);
			}
		}
	}
	
	/**
	 * Helper class for searching for the liberties of a given component.
	 * Assumes that the component search has already been done and
	 * that the data in grid.connectedComponents is accurate
	 */
	private class LibertiesSearch extends BreadthSearch {

		public LibertiesSearch(Grid grid, LipertiesSearchData data) {
			super(grid, data);
		}

		@Override
		protected void entryAction(Index idx, Index startIdx) {}

		@Override
		protected void exitAction(Index idx, Index startIdx) {}

		@Override
		protected boolean addNeighbor(Index parent, Index neighbor) {
			return super.addNeighbor(parent, neighbor)
					&& grid.colorOfField(parent).equals(grid.colorOfField(neighbor)) 
					&& !grid.colorOfField(parent).equals(FieldColor.EMPTY);
		}

		@Override
		protected void noticeAction(Index idx, Index parent, Index startIdx) {
			if (grid.colorOfField(parent) == FieldColor.EMPTY) 
				return;
			
			if (grid.colorOfField(idx) == FieldColor.EMPTY) {
				int color = grid.connectedComponents[parent.i()][parent.j()];
				LipertiesSearchData d = (LipertiesSearchData) (this.data);
				d.markAsLiberty(color, idx);
			}
		}
		
	}
	
	/**
	 * The current state of the playing field: which field are empty, have a black, or have a white stone.
	 */
	private FieldColor grid[][];
	
	/**
	 * Holds the numbers ("colors"), that represent connected components.
	 * The same number means the same component.
	 * This array is filled out by ComponentSearch.
	 */
	private int connectedComponents[][];
	
	/**
	 * Height and width of the grid.
	 */
	private int height, width;

	private ComponetSearch componetSearch;
	private LibertiesSearch libertiesSearch;
	
	public Grid(int height, int width) {
		grid = new FieldColor[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				grid[i][j] = FieldColor.EMPTY;
			}
		}
		connectedComponents = new int[height][width];
		this.height = height;
		this.width = width;
		componetSearch = new ComponetSearch(this, new ComponentSearchData());
		componetSearch.runAll();
		libertiesSearch = new LibertiesSearch(this, new LipertiesSearchData());
		libertiesSearch.runAll();
	}
	
	public int height() { return height; }
	public int width() { return width; }
	
	/**
	 * Returns the current color of a stone on the given index.
	 * @param idx Index of the field to look at.
	 * @return Color of the stone there.
	 */
	public FieldColor colorOfField(Index idx) {
		return grid[idx.i()][idx.j()];
	}
	
	/**
	 * Check if two indices are in the same component.
	 * @param idx1 Index of the first vertex.
	 * @param idx2 Index of the second vertex.
	 * @return true when they are in the same component.
	 */
	public boolean sameComponent(Index idx1, Index idx2) {
		return connectedComponents[idx1.i()][idx1.j()] == connectedComponents[idx2.i()][idx2.j()];
	}
	
	/**
	 * Place a stone of the given color on the given index.
	 * @param idx Index in the grid.
	 * @param color Color of the new stone.
	 */
	public void placeColor(Index idx, FieldColor color) {
		grid[idx.i()][idx.j()] = color;
		componetSearch.runAll();
		libertiesSearch.runAll();
		/*for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				System.out.format("%d ", povezaneKomponente[i][j]);
			}
			System.out.println();
		}*/
	}
	
	/**
	 * Check, if the given color has lost the game (if any component has no liberties).
	 * @param color Color for which we are checking, only sensible for BLACKFIELD and WHITEFIELD.
	 * @return true, if the given color has lost the game
	 */
	public boolean hasColorLost(FieldColor color) {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (grid[i][j] != color) continue;
				int componentColor = connectedComponents[i][j];
				if (((LipertiesSearchData)libertiesSearch.data).numberOfLiberties(componentColor) == 0) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Check if the field with the given index is free.
	 * @param idx Index
	 * @return true, when the field at index is free.
	 */
	public boolean isFree(Index idx) {
		return colorOfField(idx) == FieldColor.EMPTY;
	}
	
	/**
	 * Finds all free fields.
	 * @return A list of all free field in the grid.
	 */
	public List<Index> freeFields() {
		ArrayList<Index> free = new ArrayList<>();
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				Index idx = new Index(i, j);
				if (isFree(idx)) {
					free.add(idx);
				}
			}
		}
		return free;
	}

	public void print() {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				char c = switch(grid[i][j]) {
				case EMPTY -> '.';
				case WHITEFIELD -> 'o';
				case BLACKFIELD -> '#';
				};
				System.out.print(c);
			}
			System.out.println();
		}
	}
}
