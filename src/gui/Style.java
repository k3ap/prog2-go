package gui;

import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Color;

public class Style {
	public Stroke grid;
	public Stroke stoneEdge;
	public Color whiteStone;
	public Color whiteStoneEdge;
	public Color blackStone;
	public Color blackStoneEdge;
	public Color gridColor;
	public int stoneDiameter;

	public Style() {
		// privzeti izgled
		grid = new BasicStroke(1);
		stoneEdge = new BasicStroke(1);
		whiteStone = new Color(240, 255, 255);
		whiteStoneEdge = new Color(0, 100, 100);
		blackStone = new Color(5, 5, 10);
		blackStoneEdge = new Color(100, 0, 100);
		gridColor = new Color(0, 0, 0);
		stoneDiameter = 40;
	}
}