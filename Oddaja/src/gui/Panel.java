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
import logika.GoGameType;
import logika.Index;
import splosno.Poteza;
import vodja.MoveResult;
import vodja.GameType;


class Panel extends JPanel implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 7693008210122811280L;
	/**
	 * The game being played.
	 */
	private ManagedGame game;
	private Dimension dimensions;
	/**
	 * The style for drawing the board.
	 */
	public Style style;
	/**
	 * The parent window.
	 */
	private Window window;
	/**
	 * Hovering over a valid vertex draws a "shadow" (transparent) stone at it.
	 */
	private Index shadow;
	/**
	 * Coordinates of the top left edge of the board and the height / width of each square.
	 */
	private int leftEdge, topEdge, widthStep, heightStep, sideLength;
	/**
	 * The size (in grid intersections) of the current game.
	 * null if there is no game
	 */
	private Integer gameSize;

	public Panel(int width, int height, Window window) {
		super();
		this.window = window;
		dimensions = new Dimension(width, height);
		setPreferredSize(dimensions);
		style = new Style();
		game = null;
		shadow = null;
		gameSize = null;
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	public ManagedGame getGame() {
		return game;
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
		assert width == height;
		
		sideLength = width;
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
		
		// background
		g2.setBackground(style.background);
		g2.clearRect(leftEdge - widthStep / 2, topEdge - heightStep / 2, gridWidth * widthStep, gridHeight * heightStep);

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
		
		// mark the last stone that was placed
		if (game.lastMove() != null) {
			if (!game.lastMove().isPass()) {
				Index idx = new Index(game.lastMove());
				Color markColor = null;
				switch (game.fieldColor(idx)) {
				case BLACK:
					markColor = style.whiteStone;
					break;
				case WHITE:
					markColor = style.blackStone;
					break;
				case EMPTY:
					assert false;
					break;
				}
				drawMark(g2, idx.i(), idx.j(), markColor);
			}
		}
		
		// shadow stone
		if (shadow != null) {
			drawShadowStone(g2, shadow.i(), shadow.j(), game.playerTurn());
		}
		
		// draw game status 
		String prefix = "";
		PlayerColor prevPlayer = game.playerTurn().next();
		if (game.didPass(prevPlayer)) {
			switch (prevPlayer) {
			case BLACK:
				prefix = "Črn igralec je izpustil potezo. ";
				break;
			case WHITE:
				prefix = "Bel igralec je izpustil potezo. ";
				break;
			}
		}
		switch (game.gameStatus()) {
		case INVALID:
		case PLAY:
			if (game.gameType().mixedGame()) {
				// mixedGame => there is only one human player
				window.writeMessage(prefix + "Na vrsti ste.");
			}
			else {
				switch (game.playerTurn()) {
				case WHITE:
					window.writeMessage(prefix + "Na vrsti je bel.");
					break;
				case BLACK:
					window.writeMessage(prefix + "Na vrsti je črn.");
					break;
				}
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
			if (game.goGameType() == GoGameType.GO)
				drawControlAndPrisoners(g2);
			else
				highlightLoser(g2);
				
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
		
		// update the tool tip
		MoveResult status = game.gameStatus();
		if (status.canMakeMove() && game.goGameType() == GoGameType.GO) {
			this.setToolTipText("Desni klik za izpust poteze.");
		}
		else if (status.isWonGame()) {
			if (game.goGameType() == GoGameType.GO)
				this.setToolTipText("Znak + označuje ujetnika, znak x pa nadzor nad praznim poljem.");
		}
		else {
			this.setToolTipText(null);
		}
	}
	
	/**
	 * Used in FC Go, highlights the component that was captured.
	 */
	private void highlightLoser(Graphics2D g2) {
		Index[] loser = game.losingComponent();
		g2.setColor(style.losingHighlight);
		for (Index idx : loser) {
			g2.fillRect(
					leftEdge + (int) ((idx.j() - 0.5 ) * widthStep),
					topEdge + (int) ((idx.i() - 0.5 ) * widthStep),
					widthStep,
					heightStep
			);
		}
	}

	/**
	 * Draw a stone (colored circle) at the given coordinates. 
	 * @param g2 Graphics on which to draw.
	 * @param i grid index (row) on which to draw the stone.
	 * @param j grid index (column) on which to draw the stone.
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
	 * @param i grid index (row) on which to draw the stone.
	 * @param j grid index (column) on which to draw the stone.
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
	 * @return The diameter a stone should be drawn with.
	 */
	private int stoneDiameter() {
		return style.stoneDiameter * sideLength * 9 / 500 / gameSize;
	}
	
	/**
	 * Do not call this method directly.
	 */
	private void drawAbstractStone(Graphics2D g2, int i, int j, Color center, Color edge) {
		int d = stoneDiameter();
		int y = topEdge + i * heightStep - d / 2;
		int x = leftEdge + j * widthStep - d / 2;

		g2.setColor(center);
		g2.fillOval(x, y, d, d);
		g2.setColor(edge);
		g2.drawOval(x, y, d, d);
	}
	
	/**
	 * Draw the mark (small circle) that indicates the latest stone
	 * that was placed.
	 * @param g2 The graphics on which to draw on.
	 * @param i grid index (row) on which to draw the mark.
	 * @param j grid index (column) on which to draw the mark.
	 * @param color The color for the mark.
	 */
	private void drawMark(Graphics2D g2, int i, int j, Color color) {
		int d = stoneDiameter() / 3; // mark is smaller that the stones
		int y = topEdge + i * heightStep - d / 2;
		int x = leftEdge + j * widthStep - d / 2;

		g2.setColor(color);;
		g2.drawOval(x, y, d, d);
	}
	
	/**
	 * Draw a black or white cross symbolizing control over
	 * an empty field.
	 * @param g2 Graphics on which to draw.
	 * @param i grid index (row) on which to draw the cross.
	 * @param j grid index (column) on which to draw the cross.
	 * @param color Player color to draw the cross with.
	 */
	private void markControl(Graphics2D g2, int i, int j, PlayerColor color) {
		Color line = Color.PINK;
		switch (color) {
			case BLACK:
				line = style.blackPrisonersControl;
				break;
			case WHITE:
				line = style.whitePrisonersControl;
			default:
				assert false;
		}
		
		int y = topEdge + i * heightStep;
		int x = leftEdge + j * widthStep;

		g2.setColor(line);
		g2.setStroke(style.controlPrisonerStroke);
		int r = widthStep / 6;
		g2.drawLine(x - r, y - r, x + r, y + r);
		g2.drawLine(x + r, y - r, x - r, y + r);
	}
	
	/**
	 * Draw a black or white plus symbolizing a stone being a prisoner.
	 * @param g2 Graphics on which to draw.
	 * @param i grid index (row) on which to draw the plus.
	 * @param j grid index (column) on which to draw the plus.
	 * @param color Player color to draw the cross with.
	 */
	private void markPrisoner(Graphics2D g2, int i, int j, PlayerColor color) {
		Color line = Color.PINK;
		switch (color) {
			case BLACK:
				line = style.blackPrisonersControl;
				break;
			case WHITE:
				line = style.whitePrisonersControl;
			default:
				assert false;
		}
		
		int y = topEdge + i * heightStep;
		int x = leftEdge + j * widthStep;

		g2.setColor(line);
		g2.setStroke(style.controlPrisonerStroke);
		int r = widthStep / 6;
		g2.drawLine(x - r, y, x + r, y);
		g2.drawLine(x, y - r, x, y + r);
	}
	
	/**
	 * Pares a Poteza object from coordinates on the panel.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return The Poteza of the field at the given coordinates, null if the field is invalid.
	 */
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
	 * Starts a new game with the type HUMHUM.
	 */
	protected void newHumHumGame(Window window, int gameSize, GoGameType gameType) {
		game = new ManagedGame(GameType.HUMHUM, window, gameType, gameSize);
		this.gameSize = gameSize;
		repaint();
	}
	
	/**
	 * Starts a new game with the given type with intelligence playing one of the sides.
	 * @param type Game type being played, has to be either HUMCOM or COMHUM.
	 * @param intelligence The intelligence playing.
	 */
	protected void newComGame(GameType type, Inteligenca intelligence, Window window, int gameSize, GoGameType gameType) {
		assert type == GameType.HUMCOM || type == GameType.COMHUM;
		game = new ManagedGame(type, window, gameType, gameSize);
		game.setIntelligence(intelligence);
		this.gameSize = gameSize;
		repaint();
	}
	
	/**
	 * Starts a new game of the type COMCOM.
	 * @param intelligences The pair of intelligences playing against each other.
	 */
	protected void newComComGame(IntelligencePair intelligences, Window window, int gameSize, GoGameType gameType) {
		game = new ManagedGame(GameType.COMCOM, window, gameType, gameSize);
		game.setIntelligences(intelligences);
		this.gameSize = gameSize;
		repaint();
	}
	
	/**
	 * Called when a game of Go is over.
	 * Highlight which stones have been labeled as prisoners and which
	 * empty fields have been labeled as controlled.
	 * @param g2 The graphics on which to draw.
	 */
	private void drawControlAndPrisoners(Graphics2D g2) {
		for (PlayerColor player : PlayerColor.values()) {
			if (game.controlledZones(player) == null)
				continue;
			for (Index idx : game.controlledZones(player)) {
				markControl(g2, idx.i(), idx.j(), player);
			}
			for (Index idx : game.prisonersOf(player)) {
				markPrisoner(g2, idx.i(), idx.j(), player);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (game == null) { return; }
		// Ignore clicks if the player is not allowed to play a move.
		if (!game.gameStatus().canMakeMove()) { return; }
		
		Poteza move = null;
		if (e.getButton() == MouseEvent.BUTTON1) {
			move = moveFromXY(e.getX(), e.getY());
		}
		else if (e.getButton() == MouseEvent.BUTTON3) {
			if (game.goGameType().equals(GoGameType.GO)) {
				move = Poteza.pass();
			}
			// otherwise leave move as null, FCGO has no passes
		}
		playMove(move); // playMove(null) does nothing.
	}
	
	/**
	 * Play the given move on the board and update the window.
	 * If move == null nothing happens.
	 * @param move The move to be played.
	 */
	public void playMove(Poteza move) {
		if (move != null) {
			game.play(move);
			if (!move.isPass()) {
				shadow = null;
			}
			window.update();
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		// draw a shadow stone when the mouse hovers over a valid field
		// don't draw anything if the player can't make a move 
		if (game == null) { return; }
		if (!game.gameStatus().canMakeMove()) { return; }
		
		Poteza hoveredMaybe = moveFromXY(e.getX(), e.getY());
		if (hoveredMaybe == null) {
			shadow = null;
		}
		else {
			Index hovered = new Index(hoveredMaybe);
			if (game.isValid(hovered)) {
				// paintComponent draws the shadow.
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
		// remove the shadow
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
