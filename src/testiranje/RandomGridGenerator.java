package testiranje;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import logika.FieldColor;
import logika.GridFirstCapture;
import logika.Index;


public class RandomGridGenerator {
	
	private static Random RNG = new Random();
	
	public static Index randomIdx(Set<Index> taken, int n) {
		Index idx = new Index(RNG.nextInt(n), RNG.nextInt(n));
		while (taken.contains(idx)) {
			idx = new Index(RNG.nextInt(n), RNG.nextInt(n));
		}
		return idx;
	}
	
	public static FieldColor randomColor() {
		if (RNG.nextInt() % 2 == 0) {
			return FieldColor.WHITE;
		} else {
			return FieldColor.BLACK;
		}
	}
	
	public static GridFirstCapture randomGrid(int n, double fullness) {
		GridFirstCapture grid = new GridFirstCapture(n, n);
		HashSet<Index> taken = new HashSet<Index>();
		int polja = n*n;
		int c = 0;
		while (c < fullness * polja) {
			c++;
			Index idx = randomIdx(taken, n);
			FieldColor color = randomColor();
			grid.placeColor(idx, color);
			taken.add(idx);
		}
		
		return grid;
	}
}
