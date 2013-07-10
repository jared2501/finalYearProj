package com.jnew528.finalYearProject;

import java.util.Random;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Jared
 * Date: 5/07/13
 * Time: 4:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class StdMctsTree {
	protected StdMctsNode root;
	protected Random random;

	StdMctsTree(GameState gameState) {
		root = new StdMctsNode(null, null, gameState);
		random = new Random();
	}

	public Move performSearch(int iterationCount) {
		for(int i = 0; i < iterationCount; i++) {
			performIteration();
		}

		// Select child with the selection policy
		// In this case, the child with the highest number of visits
		int highestVists = 0;
		StdMctsNode selectedNode = root.getChildren().get(0);

		for(StdMctsNode node : root.getChildren()) {
			if(node.getVisits() > highestVists) {
				selectedNode = node;
				highestVists = node.getVisits();
			}
		}

		return selectedNode.getMoves().get(0);
	}

	public void performIteration() {
		StdMctsNode node = root;

		// Traverse the tree until we reach an expandable node
		// ie a node that has untried moves and non-terminal
		while(!node.hasUntriedMoves() && node.hasChildren()) {
			node = node.utcSelectChild();
		}

		// Expand the node if it has untried moves
		// if it doesnt, do nothing, because it is a terminal state and doesnt need expanding
		if(node.hasUntriedMoves()) {
			Move move = node.getUntriedMoves().get(random.nextInt(node.getUntriedMoves().size()));
			GameState newGameState = node.getGameState().createChildStateFromMove(move);

			// Remove the move that we used to expand the node and add the new node generated to the tree
			StdMctsNode newNode = new StdMctsNode(node, move, newGameState);
			node.removeMove(move);
			node.addChild(newNode);
			node = newNode;
		}

		// Play a random game from the current node using the default policy
		// in this case, default policy is to select random moves until a final state is reached
		GameState gameState = node.getGameState();
		while(!gameState.isFinalState(true)) {
			Vector<Move> moves = gameState.getChildMoves();
			Move move = moves.get(random.nextInt(moves.size()));
			gameState = gameState.createChildStateFromMove(move);
		}

		// Back propogate the result from the perspective of the player that just moved
		while(node != null) {
			double result = gameState.getResult(node.getGameState().getPlayerJustMoved(), true);
			node.update(result);
			node = node.getParents().get(0);
		}
	}


	public class StdMctsNode {
		private GameState gameState;
		private double wins;
		private int visits;

		private Vector<Move> moves;
		private Vector<Move> untriedMoves;
		private Vector<StdMctsNode> children;
		private Vector<StdMctsNode> parents;

		StdMctsNode(StdMctsNode parent, Move move, GameState gameState) {
			this.gameState = gameState;
			this.wins = 0.0;
			this.visits = 0;
			this.untriedMoves = gameState.getChildMoves();
			this.children = new Vector(untriedMoves.size());
			this.parents = new Vector();
			this.moves = new Vector();
			this.parents.add(parent);
			this.moves.add(move);
		}

		public boolean hasUntriedMoves() {
			return untriedMoves.size() > 0;
		}

		public boolean hasChildren() {
			return children.size() > 0;
		}

		public Vector<Move> getUntriedMoves() {
			return untriedMoves;
		}

		public GameState getGameState() {
			return gameState;
		}

		public Vector<Move> getMoves() {
			return moves;
		}

		public Vector<StdMctsNode> getParents() {
			return parents;
		}

		public Vector<StdMctsNode> getChildren() {
			return children;
		}

		public int getVisits() {
			return visits;
		}

		public void addChild(StdMctsNode child) {
			children.add(child);
		}

		public void addParent(StdMctsNode parent, Move move) {
			moves.add(move);
			parents.add(parent);
		}

		public void removeMove(Move move) {
			untriedMoves.remove(move);
		}

		public StdMctsNode utcSelectChild() {
			if(!hasChildren()) {
				return null;
			}

			// Find the child from the list of children with the highest mcts value
			double highestUtcValue = 0.0;
			StdMctsNode selectedChild = children.get(0);

			for(StdMctsNode child : children) {
				double childUtcValue = child.getUtcValue(visits);

				if(childUtcValue > highestUtcValue) {
					selectedChild = child;
					highestUtcValue = childUtcValue;
				}
			}

			return selectedChild;
		}

		public Double getUtcValue(int parentVists) {
			return wins/(1e-6 + visits) + Math.sqrt( (2*Math.log((double)parentVists)) / (1e-6 + visits) ) + random.nextDouble()*1e-6;
		}

		public void update(double result) {
			visits++;
			wins = wins + result;
		}
	}
}