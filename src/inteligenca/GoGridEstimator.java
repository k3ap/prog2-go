package inteligenca;

import logika.Grid;
import logika.GridGo;
import logika.PlayerColor;

public class GoGridEstimator extends GridEstimator {

	public GoGridEstimator() {
		super("go");
	}

	@Override
	double estimateGrid(Grid grid, PlayerColor player) {
		GridGo g = (GridGo) grid;
		double capturedValue = switch(player) {
		case BLACK -> g.blackCaptured - g.whiteCaptured;
		case WHITE -> g.whiteCaptured - g.blackCaptured;
		};
		
		double controlValue = 
				g.controlledZones(player.field()).size()
				- g.controlledZones(player.next().field()).size();
		
		return capturedValue + controlValue;
	}

}
