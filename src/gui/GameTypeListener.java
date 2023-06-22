package gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;

import inteligenca.Inteligenca;
import logika.GoGameType;

public class GameTypeListener implements ItemListener {
	private JComboBox<Inteligenca> blackCombo, whiteCombo;
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
		
		blackCombo.removeAllItems();
		whiteCombo.removeAllItems();
		for (Inteligenca i : desired) {
			blackCombo.addItem(i);
			whiteCombo.addItem(i);
		}
	}

}
