package gui;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import inteligenca.Inteligenca;
import vodja.GameType;


public class Window extends JFrame implements ActionListener {
	private static final long serialVersionUID = -3977009338403276682L;
	private GridBagLayout grid;
	private Panel panel;
	private JLabelMultiLine statusBar;
	private JMenuItem humCom, comHum, humHum, comCom, compDelayOption;
	private float fontSize = (float) 20.0;
	/**
	 * @see #getCompDelay
	 */
	private int compDelay = 500;
	
	public Window() {
		super();
		setTitle("Igra Capture Go");
		grid = new GridBagLayout();
		setLayout(grid);

		statusBar = new JLabelMultiLine("Izberite tip igre", JLabel.CENTER);
		statusBar.setFont(statusBar.getFont().deriveFont(fontSize));
		GridBagConstraints consBar = new GridBagConstraints();
		consBar.ipady = 20;
		consBar.weighty = 0.0;
		consBar.fill = GridBagConstraints.HORIZONTAL;
		grid.setConstraints(statusBar, consBar);
		add(statusBar);
		panel = new Panel(500, 600, this);
		GridBagConstraints consPanel = new GridBagConstraints();
		consPanel.gridx = 0;
		consPanel.weighty = 1.0;
		consPanel.weightx = 1.0;
		consPanel.fill = GridBagConstraints.BOTH;
		grid.setConstraints(panel, consPanel);
		add(panel);
		
		JMenuBar menubar = new JMenuBar();
		setJMenuBar(menubar);
		
		JMenu igre = newMenu(menubar, "Igre");
		humCom = newMenuItem(igre, "Človek vs. računalnik...");
		comHum = newMenuItem(igre, "Računalnik vs. človek...");
		humHum = newMenuItem(igre, "Človek vs. človek");
		comCom = newMenuItem(igre, "Računalnik vs. računalnik...");
		
		JMenu options = newMenu(menubar, "Nastavitve");
		compDelayOption = newMenuItem(options, "Hitrost računalnika...");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		update();
	}
	
	private JMenu newMenu(JMenuBar menubar, String name) {
		JMenu newMenu = new JMenu(name);
		menubar.add(newMenu);
		return newMenu;
	}
	
	private JMenuItem newMenuItem(JMenu menu, String name) {
		JMenuItem newItem = new JMenuItem(name);
		menu.add(newItem);
		newItem.addActionListener(this);
		return newItem;
	}

	/**
	 * Update the entire GUI.
	 */
	public void update() {
		repaint();
		panel.repaint();
	}
	
	/**
	 * If the move choosing algorithm takes less that this amount of
	 * time to choose a move, wait till this much time has passed.
	 * (Unit is milliseconds)
	 */
	public int getCompDelay() {
		return compDelay;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// A click on the menu bar at the top of the window.
		Object source = e.getSource();
		if (source == humCom) {
			Inteligenca selected = Popups.getIntelligenceChoice();
			if (selected != null) {
				panel.newComGame(GameType.HUMCOM, selected, this);
			}
		}
		else if (source == comHum) {
			Inteligenca selected = Popups.getIntelligenceChoice();
			if (selected != null) {
				panel.newComGame(GameType.COMHUM, selected, this);
			}
		}
		else if (source == humHum) {
			panel.newHumHumGame(this);
		}
		else if (source == comCom) {
			IntelligencePair selected = Popups.getIntelligencePairChoice();
			if (selected != null) {
				panel.newComComGame(selected, this);
			}
		}
		else if (source == compDelayOption) {
			compDelay = Popups.getDelayOption(compDelay);
		}
		else {
			assert false;
		}
		
		update();
	}

	public void writeMessage(String message) {
		statusBar.setText(message);
	}
}