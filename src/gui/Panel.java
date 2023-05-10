package gui;
import javax.swing.JPanel;

import inteligenca.Inteligenca;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import logika.PlayerColor;
import vodja.ManagedGame;
import logika.FieldColor;
import logika.Index;
import splosno.Poteza;
import vodja.MoveResult;
import vodja.GameType;


class Panel extends JPanel implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 7693008210122811280L;
	private ManagedGame game;
	private Dimension dimensions;
	private Style style;
	private Window window;
	private Index shadow;
	private int leftEdge, topEdge, widthStep, heightStep;

	public Panel(int width, int height, Window window) {
		super();
		this.window = window;
		dimensions = new Dimension(width, height);
		setPreferredSize(dimensions);
		style = new Style();
		game = null;
		shadow = null;
		addMouseListener(this);
		addMouseMotionListener(this);
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
			for (int j = 0; j < gridWidth; j++) {
				switch (game.fieldColor(new Index(i, j))) {
				case WHITE:
					drawStone(g2, i, j, PlayerColor.WHITE);
					break;
				case BLACK:
					drawStone(g2, i, j, PlayerColor.BLACK);
					break;
				case EMPTY:
					break;
				}
			}
		}
		
		// shadow stone
		if (shadow != null) {
			drawShadowStone(g2, shadow.i(), shadow.j(), game.playerTurn());
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
			String ime = game.intelligenceName();
			window.writeMessage("Algoritem " + ime + " izbira potezo...");
			break;
		case ERROR:
			window.writeMessage("Program za izbiranje poteze se je sesul.");
			break;
		case WHITEWINS:
		case BLACKWINS:
			switch (game.getOutcome()) {
			case COMBLACKWON:
				window.writeMessage("Algoritem " + game.intelligence1Name() + " (črni) je zmagal.");
				break;
			case COMWHITEWON:
				window.writeMessage("Algoritem " + game.intelligence2Name() + " (beli) je zmagal.");
				break;
			case COMWON:
				window.writeMessage("Zmagal je računalnik (" + game.intelligenceName() + ").");
				break;
			case HUMBLACKWON:
				window.writeMessage("Zmagal je igralec črnih.");
				break;
			case HUMWHITEWON:
				window.writeMessage("Zmagal je igralec belih.");
				break;
			case HUMWON:
				window.writeMessage("Zmagali ste.");
				break;
			}
			break;
		case ALLCOMPUTERS:
			window.writeMessage("Računalnik igra proti samemu sebi...");
			break;
		}
	}
	
	/**
	 * Draw a stone (colored circle) at the given coordinates. 
	 * @param g2 Graphics on which to draw.
	 * @param i grid index on which to draw the stone.
	 * @param j grid index on the panel on which to draw the stone.
	 * @param color Player color to draw the stone with.
	 */
	private void drawStone(Graphics2D g2, int i, int j, PlayerColor color) {
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
		
		drawAbstractStone(g2, i, j, center, edge);
	}
	
	/**
	 * Draw a shadow of a stone at the given coordinates.
	 * A shadow is drawn when hovering over a valid spot for a stone.
	 * @param g2 Graphics on which to draw.
	 * @param x coordinate on the panel on which to draw the stone (not the grid index).
	 * @param y coordinate on the panel on which to draw the stone (not the grid index).
	 * @param color Player color to draw the stone with.
	 */
	private void drawShadowStone(Graphics2D g2, int i, int j, PlayerColor color) {
		Color center = Color.PINK;
		Color edge = Color.CYAN;
		switch (color) {
			case BLACK:
				center = style.blackShadowStone;
				edge = style.blackShadowStoneEdge;
				break;
			case WHITE:
				center = style.whiteShadowStone;
				edge = style.whiteShadowStoneEdge;
			default:
				assert false;
		}
		
		drawAbstractStone(g2, i, j, center, edge);
	}
	
	/**
	 * Do not call this method directly.
	 */
	private void drawAbstractStone(Graphics2D g2, int i, int j, Color center, Color edge) {
		int y = topEdge + i * heightStep - style.stoneDiameter / 2;
		int x = leftEdge + j * widthStep - style.stoneDiameter / 2;
		
		g2.setColor(center);
		g2.fillOval(x, y, style.stoneDiameter, style.stoneDiameter);
		g2.setColor(edge);
		g2.drawOval(x, y, style.stoneDiameter, style.stoneDiameter);
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
	
	private Poteza moveFromXY(int x, int y) {
		calculateDimensions();
		
		// calculate the indices of the field that was clicked
		int i = (y - topEdge + heightStep / 2) / heightStep;
		int j = (x - leftEdge + widthStep / 2) / widthStep;
		// ignore a click if it happened too far outside the grid
		if (i < 0 || i >= game.width() || j < 0 || j >= game.height())
			return null;
		
		return new Poteza(i, j);
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
		
		game.play(moveFromXY(e.getX(), e.getY()));
		shadow = null; // remove the shadow once a stone is placed
		window.update();
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		if (game == null) { return; }
		// Ignore clicks if the player is not allowed to play a move.
		if (game.gameStatus() != MoveResult.INVALID
			&& game.gameStatus() != MoveResult.PLAY) { return; }
		
		Poteza hoveredMaybe = moveFromXY(e.getX(), e.getY());
		if (hoveredMaybe == null) {
			shadow = null;
		}
		else {
			Index hovered = new Index(hoveredMaybe);
			if (game.fieldColor(hovered) == FieldColor.EMPTY) {
				shadow = hovered;
			}
			else {
				shadow = null;
			}
		}
		window.update();
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		shadow = null;
		window.update();
	}
	
	// Unused methods of MouseListener and MouseMotionListener
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseDragged(MouseEvent e) {}

}
