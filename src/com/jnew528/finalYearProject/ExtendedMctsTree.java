package com.jnew528.finalYearProject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Jared
 * Date: 5/07/13
 * Time: 4:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExtendedMctsTree extends StdMctsTree {
	private HashMap encounteredGameStates;
	private int test = 0;

	ExtendedMctsTree(GameState gameState) {
		super(gameState);
		encounteredGameStates = new HashMap();
		encounteredGameStates.put(gameState, root);
	}

	public void performIteration() {
		StdMctsNode node = root;

		// Traverse the tree until we reach a node on the edge of the current tree
		// i.e. it has untried moves or is a state with no children (i.e. terminal game state)
		while(!node.hasUntriedMoves() && node.hasChildren()) {
			node = node.utcSelectChild();
		}

		// Expand the node if it has untried moves
		// if it doesnt, do nothing, because it is a terminal state and doesnt need expanding
		if(node.hasUntriedMoves()) {
			Move move = node.getUntriedMoves().get(random.nextInt(node.getUntriedMoves().size()));
			GameState newGameState = node.getGameState().createChildStateFromMove(move);

			node.removeMove(move);

			// If we've come across this new game state before we want to make the corresponding node of this game state
			// then we want to add child to this encountered node instead of creating a new node
//			if(encounteredGameStates.containsKey(newGameState)) {
//				node.addChild((StdMctsNode) encounteredGameStates.get(newGameState));
//				((StdMctsNode) encounteredGameStates.get(newGameState)).addParent(node, move);
//			} else {
				StdMctsNode newNode = new StdMctsNode(node, move, newGameState);
				node.addChild(newNode);
				node = newNode;
				if(encounteredGameStates.containsKey(newGameState)) {
					test++;
					System.out.println(test);
				}
				encounteredGameStates.put(newGameState, newNode);
//			}
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
}
