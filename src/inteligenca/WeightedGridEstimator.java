package inteligenca;

import logika.Grid;
import logika.PlayerColor;

@SuppressWarnings("serial")
class InvalidWeightNumberException extends Exception {}

public class WeightedGridEstimator extends GridEstimator {
	
	private double myComponentNumberWeight = -0.2;
	private double theirComponentNumberWeight = 0.2;
	private double myWorstLibertyNumberWeight = 1.5;
	private double theirWorstLibertyNumberWeight = -1.5;
	private double myAverageLibertyNumberWeight = 1;
	private double theirAverageLibertyNumberWeight = -1;
	private double myBorderDistanceWeiht = 0.2;
	private double theirBorderDistanceWeight = -0.2;
	private double myMinLibertySizeWeight = 2;
	private double theirMinLibertySizeWeight = -2;

	public WeightedGridEstimator() {
		super("weighted");
	}
	
	public WeightedGridEstimator(double[] weights) throws InvalidWeightNumberException {
		super("weighted-custom");
		if (weights.length != 10) 
			throw new InvalidWeightNumberException();
		
		myComponentNumberWeight = weights[0];
		theirComponentNumberWeight = weights[1];
		myWorstLibertyNumberWeight = weights[2];
		theirWorstLibertyNumberWeight = weights[3];
		myAverageLibertyNumberWeight = weights[4];
		theirAverageLibertyNumberWeight = weights[5];
		myBorderDistanceWeiht = weights[6];
		theirBorderDistanceWeight = weights[7];
		myMinLibertySizeWeight = weights[8];
		theirMinLibertySizeWeight = weights[9];
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
