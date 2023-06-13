package logika;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;


/**
 * Representation of the playing field.
 */
public class GridFirstCapture extends Grid {
	
	public GridFirstCapture(int height, int width) {
		super(height, width);
	}

	/**
	 * Check if the given color has lost the game (if any component has no liberties).
	 * @param color Color for which we are checking, only sensible for BLACKFIELD and WHITEFIELD.
	 * @return true, if the given color has lost the game
	 */
	@Override
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
	 * Read a FCGO grid from a text file.
	 * This is not used in the GUI program and is left here for compatibility with the testiranje package.
	 * If the package is removed, this method should be removed too. 
	 * @param filename
	 * @param size
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
    public static GridFirstCapture readFromFile(String filename, int size) throws FileNotFoundException, IOException {
        GridFirstCapture grid = new GridFirstCapture(size, size);
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
	
    @Override
	public Grid deepcopy() {
		Grid newGrid = new GridFirstCapture(height, width);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				newGrid.grid[i][j] = this.grid[i][j];
			}
		}
		newGrid.graphSearch.runAll();
		return newGrid;
	}
    
    /**
	 * Check if nextPlayer has a foced move.
	 * @param nextPlayer the player whose turn it is.
	 * @return An Index of the forced move, null if there is none.
	 */
	public Index forcedMove(FieldColor nextPlayer) {
		Index immediateWin = oneLibertyComponent(nextPlayer.next());
		if (immediateWin != null) {
			// the opponent has a component with only one liberty
			return immediateWin;
		}
		Index immediateLoss = oneLibertyComponent(nextPlayer);
		if (immediateLoss != null) {
			return immediateLoss;
		}
		return null;
	}
    
    /**
	 * Check if the field with the given index is free.
	 * @param idx Index
	 * @return true iff the field at index is free.
	 */
    @Override
	public boolean isValidForPlayer(Index idx, FieldColor player) {
		return colorOfField(idx) == FieldColor.EMPTY;
	}
}
