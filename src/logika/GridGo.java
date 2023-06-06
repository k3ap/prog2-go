package logika;

import java.util.LinkedList;

public class GridGo extends Grid {
	
	public GridGo(int height, int width) {
		super(height, width);
	}
	
	public GridGo(int height, int width, boolean blackPass, boolean whitePass) {
		super(height, width, blackPass, whitePass);
	}

	private boolean blackPass, whitePass;

	@Override
	public boolean hasColorLost(FieldColor field) {
		// TODO: count points
		return false;
	}

	@Override
	public Index[] losingComponent(FieldColor field) {
		LinkedList<Index> out = new LinkedList<Index>();

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				Index idx = new Index(i, j);
				if (!colorOfField(idx).equals(FieldColor.EMPTY) && connectedComponents.get(idx).liberties.size() == 0) {
					out.add(idx);
				}
			}
		}

		Index[] outArr = new Index[out.size()];
		out.toArray(outArr);
		return outArr;
	}

	@Override
	public Grid deepcopy() {
		Grid newGrid = new GridGo(height, width, blackPass, whitePass);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				newGrid.grid[i][j] = this.grid[i][j];
			}
		}
		newGrid.graphSearch.runAll();
		return newGrid;
	}

	@Override
	public Index forcedMove(PlayerColor player) {
		// there are no forced moves in Go
		return null;
	}

	@Override
	public boolean isValid(Index idx, FieldColor field) {
		boolean empty = colorOfField(idx).equals(FieldColor.EMPTY);
		boolean suicidal = false;
		this.placeColor(idx, field);
		if (NLibertiesComponent(field, 0) != null) {
			suicidal = true;
		}
		this.placeColor(idx, FieldColor.EMPTY);
		
		return empty && !suicidal;
	}

}
