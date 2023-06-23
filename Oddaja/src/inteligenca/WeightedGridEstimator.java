package inteligenca;

import logika.Grid;
import logika.PlayerColor;

@SuppressWarnings("serial")
class InvalidWeightNumberException extends Exception {}

/**
 * An advanced grid estimator checking different properties of the
 * grid to determine a better score estimation than simply counting liberties
 */
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
	private double myOnlyOneLiberty = -10;
	private double myOnlyTwoLiberties = -5;
	private double theirOnlyOneLiberty = 100;
	private double theirOnlyTwoLiberties = 10;

	public WeightedGridEstimator() {
		super("weighted");
	}
	
	public WeightedGridEstimator(double[] weights) throws InvalidWeightNumberException {
		super("weighted-custom");
		if (weights.length != 14) 
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
		myOnlyOneLiberty = weights[10];
		myOnlyTwoLiberties = weights[11];
		theirOnlyOneLiberty = weights[12];
		theirOnlyTwoLiberties = weights[13];
	}

	@Override
	double estimateGrid(Grid grid, PlayerColor player) {
		
		int minLibNum1 = grid.minimumNumberOfLiberties(player.field());
		int minLibNum2 = grid.minimumNumberOfLiberties(player.next().field());
		
		double s = 0;
		s += myComponentNumberWeight * grid.numberOfComponents(player.field());
		s += theirComponentNumberWeight * grid.numberOfComponents(player.next().field());
		s += myWorstLibertyNumberWeight * minLibNum1;
		s += theirWorstLibertyNumberWeight * minLibNum2;
		s += myAverageLibertyNumberWeight * grid.averageNumberOfLiberties(player.field());
		s += theirAverageLibertyNumberWeight * grid.averageNumberOfLiberties(player.next().field());
		s += myBorderDistanceWeiht * grid.distanceFromBorder(player.field());
		s += theirBorderDistanceWeight * grid.distanceFromBorder(player.next().field());
		s += myMinLibertySizeWeight * grid.minLibertiesSize(player.field());
		s += theirMinLibertySizeWeight * grid.minLibertiesSize(player.next().field());
		
		// special modifiers
		if (minLibNum1 == 1)
			s += myOnlyOneLiberty;
		else if (minLibNum1 == 2)
			s += myOnlyTwoLiberties;
		if (minLibNum2 == 1)
			s += theirOnlyOneLiberty;
		else if (minLibNum2 == 2)
			s += theirOnlyTwoLiberties;
		
		return s;
	}

}
