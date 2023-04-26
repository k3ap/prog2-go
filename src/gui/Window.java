package gui;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import vodja.GameType;


public class Window extends JFrame implements ActionListener {
	private static final long serialVersionUID = -3977009338403276682L;
	private GridBagLayout grid;
	private Panel panel;
	private JLabel statusBar;
	private JMenuItem humCom, comHum, humHum;
	
	public Window() {
		super();
		setTitle("Igra Capture Go");
		grid = new GridBagLayout();
		setLayout(grid);
		
		statusBar = new JLabel("Izberite tip igre", JLabel.CENTER);
		statusBar.setFont(statusBar.getFont().deriveFont((float) 20.0));
		GridBagConstraints consBar = new GridBagConstraints();
		consBar.ipady = 20;
		consBar.weighty = 0.0;
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
		humCom = newMenuItem(igre, "Človek vs. računalnik");
		comHum = newMenuItem(igre, "Računalnik vs. človek");
		humHum = newMenuItem(igre, "Človek vs. človek");

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
	
	protected void writeMessage(String message) {
		statusBar.setText(message);
	}

	/**
	 * Update the entire GUI.
	 */
	public void update() {
		repaint();
		panel.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// A click on the menu bar at the top of the window.
		Object source = e.getSource();
		if (source == humCom) {
			panel.newGame(GameType.HUMCOM, this);
		}
		else if (source == comHum) {
			panel.newGame(GameType.COMHUM, this);
		}
		else if (source == humHum) {
			panel.newGame(GameType.HUMHUM, this);
		}
		else {
			assert false;
		}
		
		update();
	}
}