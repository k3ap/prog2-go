package testiranje;

import java.io.FileNotFoundException;
import java.io.IOException;

import logika.FieldColor;
import logika.Grid;
import logika.Index;
import logika.PlayerColor;


/**
 * Popolna preiskava nekega stanja polja v namen testiranja
 */
public class CompleteSearch {
	
	private record TwoNumbers(int a, int b) {}
	
	private static TwoNumbers search(Grid grid, PlayerColor player, int depth, boolean doOutput) {
		int victories = 0;
		int losses = 0;
		for (Index idx : grid.freeFields()) {
			grid.placeColor(idx, player.field());
			if (doOutput)
				System.out.println(grid);
			
			if (grid.hasColorLost(player.next().field())) {
				if (doOutput)
					System.out.println("Zmaga za " + player);
				
				victories++;
				
			} else if (grid.hasColorLost(player.field())) {
				if (doOutput)
					System.out.println("Izguba za " + player);
				
				losses++;
				
			} else {
				
				if (doOutput)
					System.out.format("Branchpoint (depth=%d).\n", depth);
				
				TwoNumbers ns = search(grid, player.next(), depth+1, doOutput);
				victories += ns.b;
				losses += ns.a;
				if (doOutput)
					System.out.format("Branchpoint (depth=%d) backtrack. Zmage/izgube za %s: %d/%d\n", depth, player.toString(), ns.b, ns.a);
			}
			grid.placeColor(idx, FieldColor.EMPTY);
		}
		return new TwoNumbers(victories, losses);
	}

	public static void main(String[] args) {
		/*Grid grid = RandomGridGenerator.randomGrid(9, 0.7);
		grid.printToFile("autogen-primeri/najnovejsi.m9");*/
		
		Grid grid = null;
		try {
			grid = Grid.readFromFile("autogen-primeri/1.m9", 9);
		} catch (Exception e) {
			System.out.println("error");
			System.exit(1);
		}
		
		PlayerColor color = PlayerColor.BLACK;
		
		System.out.println(grid);
		
		System.out.println();
		System.out.format("minSS za crno: %d\n", grid.minimumNumberOfLiberties(FieldColor.BLACK));
		System.out.format("minSS za belo: %d\n", grid.minimumNumberOfLiberties(FieldColor.WHITE));
		
		TwoNumbers ns = search(grid, color, 0, false);
		System.out.format("Zmage/izgube za %s: %d/%d\n", color.toString(), ns.a, ns.b);
		
		
	}

}
