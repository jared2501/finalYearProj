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
	private StdMctsNode root;
	private Random random;

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

		return selectedNode.getMove();
	}

	public void performIteration() {
		StdMctsNode node = root;

		// Traverse the tree until we reach an expandable node
		// ie a node that has untried moves and non-terminal
		while(!node.hasUntriedMoves() && node.hasChildren()) {
			try {
				node = node.utcSelectChild();
			} catch (Exception e) { System.exit(1);	}
		}

		// Expand the node if it has untried moves
		// if it doesnt, do nothing, because it is a terminal state and doesnt need expanding
		if(node.hasUntriedMoves()) {
			Move move = node.getUntriedMoves().get(random.nextInt(node.getUntriedMoves().size()));
			try {
				GameState newGameState = node.getGameState().createChildStateFromMove(move);
				StdMctsNode newNode = new StdMctsNode(move, node, newGameState);
				node = node.addChild(newNode);
			} catch (Exception e) { System.exit(1);	}
		}

		// Play a random game from the current node using the default policy
		// in this case, default policy is to select random moves until a final state is reached
		GameState gameState = node.getGameState();
		while(!gameState.isFinalState()) {
			Vector<Move> moves = gameState.getChildMoves();
			Move move = moves.get(random.nextInt(moves.size()));
			try {
				gameState = gameState.createChildStateFromMove(move);
			} catch (Exception e) { System.exit(1); }
		}

		// Back propogate the result from the perspective of the player that just moved
		try {
			while(node != null) {
				double result = gameState.getResult(node.getGameState().getPlayerJustMoved());
				node.update(result);
				node = node.getParent();
			}
		} catch (Exception e) { System.exit(1); }
	}


	public class StdMctsNode {
		private Move move;
		private GameState gameState;

		private double wins;
		private int visits;

		private Vector<Move> untriedMoves;
		private Vector<StdMctsNode> children;
		private StdMctsNode parent;

		StdMctsNode(Move move, StdMctsNode parent, GameState gameState) {
			this.move = move;
			this.gameState = gameState;
			this.wins = 0.0;
			this.visits = 0;
			this.untriedMoves = gameState.getChildMoves();
			this.children = new Vector(untriedMoves.size());
			this.parent = parent;
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

		public Move getMove() {
			return move;
		}

		public StdMctsNode getParent() {
			return parent;
		}

		public Vector<StdMctsNode> getChildren() {
			return children;
		}

		public int getVisits() {
			return visits;
		}

		public StdMctsNode addChild(StdMctsNode child) {
			children.add(child);
			untriedMoves.remove(child.getMove());
			return child;
		}

		public StdMctsNode utcSelectChild() throws Exception {
			if(!hasChildren()) {
				throw new Exception("Cannot select child since this node has no children");
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
