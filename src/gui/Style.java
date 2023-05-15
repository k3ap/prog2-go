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
	public Color whiteShadowStone;
	public Color whiteShadowStoneEdge;
	public Color blackShadowStone;
	public Color blackShadowStoneEdge;
	public Color gridColor;
	public int stoneDiameter;
	public Color losingHighlight;

	public Style() {
		// Default look
		// grid
		grid = new BasicStroke(1);
		gridColor = new Color(0, 0, 0);

		// stone color
		stoneEdge = new BasicStroke(1);
		whiteStone = new Color(240, 255, 255);
		whiteStoneEdge = new Color(0, 100, 100);
		blackStone = new Color(5, 5, 10);
		blackStoneEdge = new Color(100, 0, 100);
		stoneDiameter = 40;
		// stone shadow
		whiteShadowStone = new Color(240, 255, 255, 40);
		whiteShadowStoneEdge = new Color(0, 100, 100, 40);
		blackShadowStone = new Color(5, 5, 10, 40);
		blackShadowStoneEdge = new Color(100, 0, 100, 40);

		// color with which the losing component is highlighted
		losingHighlight = new Color(200, 10, 10, 40);
	}
}