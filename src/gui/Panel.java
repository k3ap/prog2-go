package gui;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import logika.PlayerColor;
import vodja.ManagedGame;
import logika.Index;
import splosno.Poteza;
import vodja.MoveResult;
import vodja.GameType;


class Panel extends JPanel implements MouseListener {
	private static final long serialVersionUID = 7693008210122811280L;
	private ManagedGame game;
	private Dimension dimensions;
	private Style style;
	private Window window;
	private int leftEdge, topEdge, widthStep, heightStep;

	public Panel(int width, int height, Window window) {
		super();
		this.window = window;
		dimensions = new Dimension(width, height);
		setPreferredSize(dimensions);
		style = new Style();
		game = null;
		addMouseListener(this);
	}
	
	/**
	 * Fills in some of the properties required to draw the grid.
	 */
	private void calculateDimensions() {
		if (game == null) { return; }
		
		int width = getWidth();
		int height = getHeight();
		
		// We need to make sure to draw the grid square
		if (width >= height) {
			// shift it to the middle
			leftEdge = (width - height) / 2;
			topEdge = 0;
			width = height; // to make sure the grid is square
		}
		else {
			leftEdge = 0;
			topEdge = (height - width) / 2;
			height = width;
		}
		
		leftEdge += (int) (width * 0.05);
		widthStep = (int) (width * 0.9 / (game.width() - 1));
		topEdge += (int) (height * 0.05);
		heightStep = (int) (height * 0.9 / (game.height() - 1));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		
		if (game == null) 
			return;
		
		calculateDimensions();

		int gridWidth = game.width();
		int gridHeight = game.height();

		// Can't draw a 1x1 grid
		assert gridWidth > 1;
		assert gridHeight > 1;

		// grid
		g2.setStroke(style.grid);
		g2.setColor(style.gridColor);
		for (int i = 0; i < gridHeight; i++) {
			g2.drawLine(
					leftEdge,
					topEdge + heightStep * i,
					leftEdge + widthStep * (gridWidth - 1),
					topEdge + heightStep * i
			);
		}
		for (int j = 0; j < gridWidth; j++) {
			g2.drawLine(
					leftEdge + widthStep * j,
					topEdge,
					leftEdge + widthStep * j,
					topEdge + heightStep * (gridHeight - 1)
			);
		}
		
		// stones
		for (int i = 0; i < gridHeight; i++) {
			int y = topEdge + i * heightStep - style.stoneDiameter / 2;
			for (int j = 0; j < gridWidth; j++) {
				int x = leftEdge + j * widthStep - style.stoneDiameter / 2;
				
				switch (game.fieldColor(new Index(i, j))) {
				case WHITE:
					drawStone(g2, x, y, PlayerColor.WHITE);
					break;
				case BLACK:
					drawStone(g2, x, y, PlayerColor.BLACK);
					break;
				case EMPTY:
					break;
				}
			}
		}
		
		// draw game status
		switch (game.gameStatus()) {
		case INVALID:
		case PLAY:
			switch (game.playerTurn()) {
			case WHITE:
				window.writeMessage("Na vrsti je bel.");
				break;
			case BLACK:
				window.writeMessage("Na vrsti je črn.");
				break;
			}
			break;
		case WAIT:
			window.writeMessage("Računalnik izbera potezo...");
			break;
		case ERROR:
			window.writeMessage("Program za izbiranje poteze se je sesul.");
			break;
		case WHITEWINS:
			window.writeMessage("Beli igralec je zmagal.");
			break;
		case BLACKWINS:
			window.writeMessage("Črn igralec je zmagal.");
			break;
		}
	}
	
	/**
	 * Draw a stone (colored circle) at the given coordinates. 
	 * @param g2 Graphics on which to draw.
	 * @param x coordinate on the panel on which to draw the stone (not the grid index).
	 * @param y coordinate on the panel on which to draw the stone (not the grid index).
	 * @param color Player color to draw the stone with.
	 */
	private void drawStone(Graphics2D g2, int x, int y, PlayerColor color) {
		Color center = Color.PINK;
		Color edge = Color.CYAN;
		switch (color) {
			case BLACK:
				center = style.blackStone;
				edge = style.blackStoneEdge;
				break;
			case WHITE:
				center = style.whiteStone;
				edge = style.whiteStoneEdge;
			default:
				assert false;
		}
		
		g2.setColor(center);
		g2.fillOval(x, y, style.stoneDiameter, style.stoneDiameter);
		g2.setColor(edge);
		g2.drawOval(x, y, style.stoneDiameter, style.stoneDiameter);
	}
	
	/**
	 * Starts a new game of the given type.
	 * @param type Type of game that is started.
	 */
	protected void newGame(GameType type, Window window) {
		// ManagedGame needs access to the window to update the board 
		// once the computer has decided what to play
		game = new ManagedGame(type, window);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (game == null) { return; }
		// Ignore clicks if the player is not allowed to play a move.
		if (game.gameStatus() != MoveResult.INVALID
			&& game.gameStatus() != MoveResult.PLAY) { return; }
		
		calculateDimensions();
		int x = e.getX();
		int y = e.getY();
		
		// calculate the indices of the field that was clicked
		int i = (y - topEdge + heightStep / 2) / heightStep;
		int j = (x - leftEdge + widthStep / 2) / widthStep;
		// ignore a click if it happened too far outside the grid
		if (i < 0 || i >= game.width() || j < 0 || j >= game.height()) { return; }
		
		game.play(new Poteza(i, j));
		window.update();
	}
	
	// Unused methods of MouseListener
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
}
