package com.jnew528.finalYearProject;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Jared
 * Date: 5/07/13
 * Time: 4:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExtendedMctsTree extends StdMctsTree {
	private HashMap<GameState, StdMctsNode> encounteredGameStates;
	private int collisions;

	ExtendedMctsTree() {
		super();
	}

	@Override
	public Move performSearch(GameState gameState, int iterationCount) {
		// We want to start a-fresh!
		this.collisions = 0;
		this.encounteredGameStates = new HashMap();
		this.encounteredGameStates.put(gameState, root);
		return super.performSearch(gameState, iterationCount);
	}

	public void performIteration() {
		StdMctsNode node = root;

		// Traverse the tree until we reach a node on the edge of the current tree
		// i.e. it has untried moves or is a state with no children (i.e. terminal game state)
		select: do {
			node = utcSelect(node);

			// Expand the node if it has untried moves
			// if it doesnt, do nothing, because it is a terminal state and doesnt need expanding
			if(node.hasUntriedMoves()) {
				Move move = node.getUntriedMoves().get(random.nextInt(node.getUntriedMoves().size()));
				GameState newGameState = node.getGameState().createChildStateFromMove(move);

				node.removeMove(move);

				// Check if there are any transpositions in the encountered states
				StdMctsNode transposition = newGameState.getTransposition(encounteredGameStates);

				if(transposition != null) { // If there are...
					this.collisions++;
					// Only add a reference to the current node if the transposition is NOT in the current nodes children already!
					if(!node.getChildren().contains(transposition)) {
						node.addChild(transposition);
						transposition.addParent(node, move);
						node = transposition;
						continue select;
					}

					node = transposition;
				} else {
					StdMctsNode newNode = new StdMctsNode(node, move, newGameState);
					node.addChild(newNode);
					node = newNode;
					encounteredGameStates.put(newGameState, newNode);
				}
			}

			GameState gameState = node.getGameState();
			gameState = defaultPolicy(node, gameState);

			// Back propogate the result from the perspective of the player that just moved
			backpropogate(node, gameState);
			break;
		} while(true);
	}

	private StdMctsNode utcSelect(StdMctsNode node) {
		while(!node.hasUntriedMoves() && node.hasChildren()) {
			node = node.utcSelectChild();
		}

		return node;
	}

	private GameState defaultPolicy(StdMctsNode node, GameState gameState) {
		// Play a random game from the current node using the default policy
		// in this case, default policy is to select random moves until a final state is reached
		while(!gameState.isFinalState(true)) {
			Vector<Move> moves = gameState.getChildMoves();
			Move move = moves.get(random.nextInt(moves.size()));
			gameState = gameState.createChildStateFromMove(move);
		}
		return gameState;
	}

	private void backpropogate(StdMctsNode node, GameState gameState) {
		// Play a random game from the current node using the default policy
		// in this case, default policy is to select random moves until a final state is reached
		Deque<StdMctsNode> stack = new LinkedList<StdMctsNode>();
		stack.push(node);
		while(!stack.isEmpty()) {
			node = stack.pop();

			if(node != null) {
				double result = gameState.getResult(node.getGameState().getPlayerJustMoved(), true);
				node.update(result);
				for(StdMctsNode parent : node.getParents()) {
					stack.push(parent);
				}
			}
		}
	}

	@Override
	public Integer getCollisions() {
		return this.collisions;
	}
}
