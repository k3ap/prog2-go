package logika;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Representation of the playing field.
 */
public class Grid {
	
	/**
	 * Helper class for keeping track of the liberties of a component 
	 */
	private class LibertiesSetMapping implements SetMapping {
		public Set<Index> liberties;
		
		public LibertiesSetMapping() {
			liberties = new HashSet<>();
		}

		@Override
		public void joinWith(SetMapping other) {
			LibertiesSetMapping o = (LibertiesSetMapping) other;
			liberties.addAll(o.liberties);
		}
	}
	
	private class ComponentLibertySearch extends BreadthFirstSearch {

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
	 * The current state of the playing field: which field are empty, have a black, or have a white stone.
	 */
	private FieldColor grid[][];
	
	/**
	 * Height and width of the grid.
	 */
	private int height, width;
	
	/**
	 * Keeps track of the connected components in the graph and the liberties associated with them
	 */
	private UFDS<Index, LibertiesSetMapping> connectedComponents;
	
	private ComponentLibertySearch graphSearch;
	
	public Grid(int height, int width) {
		grid = new FieldColor[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				grid[i][j] = FieldColor.EMPTY;
			}
		}
		connectedComponents = new UFDS<>();
		this.height = height;
		this.width = width;
		graphSearch = new ComponentLibertySearch(this, new SearchData());
		graphSearch.runAll();
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
		return connectedComponents.isSameSet(idx1, idx2);
	}
	
	/**
	 * Place a stone of the given color on the given index.
	 * Assumes the previous color at the given index is a EMPTY and 
	 * that we're setting a non-EMPTY color
	 * @param idx Index in the grid.
	 * @param color Color of the new stone.
	 */
	public void placeColor(Index idx, FieldColor color) {
		FieldColor oldColor = grid[idx.i()][idx.j()];
		grid[idx.i()][idx.j()] = color;
		
		// The component directly adjacent to the field we've just painted over may be invalidated
		// by this, so we need to rerun the BFS
		
		final int[][] d = new int[][] {{0, 1}, {1, 0}, {-1, 0}, {0, -1}};
		
		graphSearch.data.clear();
		graphSearch.run(idx);
		
		for (int didx = 0; didx < 4; didx++) {
			int di = d[didx][0];
			int dj = d[didx][1];
			
			if (idx.i() + di < 0 || idx.j() + dj < 0 || idx.i() + di >= height || idx.j() + dj >= width)
				continue;
			
			// if the colors before didn't match, there's nothing to fix
			if (grid[idx.i() + di][idx.j() + dj].equals(oldColor)) {
				Index newpar = new Index(idx.i() + di, idx.j() + dj);
				graphSearch.run(newpar);
			}
		}
		
		// this used to be a blank field; we need to remove it from any liberties it may be in
		for (int didx = 0; didx < 4; didx++) {
			int di = d[didx][0];
			int dj = d[didx][1];
			
			if (idx.i() + di < 0 || idx.j() + dj < 0 || idx.i() + di >= height || idx.j() + dj >= width)
				continue;
			
			Index neighbour = new Index(idx.i() + di, idx.j() + dj);
			connectedComponents.get(neighbour).liberties.remove(idx);
		}
	}
	
	/**
	 * Check, if the given color has lost the game (if any component has no liberties).
	 * @param color Color for which we are checking, only sensible for BLACKFIELD and WHITEFIELD.
	 * @return true, if the given color has lost the game
	 */
	public boolean hasColorLost(FieldColor color) {
		for (Index idx : connectedComponents.getToplevels()) {
			if (colorOfField(idx).equals(color)) {
				LibertiesSetMapping mapping = connectedComponents.get(idx);
				if (mapping.liberties.size() == 0) return true;
			}
		}
		return false;
	}
	
	/**
	 * Get the component which has no liberties and has lost its owner the game.
	 * @return An array of indices that the component occupies.
	 */
	public Index[] losingComponent() {
		assert (hasColorLost(FieldColor.WHITE) || hasColorLost(FieldColor.BLACK));

		LinkedList<Index> out = new LinkedList<Index>();

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				Index idx = new Index(i, j);
				if (connectedComponents.get(idx).liberties.size() == 0) {
					out.add(idx);
				}
			}
		}

		Index[] outArr = new Index[out.size()];
		out.toArray(outArr);
		return outArr;
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

    public static Grid readFromFile(String filename, int size) throws FileNotFoundException, IOException {
        Grid grid = new Grid(size, size);
		try (FileReader reader = new FileReader(filename)) {
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					char c = (char) reader.read();
					switch(c) {
					case 'o':
						grid.grid[i][j] = FieldColor.WHITE;
						break;
					case '#':
						grid.grid[i][j] = FieldColor.BLACK;
						break;
					default:
					}
				}
				reader.read();  // newline
			}
		}
		grid.graphSearch.runAll();
		return grid;
    }

	public Grid deepcopy() {
		Grid m = new Grid(height, width);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				m.grid[i][j] = grid[i][j];
			}
		}
		m.graphSearch.runAll();
		return m;
	}

    public double distanceFromBorder(FieldColor color) {
		int r = 10;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				Index idx = new Index(i, j);
				if (colorOfField(idx).equals(color)) {
					r = Math.min(r,
                                 Math.min(
                                     Math.min(i, j),
                                     Math.min(8-i, 8-j)
                                     ));
				}
			}
		}
		return r;
    }
    
    /**
     * Get the minimum number of liberties a certain connected component has
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
     * Get the average number of liberties over all connected components of this color
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
     * Get the number of connected components a color has
     * @param color The color we're interested in
     * @return The number of components belonging to this color.
     */
    public int numberOfComponents(FieldColor color) {
    	int c = 0;
		for (Index idx : connectedComponents.getToplevels()) {
			if (colorOfField(idx).equals(color)) {
				c++;
			}
		}
		return c;
    }
    
    /**
     * Return the smallest size of a liberty component adjacent to any 
     * field of the given color
     * @param color The color we're interested in
     * @return As described
     */
    public int minLibertiesSize(FieldColor color) {
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
    	return m;
    }
}
