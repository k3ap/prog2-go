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


public class Window extends JFrame implements ActionListener {
	private static final long serialVersionUID = -3977009338403276682L;
	
	private GridBagLayout grid;
	private Panel panel;
	private JLabel statusBar;
	private JMenu gameMenu, optionsMenu;
	private JMenuItem newGame;
	private JMenuItem compDelayOption;
	private JMenuBar menubar;
	
	private float fontSize = (float) 20.0;
	/**
	 * @see #getCompDelay
	 */
	private int compDelay = 500;
	
	public Window() {
		super();
		setTitle("Igra Go");
		
		grid = new GridBagLayout();
		setLayout(grid);
		
		// Add the status bar
		statusBar = new JLabel("Izberite tip igre", JLabel.CENTER);
		statusBar.setFont(statusBar.getFont().deriveFont(fontSize));
		GridBagConstraints consBar = new GridBagConstraints();
		consBar.ipady = 20;
		consBar.weighty = 0.0;
		consBar.fill = GridBagConstraints.HORIZONTAL;
		grid.setConstraints(statusBar, consBar);
		add(statusBar);
		
		// Add the panel for games
		panel = new Panel(1000, 600, this);
		GridBagConstraints consPanel = new GridBagConstraints();
		consPanel.gridx = 0;
		consPanel.weighty = 1.0;
		consPanel.weightx = 1.0;
		consPanel.fill = GridBagConstraints.BOTH;
		grid.setConstraints(panel, consPanel);
		add(panel);
		
		// Add the menu bar, submenus and items
		menubar = new JMenuBar();
		setJMenuBar(menubar);
		
		gameMenu = new JMenu("Igraj");
		menubar.add(gameMenu);
		
		newGame = newMenuItem(gameMenu, "Nova igra...");
		
		optionsMenu = new JMenu("Nastavitve");
		menubar.add(optionsMenu);
		compDelayOption = newMenuItem(optionsMenu, "Hitrost računalnika...");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		update();
	}
	
	private JMenuItem newMenuItem(JMenu menu, String name) {
		JMenuItem newItem = new JMenuItem(name);
		menu.add(newItem);
		newItem.addActionListener(this);
		return newItem;
	}
	
	protected void writeMessage(String message) {
		// use <html> and <br /> to break the text into multiple lines
		int charsPerLine = (int) (getWidth() * 2 / fontSize);
		if (charsPerLine <= 0) {
			charsPerLine = 10;
		}
		
		String[] words = message.split(" ");
		LinkedList<String> lines = new LinkedList<String>();
		lines.add(words[0]);
		for (int i = 1; i < words.length; i++) {
			if (lines.getLast().length() + words[i].length() > charsPerLine) {
				lines.add(words[i]);
			}
			else {
				lines.set(lines.size() - 1, lines.getLast() + " " + words[i]);
			}
		}
		
		String brokenMessage = "<html>";
		for (int i = 0; i < lines.size(); i++) {
			if (i != 0)
				brokenMessage += "<br />";
			brokenMessage += lines.get(i);
		}
		brokenMessage += "</html>";
		statusBar.setText(brokenMessage);
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
		if (source == newGame) {
			newGame();
		}
		else if (source == compDelayOption) {
			compDelay = Popups.getDelayOption(compDelay);
		}
		
		update();
	}
	
	private void newGame() {
		GameParams params = Popups.getGameChoice();
		
		if (params == null)
			return; // the cancel button was pressed
		
		switch (params.gameType()) {
		case COMCOM:
			panel.newComComGame(params.ip(), this, 9, params.goGameType());
			break;
		case COMHUM:
			panel.newComGame(params.gameType(), params.ip().i1(), this, 9, params.goGameType());
			break;
		case HUMCOM:
			panel.newComGame(params.gameType(), params.ip().i2(), this, 9, params.goGameType());
			break;
		case HUMHUM:
			panel.newHumHumGame(this, 9, params.goGameType());
			break;
		}
	}
}