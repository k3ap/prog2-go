package inteligenca;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import logika.GridGo;
import logika.Index;
import logika.PlayerColor;
import splosno.Poteza;

/**
 * A node in the MCTS search tree.
 * Represents the entire subtree, as it holds references to child nodes.
 */
public class MCTSTreeNode {
	
	/**
	 * The square of the MCTS constant.
	 */
	private final static double CONST = 2.1;
	
	/**
	 * The game state that the node is based on.
	 */
	private GridGo state;
	
	/**
	 * Which player is on turn to play; this is part of the game state,
	 * but not part of the grid.
	 */
	private PlayerColor player;
	
	/**
	 * Each move spawns a subtree.
	 */
	private Map<Poteza, MCTSTreeNode> children;
	
	/**
	 * The total score this node received from simulations.
	 */
	private double score;
	
	/**
	 * How many simulations we've performed in the subtree.
	 */
	private int numPlays;
	
	/**
	 * A list which holds all moves that we can make
	 */
	private List<Poteza> validMoves;
	
	/**
	 * A pointer to the first unexpanded node in 'validMoves'
	 */
	private Iterator<Poteza> unvisitedMoveIterator;
	
	/**
	 * How many moves a simulation should perform before checking for victory.
	 */
	private int simulationDepth;
	
	/**
	 * Construct a new tree from the given grid.
	 * @param grid The current state of the grid
	 */
	public MCTSTreeNode(GridGo grid, PlayerColor player, int simulationDepth) {
		this.state = (GridGo) grid.deepcopy();
		this.player = player;
		this.children = new HashMap<>();
		this.score = 0;
		this.numPlays = 0;
		this.simulationDepth = simulationDepth;
		
		// this is a root node, so we fill 'children' with truly valid moves only.
		// Checking for validity takes a very long time, so all other nodes are filled
		// with empty board positions; but we can't do that here, as we still
		// need to return valid moves when asked
		this.fillChildren(state.fieldsValidForPlayer(player.field()));
	}
	
	/**
	 * Build a child node.
	 * @param grid The grid of the parent node; this is copied for the child
	 * @param previuousPlayer The parent's player
	 * @param move The move the given player played
	 */
	private MCTSTreeNode(GridGo grid, PlayerColor previuousPlayer, Poteza move, int simulationDepth) {
		this.state = (GridGo) grid.deepcopy();
		
		// perform the move
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
		this.children = new HashMap<>();
		this.score = 0;
		this.numPlays = 0;
		this.simulationDepth = simulationDepth;
		
		// this is not the root node, so we won't need to provide a valid bestMove()
		// this also means that we might perform moves which are actually illegal;
		// we hope that they don't affect the score all that much
		this.fillChildren(this.state.allEmptyFields());
	}

	/**
	 * Initialize children as a map from the valid moves to the subnodes.
	 */
	private void fillChildren(List<Index> validMoves) {
		
		this.validMoves = new ArrayList<>();
		
		// we only consider passing if there's already a lot of
		// stones on the board
		if (validMoves.size() < state.width() * state.height() / 2)
			validMoves.add(new Index(-1, -1));
		
		for (Index move : validMoves) {
			this.validMoves.add(move.poteza());
		}
		
		// instead of picking a random move each time,
		// we just shuffle the list at the start
		Collections.shuffle(this.validMoves);
		
		// we assume that this.validMoves will not change,
		// so this iterator won't become invalid. 
		this.unvisitedMoveIterator = this.validMoves.iterator();
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
			// we can make a child node
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
	 * @return The change in score for this node.
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
			
			// we don't forbid suicidal moves here
			grid.captureComponentsOf(currentPlayer.next().field());
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
			if (best == null || children.get(move).numPlays > bestNum) {
				best = move;
				bestNum = children.get(move).numPlays;
			}
		}
		return best;
	}
}
