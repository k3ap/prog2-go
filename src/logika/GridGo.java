package logika;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class GridGo extends Grid {
	
	public GridGo(int height, int width) {
		this(height, width, false, false);
	}
	
	public GridGo(int height, int width, boolean blackPass, boolean whitePass) {
		super(height, width, blackPass, whitePass);
	}
	
	private Map<Index, PlayerColor> getZoneConrol() {
		final int[][] NEIGHBOURS = new int[][] {{0, 1}, {1, 0}, {-1, 0}, {0, -1}};
		
		// Find all connected empty zones and all the stones bordering those zones.
		Map<Index, Set<Index>> zonesNeighbours = new HashMap<Index, Set<Index>>();
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				Index idx = new Index(i, j);
				if (!colorOfField(idx).equals(FieldColor.EMPTY)) {
					continue;
				}
				for (int didx = 0; didx < 4; didx++) {
					int di = NEIGHBOURS[didx][0];
					int dj = NEIGHBOURS[didx][1];
					if (idx.i() + di < 0 || idx.j() + dj < 0 || idx.i() + di >= height || idx.j() + dj >= width)
						continue;
					Index neighbor = new Index(i + di, j + dj);
					if (!colorOfField(neighbor).equals(FieldColor.EMPTY)) {
						zonesNeighbours.getOrDefault(connectedComponents.getRepresentative(idx), new HashSet<Index>()).add(neighbor);
					}
				}
			}
		}
				
		// Assign each empty zone a controller.
		Map<Index, PlayerColor> zoneControl = new HashMap<Index, PlayerColor>();
		Random rng = new Random();
		for (Index zoneIdx : zonesNeighbours.keySet()) {
			int black = 0, white = 0;
			for (Index border : zonesNeighbours.get(zoneIdx)) {
				if (colorOfField(border).equals(FieldColor.BLACK)) {
					black++;
				}
				else {
					assert colorOfField(border).equals(FieldColor.WHITE);
					white++;
				}
				if (black > white) {
					zoneControl.put(zoneIdx, PlayerColor.BLACK);
				}
				else if (white > black) {
					zoneControl.put(zoneIdx, PlayerColor.WHITE);
				}
				else {
					if (rng.nextBoolean()) {
						zoneControl.put(zoneIdx, PlayerColor.BLACK);
					}
					else {
						zoneControl.put(zoneIdx, PlayerColor.WHITE);
					}
				}
			}
		}
		
		return zoneControl;
	}
	
	private boolean isPrisoner(Index idx, Map<Index, PlayerColor> zoneControl) {
		int friendly = 0, enemy = 0;
		for (Index liberty : connectedComponents.get(idx).liberties) {
			// Who is the controller of the zone (connected component of FieldColor.EMPTYs)
			// that this liberty is in?
			PlayerColor liberyZoneController = zoneControl.get(connectedComponents.getRepresentative(liberty));
			if (liberyZoneController.field().equals(colorOfField(idx))) {
				// if the component of idx has a liberty in a friendly zone, then
				// it is less likey to be a prisoner
				friendly++;
			}
			else {
				// the component is more likely to be a prisoner
				enemy++;
			}
		}
		
		if (friendly > enemy) {
			return false;
		}
		else if (friendly > enemy) {
			return true;
		}
		else {
			return (new Random()).nextBoolean();
		}
	}

	@Override
	public boolean hasColorLost(FieldColor field) {
		assert !field.equals(FieldColor.EMPTY);
		int blackPoints = 0, whitePoints = 6; // white = 6 because of the komi rule
		
		Map<Index, PlayerColor> zoneControl = getZoneConrol();

		// subtract points for captured stones
		blackPoints -= blackCaptured;
		whitePoints -= whiteCaptured;
		
		// add points for controlled zones
		for (Index zone : zoneControl.keySet()) {
			switch (zoneControl.get(zone)) {
			case BLACK:
				blackPoints += connectedComponents.getSize(zone);
				break;
			case WHITE:
				whitePoints += connectedComponents.getSize(zone);
				break;
			}
		}
		
		// subtract points for prisoners
		for (Index component : connectedComponents.getToplevels()) {
			if (colorOfField(component).equals(FieldColor.EMPTY))
				continue;
			if (isPrisoner(component, zoneControl)) {
				if (colorOfField(component).equals(FieldColor.BLACK)) {
					blackPoints -= connectedComponents.getSize(component);
				}
				else {
					assert colorOfField(component).equals(FieldColor.WHITE):
					whitePoints -= connectedComponents.getSize(component);
				}
			}
		}
		
		if (blackPoints > whitePoints) {
			return field.equals(FieldColor.BLACK);
		}
		// if blackPoints <= whitePoints
		// again because of the komi rule white wins in this case
		return field.equals(FieldColor.WHITE);
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
