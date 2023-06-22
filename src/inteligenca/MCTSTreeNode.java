package inteligenca;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import logika.GridGo;
import logika.Index;
import logika.PlayerColor;
import splosno.Poteza;

/**
 * A node in the MCTS search tree
 */
public class MCTSTreeNode {
	
	/**
	 * The square of the MCTS constant
	 */
	private final static double CONST = 2.1;
	
	private GridGo state;
	private PlayerColor player;
	private Map<Poteza, MCTSTreeNode> children;
	private double score;
	private int numPlays;
	private Iterator<Poteza> unvisitedMoveIterator;
	private int simulationDepth;
	
	/**
	 * Construct a new tree from the given grid.
	 * @param grid The currect state of the grid
	 */
	public MCTSTreeNode(GridGo grid, PlayerColor player, int simulationDepth) {
		this.state = (GridGo) grid.deepcopy();
		this.player = player;
		this.children = new TreeMap<>();
		this.score = 0;
		this.numPlays = 0;
		this.simulationDepth = simulationDepth;
		
		this.fillChildren(state.fieldsValidForPlayer(player.field()));
	}
	
	private MCTSTreeNode(GridGo grid, PlayerColor previuousPlayer, Poteza move, int simulationDepth) {
		this.state = (GridGo) grid.deepcopy();
		if (move.isPass()) {
			switch(previuousPlayer) {
			case WHITE:
				state.whitePass = true;
				break;
			case BLACK:
				state.blackPass = true;
				break;
			}
		} else {
			this.state.placeColor(new Index(move), previuousPlayer.field());
		}
		this.player = previuousPlayer.next();
		this.children = new TreeMap<>();
		this.score = 0;
		this.numPlays = 0;
		this.simulationDepth = simulationDepth;
		this.fillChildren(this.state.allEmptyFields());
	}

	/**
	 * Initialize children as a map from the valid moves to the subnodes.
	 */
	private void fillChildren(List<Index> validMoves) {
		validMoves.add(new Index(-1, -1));
		Collections.shuffle(validMoves);
		
		for (Index move : validMoves) {
			children.put(move.poteza(), null);
		}
		
		// we assume that children will not change
		this.unvisitedMoveIterator = children.keySet().iterator();
	}
	
	/**
	 * Automatically select a move and expand the playing field.
	 * Perform score backpropagation.
	 * @return How many points this node's player scored in the expansion 
	 */
	private double selectAndExpand() {
		if (!unvisitedMoveIterator.hasNext()) {
			// this is a fully expanded node
			double delta = findBestAndSelect();
			numPlays++;
			score += delta;
			return delta;
		} else {
			Poteza moveToMake = unvisitedMoveIterator.next();
			children.put(moveToMake, new MCTSTreeNode(state, player, moveToMake, simulationDepth));
			double delta = 1 - children.get(moveToMake).simulate();
			score += delta;
			numPlays++;
			return delta;
		}
	}
	
	/**
	 * When at a fully expanded node, use this to find the next node to recursively expand.
	 * @return The change is score for this node.
	 */
	private double findBestAndSelect() {
		Poteza bestMove = null;
		Double bestValue = null;
		for (Poteza move : children.keySet()) {
			double value = children.get(move).calculateNodeValue() 
					+ Math.sqrt(CONST * Math.log(numPlays) / children.get(move).numPlays);
			
			if (bestValue == null || value > bestValue) {
				bestValue = value;
				bestMove = move;
			}
		}
		return 1 - children.get(bestMove).selectAndExpand();
	}
	
	/**
	 * Simulate a random run, starting at this node.
	 * This simulation ignores all placement criteria, except the empty-field criterion 
	 * @return The score granted to this node
	 */
	private double simulate() {
		PlayerColor currentPlayer = player;
		GridGo grid = (GridGo) state.deepcopy();
		for (int moveNumber = 0; moveNumber < simulationDepth; moveNumber++) {
			Index move = grid.getRandomEmptyField();
			grid.placeColor(move, currentPlayer.field());
			currentPlayer = currentPlayer.next();
			grid.captureComponentsOf(currentPlayer.field());
		}
		// because of the komi rule, there are no ties
		double delta = grid.hasColorLost(player.field()) ? 0 : 1;
		numPlays++;
		score += delta;
		return delta;
	}
	
	/**
	 * Calculate (part of) the node value formula.
	 */
	private double calculateNodeValue() {
		return ((double)numPlays - score) / numPlays;
	}
	
	/**
	 * Perform a single run of the algorithm.
	 */
	public void singleRun() {
		selectAndExpand();
	}
	
	public Poteza getBestMove() {
		Poteza best = null;
		int bestNum = -1;
		for (Poteza move : children.keySet()) {
			if (children.get(move) == null) {
				// this is a very bad situation
				// we have fewer runs than possible moves
				break;
			}
			if (best == null || children.get(move).numPlays > bestNum) {
				best = move;
				bestNum = children.get(move).numPlays;
			}
		}
		return best;
	}
}
