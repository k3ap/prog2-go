package logika;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Grid {
	
	/**
	 * Helper class for keeping track of the liberties of a component 
	 */
	protected class LibertiesSetMapping implements SetMapping {
		public Set<Index> liberties;
		
		public LibertiesSetMapping() {
			liberties = new HashSet<>();
		}

		@Override
		public SetMapping joinWith(SetMapping other) {
			LibertiesSetMapping o = (LibertiesSetMapping) other;
			LibertiesSetMapping newMapping = new LibertiesSetMapping();
			newMapping.liberties.addAll(liberties);
			newMapping.liberties.addAll(o.liberties);
			return newMapping;
		}
	}
	
	protected class ComponentLibertySearch extends BreadthFirstSearch {

		public ComponentLibertySearch(Grid grid, SearchData data) {
			super(grid, data);
			grid.connectedComponents = new UFDS<>();
		}

		@Override
		protected void entryAction(Index idx, Index startIdx) {
			//System.out.format("entryAction at %d,%d with start %d,%d\n", idx.i(), idx.j(), startIdx.i(), startIdx.j());
			// no need to fill out the set mapping here; we'll do that in noticeAction
			connectedComponents.insert(idx, new LibertiesSetMapping());
			
			// merge the new set with the connected component
			connectedComponents.doUnion(idx, startIdx);
		}

		@Override
		protected void exitAction(Index idx, Index startIdx) {}

		@Override
		protected void noticeAction(Index neighbor, Index parent, Index startIdx) {
			if (grid.colorOfField(parent) == FieldColor.EMPTY) 
				return;
			
			if (grid.colorOfField(neighbor) == FieldColor.EMPTY) {
				//System.out.format("liberty for %d,%d at %d,%d\n", parent.i(), parent.j(), neighbor.i(), neighbor.j());
				connectedComponents.get(parent).liberties.add(neighbor);
			}
		}
		
		@Override
		protected boolean addNeighbor(Index parent, Index neighbor) {
			return super.addNeighbor(parent, neighbor) 
					&& grid.colorOfField(neighbor).equals(grid.colorOfField(parent));
		}
	}
	
	/**
	 * Keeps track of the connected components in the graph and the liberties associated with them
	 */
	protected UFDS<Index, LibertiesSetMapping> connectedComponents;
	
	protected ComponentLibertySearch graphSearch;
	
	/**
	 * Height and width of the grid.
	 */
	protected int width, height;
	
	/**
	 * The current state of the playing field: which fields are empty, have a black, or have a white stone.
	 */
	protected FieldColor grid[][];
	
	public Grid(int height, int width) {
		this.height = height;
		this.width = width;
		
		grid = new FieldColor[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				grid[i][j] = FieldColor.EMPTY;
			}
		}
		
		connectedComponents = new UFDS<>();
		graphSearch = new ComponentLibertySearch(this, new SearchData());
		graphSearch.runAll();
	}
	
	public int width() { return width; }
	public int height() { return height; }
	
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
		return connectedComponents.isSameSet(idx1, idx2);
	}
	
	/**
	 * Place a stone of the given color on the given index.
	 * @param idx Index in the grid.
	 * @param color Color of the new stone.
	 */
	public void placeColor(Index idx, FieldColor color) {
		
		final int[][] NEIGHBOURS = new int[][] {{0, 1}, {1, 0}, {-1, 0}, {0, -1}};
		
		FieldColor oldColor = grid[idx.i()][idx.j()];
		grid[idx.i()][idx.j()] = color;
		
		// The component directly adjacent to the field we've just painted over may be invalidated
		// by this, so we need to rerun the BFS
		
		graphSearch.data.clear();
		graphSearch.run(idx);
		
		for (int didx = 0; didx < 4; didx++) {
			int di = NEIGHBOURS[didx][0];
			int dj = NEIGHBOURS[didx][1];
			
			if (idx.i() + di < 0 || idx.j() + dj < 0 || idx.i() + di >= height || idx.j() + dj >= width)
				continue;
			
			// if the colors before didn't match, there's nothing to fix
			if (grid[idx.i() + di][idx.j() + dj].equals(oldColor)) {
				Index newpar = new Index(idx.i() + di, idx.j() + dj);
				graphSearch.run(newpar);
			}
		}
		
		if (oldColor.equals(FieldColor.EMPTY)) {
			// this used to be a blank field; we need to remove it from any liberties it may be in
			for (int didx = 0; didx < 4; didx++) {
				int di = NEIGHBOURS[didx][0];
				int dj = NEIGHBOURS[didx][1];
				
				if (idx.i() + di < 0 || idx.j() + dj < 0 || idx.i() + di >= height || idx.j() + dj >= width)
					continue;
				
				Index neighbour = new Index(idx.i() + di, idx.j() + dj);
				connectedComponents.get(neighbour).liberties.remove(idx);
			}
			
		} else if (color.equals(FieldColor.EMPTY)) {
			
			// we need to add this field to any liberties it could be in
			for (int didx = 0; didx < 4; didx++) {
				int di = NEIGHBOURS[didx][0];
				int dj = NEIGHBOURS[didx][1];
				
				if (idx.i() + di < 0 || idx.j() + dj < 0 || idx.i() + di >= height || idx.j() + dj >= width)
					continue;
				
				if (grid[idx.i()+di][idx.j()+dj].equals(FieldColor.EMPTY)) 
					continue;
				
				Index neighbour = new Index(idx.i() + di, idx.j() + dj);
				connectedComponents.get(neighbour).liberties.add(idx);
			}
		}
	}

	/**
	 * Check if the given color has lost the game.
	 * @param color Color for which we are checking, only sensible for BLACKFIELD and WHITEFIELD.
	 * @return true, if the given color has lost the game
	 */
	public abstract boolean hasColorLost(FieldColor field);
	
	/**
	 * Return the indicies of all stones of the given color, which belong to a component with no liberties.
	 * This is used to draw the losing stones in a FCGO game and not used in GO. 
	 * @return An array of indices that the component occupies.
	 */
	public abstract Index[] losingComponent(FieldColor field);
	
	/**
	 * Check if the field with the given index is free.
	 * @param idx Index
	 * @return true iff the field at index is free.
	 */
	private boolean isFree(Index idx) {
		return colorOfField(idx) == FieldColor.EMPTY;
	}
	
	/**
	 * Finds all free fields.
	 * @return A list of all free fields in the grid.
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
	
	/**
	 * Finds the interesting fields, i.e. the fields that an AI may wish to place stones upon.
	 * @return A list of all interesting fields in the grid.
	 */
	public List<Index> interestingFields() {
		HashSet<Index> intermediate = new HashSet<Index>();
		int range = 2;
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (!isFree(new Index(i, j))) {
					// for each placed stone
					for (int di = -range; di <= range; di++) {
						for (int dj = -range; dj <= range; dj++) {
							// only check inside a range radius circle around placed stones
							if (Math.abs(di) + Math.abs(dj) > range)
								continue;
							if (i+di < 0 || i+di >= height || j+dj < 0 || j+dj >= width)
								continue;
							Index idx = new Index(i+di, j+dj);
							if (isFree(idx)) {
								intermediate.add(idx);
							}
						}
					}
				}
			}
		}
		
		List<Index> interesting = new ArrayList<Index>();
		if (intermediate.size() == 0) {
			interesting.add(new Index(height / 2, width / 2));
		} else {
			interesting.addAll(intermediate);
		}
		return interesting;
	}
	
    @Override
    public String toString() {
		String buf = "";
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				char c = switch(grid[i][j]) {
				case EMPTY -> '.';
				case WHITE -> 'o';
				case BLACK -> '#';
				};
				buf += c;
			}
			buf += '\n';
		}
		return buf;
    }

    public void printToFile(String filename) {
		try (FileWriter writer = new FileWriter(filename)) {
			writer.write(toString());
		}
		catch(Exception e) {}
    }
    
    /**
     * Get the minimum distance of a stone of the given color from the border.
     * Used in the weighted estimator.
     */
    public double distanceFromBorder(FieldColor color) {
		int r = width + height;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				Index idx = new Index(i, j);
				if (colorOfField(idx).equals(color)) {
					r = Math.min(
							r,
							Math.min(
                                Math.min(i, j),
                                Math.min(8-i, 8-j)
                            )
					);
				}
			}
		}
		return (double) r;
    }
    
    /**
     * Get the minimum number of liberties a certain connected component has.
     * Used in the weighted estimator.
     * @param color The color we're interested in
     * @return The minimal number of liberties of any component of this color
     */
    public int minimumNumberOfLiberties(FieldColor color) {
    	int m = width * height;
		for (Index idx : connectedComponents.getToplevels()) {
			if (colorOfField(idx).equals(color)) {
				m = Math.min(m, connectedComponents.get(idx).liberties.size());
			}
		}
		return m;
    }
    
    /**
     * Get the average number of liberties over all connected components of this color.
     * Used in the weighted estimator.
     * @param color The color we're interested in
     */
    public double averageNumberOfLiberties(FieldColor color) {
    	int s = 0;  // all liberties
    	int c = 0;  // all components
		for (Index idx : connectedComponents.getToplevels()) {
			if (colorOfField(idx).equals(color)) {
				c++;
				s += connectedComponents.get(idx).liberties.size();
			}
		}
		if (c == 0) return 0;
		return s / c;
    }
    
    /**
     * Get the number of connected components a color has.
     * Used in the weighted estimator.
     * @param color The color we're interested in
     * @return The number of components belonging to this color.
     */
    public double numberOfComponents(FieldColor color) {
    	int c = 0;
		for (Index idx : connectedComponents.getToplevels()) {
			if (colorOfField(idx).equals(color)) {
				c++;
			}
		}
		return (double) c;
    }
    
    /**
     * Return the smallest size of a liberty component adjacent to any 
     * field of the given color.
     * Used in the weighted estimator.
     * @param color The color we're interested in
     * @return As described
     */
    public double minLibertiesSize(FieldColor color) {
    	int[][] d = new int[][] {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
    	int m = width*height;
    	for (int i = 0; i < height; i++) {
    		for (int j = 0; j < width; j++) {
    			if (!grid[i][j].equals(color)) continue;
    			for (int didx = 0; didx < 4; didx++) {
    				int di = d[didx][0];
    				int dj = d[didx][1];
    				if (i+di < 0 || i+di >= height || j+dj < 0 || j+dj >= width) continue;
    				if (grid[i+di][j+dj].equals(FieldColor.EMPTY)) {
    					m = Math.min(
    						m, 
    						connectedComponents.getSize(new Index(i+di, j+dj))
    					);
    				}
    			}
    		}
    	}
    	return (double) m;
    }
    
    /**
     * Make a deep copy of the grid.
     * @return
     */
    public abstract Grid deepcopy();

    /**
	 * Check if nextPlayer has a foced move.
	 * Only valid for FCGO.
	 * @param nextPlayer the player whose turn it is.
	 * @return An Index of the forced move, null if there is none.
	 */
	public abstract Index forcedMove(PlayerColor player);

	/**
     * Check whether the given placement is a valid move.
     * @param idx
     * @param color
     * @return
     */
	protected abstract boolean isPlacementValid(Index idx, FieldColor field);
}
