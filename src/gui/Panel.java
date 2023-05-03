package gui;
import javax.swing.JPanel;

import inteligenca.Inteligenca;

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
	private int leftEdge, topEdge, widthStep, heightStep;

	public Panel(int width, int height) {
		super();
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
		
		int width = getSize().width;
		int height = getSize().height;
		
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
		
		leftEdge += (int) (width * 0.1);
		widthStep = (int) (width * 0.8 / (game.width() - 1));
		topEdge += (int) (height * 0.1);
		heightStep = (int) (height * 0.8 / (game.height() - 1));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		
		if (game == null) {
			// increase font size
			g2.setFont(g2.getFont().deriveFont((float) 30.0));
			g2.drawString("Izberite tip igre", dimensions.width / 3, dimensions.height / 2);
			return;
		}
		
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
				writeMessage(g2, "Na vrsti je bel.");
				break;
			case BLACK:
				writeMessage(g2, "Na vrsti je črn.");
				break;
			}
			break;
		case WAIT:
			writeMessage(g2, "Računalnik izbera potezo...");
			break;
		case ERROR:
			writeMessage(g2, "Program za izpibarnje poteze se je sesul.");
			break;
		case WHITEWINS:
			writeMessage(g2, "Beli igralec je zmagal.");
			break;
		case BLACKWINS:
			writeMessage(g2, "Črn igralec je zmagal.");
			break;
		case ALLCOMPUTERS:
			writeMessage(g2, "Računalnik igra proti samemu sebi...");
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
	 * Draws a message at the above the grid.
	 * @param g2 Graphics on which to draw.
	 * @param message Text that will be written.
	 */
	private void writeMessage(Graphics2D g2, String message) {
		float size = (float) (20.0 * getSize().height / 700.0);
		g2.setFont(g2.getFont().deriveFont(size));
		g2.drawString(message, leftEdge, (int) (topEdge - size));
	}
	
	/**
	 * Starts a new game with the type HUMHUM.
	 */
	protected void newHumHumGame(Window window) {
		// ManagedGame needs access to the window to update the board 
		// once the computer has decided what to play
		game = new ManagedGame(GameType.HUMHUM, window);
		repaint();
	}
	
	/**
	 * Starts a new game with the given type with intelligence playing one of the sides.
	 * @param type Game type being played, has to be either HUMCOM or COMHUM.
	 * @param intelligence The intelligence playing.
	 */
	protected void newComGame(GameType type, Inteligenca intelligence, Window window) {
		// ManagedGame needs access to the window to update the board 
		// once the computer has decided what to play
		assert(type == GameType.HUMCOM || type == GameType.COMHUM);
		game = new ManagedGame(type, intelligence, window);
		repaint();
	}
	
	/**
	 * Starts a new game of the type COMCOM.
	 * @param intelligences The pair of intelligences playing against each other.
	 */
	protected void newComComGame(IntelligencePair intelligences, Window window) {
		// ManagedGame needs access to the window to update the board 
		// once the computer has decided what to play
		game = new ManagedGame(GameType.COMCOM, intelligences, window);
		repaint();
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
		repaint();
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
