package gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;

import inteligenca.Inteligenca;
import logika.GoGameType;

/**
 * Listen to changes if the JComboBox for selecting game type (FC Go or Go)
 * and update the JComboBoxes for picking the opponent AI, since the options
 * for Go and FC Go are different.
 */
public class GameTypeListener implements ItemListener {
	/**
	 * The dropdowns for picking the black and white intelligences.
	 */
	private JComboBox<Inteligenca> blackCombo, whiteCombo;
	/**
	 * The lists of available intelligences for both games.  
	 */
	private Inteligenca[] fc, go;
	
	public GameTypeListener(JComboBox<Inteligenca> blackCombo, JComboBox<Inteligenca> whiteCombo, Inteligenca[] fc, Inteligenca[] go) {
		super();
		this.blackCombo = blackCombo;
		this.whiteCombo = whiteCombo;
		this.fc = fc;
		this.go = go;
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		GoGameType selected = (GoGameType) e.getItem();
		Inteligenca[] desired;
		switch (selected) {
		case FCGO:
			desired = fc;
			break;
		case GO:
			desired = go;
			break;
		default:
			System.out.println("Invalid item selected " +  selected);
			return;
		}
		
		// Reset the available options to the ones corresponding to the
		// selected game.
		blackCombo.removeAllItems();
		whiteCombo.removeAllItems();
		for (Inteligenca i : desired) {
			blackCombo.addItem(i);
			whiteCombo.addItem(i);
		}
	}

}
