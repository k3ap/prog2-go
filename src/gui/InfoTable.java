package gui;

import java.util.Vector;

import javax.swing.JTable;

import logika.GoGameType;
import logika.PlayerColor;
import vodja.ManagedGame;

public class InfoTable {
	public Vector<Vector<String>> data;
	private Vector<String> column;
	private JTable table;
	
	public InfoTable() {
		data = new Vector<Vector<String>>();
		column = new Vector<String>();
		column.add("Lastnost");
		column.add("Črn");
		column.add("Bel");
		table = new JTable(data, column) {
		    private static final long serialVersionUID = 4588881758826517817L;
			@Override
		    public boolean isCellEditable(int row, int column) {
		    	// disable editing cells
		        return false;
		    }
		};
		table.setRowSelectionAllowed(false);
		table.setColumnSelectionAllowed(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	}
	
	public JTable getTable() {
		return table;
	}
	
	public void updateTable(ManagedGame game) {
		data.clear();
		
		if (game != null) {
			Vector<String> stoneCount = new Vector<String>();
			stoneCount.add("Kamni");
			stoneCount.add(Integer.toString(game.stoneCount(PlayerColor.BLACK)));
			stoneCount.add(Integer.toString(game.stoneCount(PlayerColor.WHITE)));
			data.add(stoneCount);
			
			if (game.goGameType().equals(GoGameType.GO)) {
				Vector<String> captured = new Vector<String>();
				captured.add("Izgubljeni");
				captured.add(Integer.toString(game.captured(PlayerColor.BLACK)));
				captured.add(Integer.toString(game.captured(PlayerColor.WHITE)));
				data.add(captured);

				Vector<String> points = new Vector<String>();
				points.add("Točke");
				points.add(Integer.toString(game.calculatePoints(PlayerColor.BLACK)));
				points.add(Integer.toString(game.calculatePoints(PlayerColor.WHITE)) + ".5");
				data.add(points);
			}
		}
		
		table.updateUI();
	}
}
