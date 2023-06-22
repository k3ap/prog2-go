package gui;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;

import logika.GoGameType;
import splosno.Poteza;


public class Window extends JFrame implements ActionListener {
	private static final long serialVersionUID = -3977009338403276682L;
	
	private GridBagLayout grid;
	private Panel panel;
	private JLabel statusBar;
	private InfoTable infoTable;
	private JScrollPane infoTableScrollPane;
	private JMenu gameMenu, optionsMenu;
	private JMenuItem newGame;
	private JMenuItem compDelayOption, colorsOption;
	private JMenuBar menubar;
	private JButton newGameButton, passButton;
	
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
		consBar.weightx = 1.0;
		consBar.fill = GridBagConstraints.HORIZONTAL;
		grid.setConstraints(statusBar, consBar);
		add(statusBar);
		
		// add the new game button
		newGameButton = new JButton("Nova igra...");
		newGameButton.addActionListener(this);
		GridBagConstraints consButton = new GridBagConstraints();
		consButton.weighty = 0.0;
		consButton.gridy = 1;
		consButton.weightx = 1.0;
		consButton.insets = new Insets(5, 5, 5, 5);
		grid.setConstraints(newGameButton, consButton);
		add(newGameButton);
		
		// pass button
		passButton = new JButton("Izpusti potezo");
		passButton.addActionListener(this);
		grid.setConstraints(passButton, consButton);
		passButton.setVisible(false);
		add(passButton);
		
		
		// Add the panel for games
		panel = new Panel(1000, 600, this);
		GridBagConstraints consPanel = new GridBagConstraints();
		consPanel.gridx = 0;
		consPanel.gridy = 2;
		consPanel.weighty = 1.0;
		consPanel.weightx = 1.0;
		consPanel.fill = GridBagConstraints.BOTH;
		grid.setConstraints(panel, consPanel);
		add(panel);
		
		infoTableScrollPane = new JScrollPane();
		infoTable = new InfoTable();
		infoTableScrollPane.setViewportView(infoTable.getTable());
		infoTable.getTable().setFont(infoTable.getTable().getFont().deriveFont(fontSize));
		infoTable.getTable().setRowHeight((int) fontSize * 2);
		GridBagConstraints constTable = new GridBagConstraints();
		constTable.gridy = 2;
		constTable.gridx = 1;
		constTable.ipadx = 100;
		constTable.weightx = 0.5;
		constTable.fill = GridBagConstraints.BOTH;
		constTable.insets = new Insets(10, 10, 10, 10);
		grid.setConstraints(infoTableScrollPane, constTable);
		//infoTableScrollPane.setVisible(false);
		add(infoTableScrollPane);
		
		// Add the menu bar, submenus and items
		menubar = new JMenuBar();
		setJMenuBar(menubar);
		
		gameMenu = new JMenu("Igraj");
		menubar.add(gameMenu);
		
		newGame = newMenuItem(gameMenu, "Nova igra...");
		
		optionsMenu = new JMenu("Nastavitve");
		menubar.add(optionsMenu);
		compDelayOption = newMenuItem(optionsMenu, "Hitrost računalnika...");
		colorsOption =  newMenuItem(optionsMenu, "Barva ozadja...");

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
		infoTable.updateTable(panel.getGame());
		if (panel.getGame() != null) {
			if (panel.getGame().gameStatus().isWonGame()) {
				// show the button for a new game if the current one is over
				newGameButton.setVisible(true);
				passButton.setVisible(false);
			}
			else if (panel.getGame().goGameType() == GoGameType.GO) {
				// pass button is visible but
				// it's only enabled if the player can play a move right now
				passButton.setVisible(true);
				passButton.setEnabled(panel.getGame().gameStatus().canMakeMove());
			}
			else {
				// FCGO is being played, show neither button
				newGameButton.setVisible(false);
				passButton.setVisible(false);
			}
		}
		else {
			// there is no game
			passButton.setVisible(false);
			newGameButton.setVisible(true);
		}
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
		if (source == newGame || source == newGameButton) {
			if (newGame())
				newGameButton.setVisible(false);
		}
		else if (source == passButton) {
			if (Popups.getPassConfirmation(panel.getGame())) {
				panel.playMove(Poteza.pass());
			}
		}
		else if (source == compDelayOption) {
			compDelay = Popups.getDelayOption(compDelay);
		}
		else if (source == colorsOption) {
			Color newColor = JColorChooser.showDialog(this, "Barva ozadja", panel.style.background);
			if (newColor != null) {
				panel.style.background = newColor;
			}
		}
		
		update();
	}
	
	private boolean newGame() {
		GameParams params = Popups.getGameChoice();
		
		if (params == null)
			return false; // the cancel button was pressed
		
		switch (params.gameType()) {
		case COMCOM:
			panel.newComComGame(params.ip(), this, params.size(), params.goGameType());
			break;
		case COMHUM:
			panel.newComGame(params.gameType(), params.ip().i1(), this, params.size(), params.goGameType());
			break;
		case HUMCOM:
			panel.newComGame(params.gameType(), params.ip().i2(), this, params.size(), params.goGameType());
			break;
		case HUMHUM:
			panel.newHumHumGame(this, params.size(), params.goGameType());
			break;
		}
		
		infoTableScrollPane.setVisible(true);
		update();
		
		return true;
	}
}