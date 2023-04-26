package inteligenca;

import logika.Grid;
import logika.PlayerColor;

public abstract class GridEstimator {
	public final String name;
	
	public GridEstimator(String name) {
		this.name = name;
	}
	
	abstract double estimateGrid(Grid grid, PlayerColor player);
}
