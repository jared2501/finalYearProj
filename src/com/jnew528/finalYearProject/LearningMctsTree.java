package com.jnew528.finalYearProject;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Jared
 * Date: 11/08/13
 * Time: 4:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class LearningMctsTree implements MctsTree {
	protected static Random random;
	// Stores ALL the states encountered for the lifetime of the class
	protected HashMap<GameState, StdMctsNode> encounteredGameStates;
	protected int collisions;

	static {
		random = new Random();
	}

	LearningMctsTree() {
		this.encounteredGameStates = new HashMap();
	}

	@Override
	public Move search(GameState gameState, int iterationCount) {
		StdMctsNode root = gameState.getTransposition(encounteredGameStates);
		collisions = 0;

		if(root == null) {
			root = new StdMctsNode(null, null, gameState);
			this.encounteredGameStates.put(gameState, root);
		} else {
			collisions++;
		}

		for(int i = 0; i < iterationCount; i++) {
			performIteration(root);
		}

		// Select child with the selection policy
		// In this case, the child with the highest number of visits
		int highestVists = 0;
		StdMctsNode selectedNode = root.getChildren().get(0);

		// Find the child with the highest number of visits
		for(StdMctsNode node : root.getChildren()) {
			if(node.getVisits() > highestVists) {
				selectedNode = node;
				highestVists = node.getVisits();
			}
		}

		// Now go through the parents that lead to the child and find the parent that is THIS node
		// And, therefore find the move that leads from THIS node to the selectedChild
		int i = 0;
		for(StdMctsNode node : selectedNode.getParents()) {
			if(root == node) {
				return selectedNode.getMoves().get(i);
			}
			i++;
		}

		return null;
	}

	public void performIteration(StdMctsNode root) {
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
		Set<StdMctsNode> updatedAlready = new HashSet<StdMctsNode>();

		stack.push(node);
		while(!stack.isEmpty()) {
			node = stack.pop();

			// If the node is not null, and we haven't already updated it!
			if(node != null && !updatedAlready.contains(node)) {
				double result = gameState.getResult(node.getGameState().getPlayerJustMoved(), true);
				node.update(result);
				updatedAlready.add(node);
				for(StdMctsNode parent : node.getParents()) {
					stack.push(parent);
				}
			}
		}
	}

	@Override
	public int getCollisions() {
		return collisions;
	}
}
