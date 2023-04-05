package gui;

import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Color;

public class Izgled {
	public Stroke mreza;
	public Stroke robKamna;
	public Color belKamen;
	public Color belKamenRob;
	public Color crnKamen;
	public Color crnKamenRob;
	public Color barvaMreze;
	public int premerKamna;

	public Izgled() {
		// privzeti izgled
		mreza = new BasicStroke(1);
		robKamna = new BasicStroke(1);
		belKamen = new Color(240, 255, 255);
		belKamenRob = new Color(0, 100, 100);
		crnKamen = new Color(5, 5, 10);
		crnKamenRob = new Color(100, 0, 100);
		barvaMreze = new Color(0, 0, 0);
		premerKamna = 40;
	}
}