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
	private int collisions = 0;

	ExtendedMctsTree(GameState gameState) {
		super(gameState);
		encounteredGameStates = new HashMap();
		encounteredGameStates.put(gameState, root);
	}

	public void performIteration() {
		StdMctsNode node = root;

		// Traverse the tree until we reach a node on the edge of the current tree
		// i.e. it has untried moves or is a state with no children (i.e. terminal game state)
		select: do {
			while(!node.hasUntriedMoves() && node.hasChildren()) {
				node = node.utcSelectChild();
			}

			// Expand the node if it has untried moves
			// if it doesnt, do nothing, because it is a terminal state and doesnt need expanding
			if(node.hasUntriedMoves()) {
				Move move = node.getUntriedMoves().get(random.nextInt(node.getUntriedMoves().size()));
				GameState newGameState = node.getGameState().createChildStateFromMove(move);

				node.removeMove(move);

				// Check if there are any transpositions in the encountered states
				StdMctsNode transposition = newGameState.getTransposition(encounteredGameStates);

				if(transposition != null) { // If there are...
	//				System.out.println(collisions++);
					// Only add a reference to the current node if the transposition is NOT in the current nodes children already!
					if(!node.getChildren().contains(transposition)) {
						node.addChild(transposition);
						transposition.addParent(node, move);
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

			// Play a random game from the current node using the default policy
			// in this case, default policy is to select random moves until a final state is reached
			GameState gameState = node.getGameState();
			while(!gameState.isFinalState(true)) {
				Vector<Move> moves = gameState.getChildMoves();
				Move move = moves.get(random.nextInt(moves.size()));
				gameState = gameState.createChildStateFromMove(move);
			}

			// Back propogate the result from the perspective of the player that just moved
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
			break;
		} while(true);
	}
}
