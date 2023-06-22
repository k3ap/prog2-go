package logika;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class GridGo extends Grid {
	private Set<Index> blackControl, blackPrisoners;
	private Set<Index> whiteControl, whitePrisoners;
	protected int prevState, prevPrevState;
	/**
	 * Whether black / white passed in the previous turn.
	 */
	public boolean blackPass, whitePass;
	/**
	 * How many black / white stones have been captured.
	 * If black surrounds white's stones then whiteCaptured is increased.
	 */
	public int blackCaptured, whiteCaptured;
	
	public GridGo(int height, int width) {
		this(height, width, false, false, 0, 0);
	}
	
	public GridGo(int height, int width, boolean blackPass, boolean whitePass, int prevState, int prevPrevState) {
		super(height, width, blackPass, whitePass);
		this.blackPass = blackPass;
		this.whitePass = whitePass;
		this.prevState = prevState;
		this.prevPrevState = prevPrevState;
		this.blackControl = new HashSet<>();
		this.whiteControl = new HashSet<>();
		this.blackPrisoners = new HashSet<>();
		this.whitePrisoners = new HashSet<>();
		blackCaptured = whiteCaptured = 0;
	}

	public boolean consecutivePasses() {
		return blackPass && whitePass;
	}
	
	public void captureComponentsOf(FieldColor player) {
    	assert !player.equals(FieldColor.EMPTY);
    	
    	Index[] captured = libertylessFields(player);
    	if (player.equals(FieldColor.BLACK))
			blackCaptured += captured.length;
		else
			whiteCaptured += captured.length;
    	
    	for (Index idx : captured) {
			grid[idx.i()][idx.j()] = FieldColor.EMPTY;
		}
		graphSearch.runAll();
	}
	
	public int calculatePoints(FieldColor color) {
		int score = 0;
		switch(color) {
		case BLACK:
			score -= blackCaptured;
			score += blackControl.size();
			score -= blackPrisoners.size();
			break;
		case WHITE:
			score += 6;
			score -= whiteCaptured;
			score += whiteControl.size();
			score -= whitePrisoners.size();
			break;
		default:
			assert false;
		}
		return score;
	}

	@Override
	public boolean hasColorLost(FieldColor field) {
		assert !field.equals(FieldColor.EMPTY);
		
		int blackPoints = calculatePoints(FieldColor.BLACK);
		int whitePoints = calculatePoints(FieldColor.WHITE);
		
		if (blackPoints > whitePoints) {
			// black won, white lost
			return field.equals(FieldColor.WHITE);
		}
		// if blackPoints <= whitePoints
		// again because of the komi rule white wins, black loses
		return field.equals(FieldColor.BLACK);
	}

	@Override
	public Grid deepcopy() {
		Grid newGrid = new GridGo(height, width, blackPass, whitePass, prevState, prevPrevState);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				newGrid.grid[i][j] = this.grid[i][j];
			}
		}
		newGrid.graphSearch.runAll();
		return newGrid;
	}
	
	private int hash2D(Object[][] arr) {
		int hash = 0;
		for (Object[] line : arr) {
			hash += Arrays.hashCode(line);
		}
		return hash;
	}

	@Override
	public boolean isValidForPlayer(Index idx, FieldColor field) {
		if (!colorOfField(idx).equals(FieldColor.EMPTY))
			return false; // this field is not empty
		
		GridGo g = (GridGo) deepcopy();
		
		boolean toReturn = true;
		g.placeColor(idx, field);
		if (g.NLibertiesComponent(field, 0) != null && g.NLibertiesComponent(field.next(), 0) == null) {
			// you've created a libertyless component without capturing
			// an enemy component, aka. suicide
			toReturn = false;
		}
		else if (prevPrevState == hash2D(g.grid)) {
			toReturn = false; // grid repetition
		}
		return toReturn;
	}
	
	/**
	 * Find the controller (if one exists) of every empty zone.
	 */
	private Map<Index, FieldColor> getZoneConrol() {
		
		// Assign each empty zone a controller.
		Map<Index, FieldColor> zoneControl = new HashMap<Index, FieldColor>();
		
		for (Index tln : connectedComponents.getToplevels()) {
			if (colorOfField(tln).equals(FieldColor.EMPTY)) {
				// count the number of border nodes of each color
				int black = 0, white = 0;
				
				// we iterate through the "liberties" of an empty component,
				// which are actually just all the adjacent nodes
				for (Index idx : connectedComponents.get(tln).liberties) {
					if (colorOfField(idx).equals(FieldColor.BLACK)) {
						black++;
					}
					else if (colorOfField(idx).equals(FieldColor.WHITE)) {
						white++;
					}
					else {
						System.out.println("Houston, we have a problem.");
					}
				}
				
				System.out.format("black: %d, white: %d\n", black, white);
				
				double blockage = (double) Integer.max(black, white);
				double gridAverage = (double) (height + width) / 2.0;
				
				// The zone is neutral if
				// either both sides have the same claim to the zone or
				// the second formula is correct. In the 9x9 case the formula means,
				// that you need to have at least 1 stone per 7.2 controlled empty fields.
				// The second case is here mostly to avoid black having control over the
				// entire board every other turn at the start of the game.
				if (black == white ||
					connectedComponents.getSize(tln) >= (blockage * gridAverage) * 0.8) {
					zoneControl.put(tln, FieldColor.EMPTY);
				}
				else if (black > white) {
					zoneControl.put(tln, FieldColor.BLACK);
				}
				else {
					zoneControl.put(tln, FieldColor.WHITE);
				}
			}
		}
		
		return zoneControl;
	}
	
	/**
	 * Is the stone at the given index a prisoner.
	 * A stone is a prisoner if its component is a prisoner.
	 * A component is a prisoner, if most of its liberties are
	 * in zones controlled by the opponent.
	 * @param idx The index of the field to be checked.
	 * @param zoneControl Must be precalculated with getZoneControl 
	 * for optimization purposes.
	 * @return True iff the stone is a prisoner.
	 */
	private boolean isPrisoner(Index idx, Map<Index, FieldColor> zoneControl) {
		assert !colorOfField(idx).equals(FieldColor.EMPTY);
		int friendly = 0, enemy = 0;
		for (Index liberty : connectedComponents.get(idx).liberties) {
			// Who is the controller of the zone (connected component of FieldColor.EMPTYs)
			// that this liberty is in?
			Index neighborComponent = connectedComponents.getRepresentative(liberty);
			FieldColor liberyZoneController = zoneControl.get(neighborComponent);
			if (liberyZoneController.equals(colorOfField(idx)) || liberyZoneController.equals(FieldColor.EMPTY)) {
				// if the component of idx has a liberty in a friendly or neutral zone, then
				// it is less likely to be a prisoner
				friendly++;
			}
			else {
				// the component is more likely to be a prisoner
				enemy++;
			}
		}
		
		if (friendly >= enemy) {
			return false;
		}
		else {
			return true;
		}
	}
	
	private Set<Index> calculateControlledZones(PlayerColor player, Map<Index, FieldColor> zoneControl) {
		Set<Index> controlled = new HashSet<Index>();
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				Index idx = new Index(i, j);
				if (!colorOfField(idx).equals(FieldColor.EMPTY))
					continue;
				Index component = connectedComponents.getRepresentative(idx);
				if (zoneControl.get(component).equals(player.field())) {
					controlled.add(idx);
				}
			}
		}
		
		return controlled;
	}

	private Set<Index> calculatePrisonersOf(PlayerColor player, Map<Index, FieldColor> zoneControl) {
		Set<Index> prisoners = new HashSet<Index>();
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				Index idx = new Index(i, j);
				if (!colorOfField(idx).equals(player.field()))
					continue;
				Index component = connectedComponents.getRepresentative(idx);
				if (isPrisoner(component, zoneControl))
					prisoners.add(idx);
				
			}
		}
		return prisoners;
	}
	
	@Override
	public void placeColor(Index idx, FieldColor color) {
		super.placeColor(idx, color);
		Map<Index, FieldColor> zoneControl = getZoneConrol();
		blackPrisoners = calculatePrisonersOf(PlayerColor.BLACK, zoneControl);
		whitePrisoners = calculatePrisonersOf(PlayerColor.WHITE, zoneControl);
		blackControl = calculateControlledZones(PlayerColor.BLACK, zoneControl);
		whiteControl = calculateControlledZones(PlayerColor.WHITE, zoneControl);
		prevPrevState = prevState;
		prevState = hash2D(grid);
	}

	/**
	 * Get the indices controlled by player.
	 * A connected component of empty indices is controlled by a
	 * player if the majority of the bounding stones of this component
	 * are of the player's color.
	 * @param fieldColor the player in question.
	 * @return The empty fields controlled by player.
	 */
	public Set<Index> controlledZones(FieldColor fieldColor) {
		switch (fieldColor) {
		case BLACK:
			return blackControl;
		case WHITE:
			return whiteControl;
		default:
			break;
		}
		return null;
	}

	/**
	 * A prisoner is a small component of stones that is contained
	 * in a zone controlled by the opposing player. 
	 * @param player The owner of the prisoners.
	 * @return The idicies of all of player's prisoners.
	 */
	public Set<Index> prisonersOf(FieldColor player) {
		switch (player) {
		case BLACK:
			return blackPrisoners;
		case WHITE:
			return whitePrisoners;
		default:
			break;
		}
		return null;
	}
	
	public static final Random RNG = new Random(); 
	
	/**
	 * Return the index of a random empty field.
	 */
	public Index getRandomEmptyField() {
		Index ret;
		do {
			ret = new Index(RNG.nextInt(height), RNG.nextInt(width));
		} while (!colorOfField(ret).equals(FieldColor.EMPTY));
		return ret;
	}

}
