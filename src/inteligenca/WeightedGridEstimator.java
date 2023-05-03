package inteligenca;

import logika.Grid;
import logika.PlayerColor;

public class WeightedGridEstimator extends GridEstimator {
	
	double myComponentNumberWeight = -0.2;
	double theirComponentNumberWeight = 0.2;
	double myWorstLibertyNumberWeight = 1.5;
	double theirWorstLibertyNumberWeight = -1.5;
	double myAverageLibertyNumberWeight = 1;
	double theirAverageLibertyNumberWeight = -1;
	double myBorderDistanceWeiht = 0.2;
	double theirBorderDistanceWeight = -0.2;
	double myMinLibertySizeWeight = 2;
	double theirMinLibertySizeWeight = -2;

	public WeightedGridEstimator() {
		super("weighted");
	}

	@Override
	double estimateGrid(Grid grid, PlayerColor player) {
		double s = 0;
		s += myComponentNumberWeight * grid.numberOfComponents(player.field());
		s += theirComponentNumberWeight * grid.numberOfComponents(player.next().field());
		s += myWorstLibertyNumberWeight * grid.minimumNumberOfLiberties(player.field());
		s += theirWorstLibertyNumberWeight * grid.minimumNumberOfLiberties(player.next().field());
		s += myAverageLibertyNumberWeight * grid.averageNumberOfLiberties(player.field());
		s += theirAverageLibertyNumberWeight * grid.averageNumberOfLiberties(player.next().field());
		s += myBorderDistanceWeiht * grid.distanceFromBorder(player.field());
		s += theirBorderDistanceWeight * grid.distanceFromBorder(player.next().field());
		s += myMinLibertySizeWeight * grid.minLibertiesSize(player.field());
		s += theirMinLibertySizeWeight * grid.minLibertiesSize(player.next().field());
		return s;
	}

}
