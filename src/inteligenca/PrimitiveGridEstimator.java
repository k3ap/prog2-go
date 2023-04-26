package inteligenca;

import logika.Grid;
import logika.PlayerColor;

public class PrimitiveGridEstimator extends GridEstimator {
	
	public PrimitiveGridEstimator() {
		super("primitive");
	}

	@Override
	double estimateGrid(Grid grid, PlayerColor player) {
		int myPoints = grid.minimumNumberOfLiberties(player.field());
		int theirPoints = grid.minimumNumberOfLiberties(player.next().field());
		return myPoints - theirPoints + 2.5 * grid.distanceFromBorder(player.field()) / 9.0
				- 2.5 * grid.distanceFromBorder(player.next().field()) / 9.0;
	}
}
