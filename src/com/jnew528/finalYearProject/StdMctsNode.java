package com.jnew528.finalYearProject;

import com.jnew528.finalYearProject.GameState;
import com.jnew528.finalYearProject.Move;

import java.util.Random;
import java.util.Vector;

public class StdMctsNode {
	private static Random random;

	static {
		random = new Random();
	}

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