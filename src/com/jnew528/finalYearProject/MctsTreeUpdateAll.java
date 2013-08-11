package com.jnew528.finalYearProject;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Jared
 * Date: 5/07/13
 * Time: 4:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class MctsTreeUpdateAll implements MctsTree {
	@Override
	public Move search(GameState gameState, int iterationCount) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public int getCollisions() {
		return 0;  //To change body of implemented methods use File | Settings | File Templates.
	}
//	protected static Random random;
//	protected int collisions;
//
//	static {
//		random = new Random();
//	}
//
//	MctsTreeUpdateAll() {}
//
//	@Override
//	public Move search(GameState gameState, int iterationCount) {
//		// We want to start a-fresh!
//		HashMap<GameState, StdMctsNode> encounteredGameStates = new HashMap();
//		StdMctsNode root = new StdMctsNode(null, null, gameState);
//		collisions = 0;
//
//		encounteredGameStates.put(gameState, root);
//
//		for(int i = 0; i < iterationCount; i++) {
//			performIteration(root, encounteredGameStates);
//		}
//
//		// Select child with the selection policy
//		// In this case, the child with the highest number of visits
//		int highestVists = 0;
//		StdMctsNode selectedNode = root.getChildren().get(0);
//
//		for(StdMctsNode node : root.getChildren()) {
//			if(node.getVisits() > highestVists) {
//				selectedNode = node;
//				highestVists = node.getVisits();
//			}
//		}
//
//		// Now go through the parents that lead to the child and find the parent that is THIS node
//		// And, therefore find the move that leads from THIS node to the selectedChild
//		int i = 0;
//		for(StdMctsNode node : selectedNode.getParents()) {
//			if(root == node) {
//				return selectedNode.getMoves().get(i);
//			}
//			i++;
//		}
//
//		return null;
//	}
//
//	public void performIteration(StdMctsNode root, HashMap<GameState, StdMctsNode> encounteredGameStates) {
//		StdMctsNode node = root;
//
//		// Traverse the tree until we reach a node on the edge of the current tree
//		// i.e. it has untried moves or is a state with no children (i.e. terminal game state)
//		select: do {
//			node = utcSelect(node);
//
//			// Expand the node if it has untried moves
//			// if it doesnt, do nothing, because it is a terminal state and doesnt need expanding
//			if(node.hasUntriedMoves()) {
//				Move move = node.getUntriedMoves().get(random.nextInt(node.getUntriedMoves().size()));
//				GameState newGameState = node.getGameState().createChildStateFromMove(move);
//
//				node.removeMove(move);
//
//				// Check if there are any transpositions in the encountered states
//				StdMctsNode transposition = newGameState.getTransposition(encounteredGameStates);
//
//				if(transposition != null) { // If there are...
//					collisions++;
//					// Only add a reference to the current node if the transposition is NOT in the current nodes children already!
//					if(!node.getChildren().contains(transposition)) {
//						node.addChild(transposition);
//						transposition.addParent(node, move);
//						node = transposition;
//						continue select;
//					}
//
//					node = transposition;
//				} else {
//					StdMctsNode newNode = new StdMctsNode(node, move, newGameState);
//					node.addChild(newNode);
//					node = newNode;
//					encounteredGameStates.put(newGameState, newNode);
//				}
//			}
//
//			GameState gameState = node.getGameState();
//			gameState = defaultPolicy(node, gameState);
//
//			// Back propogate the result from the perspective of the player that just moved
//			backpropogate(node, gameState);
//			break;
//		} while(true);
//	}
//
//	private StdMctsNode utcSelect(StdMctsNode node) {
//		while(!node.hasUntriedMoves() && node.hasChildren()) {
//			node = node.utcSelectChild();
//		}
//
//		return node;
//	}
//
//	private GameState defaultPolicy(StdMctsNode node, GameState gameState) {
//		// Play a random game from the current node using the default policy
//		// in this case, default policy is to select random moves until a final state is reached
//		while(!gameState.isFinalState(true)) {
//			Vector<Move> moves = gameState.getChildMoves();
//			Move move = moves.get(random.nextInt(moves.size()));
//			gameState = gameState.createChildStateFromMove(move);
//		}
//		return gameState;
//	}
//
//	private void backpropogate(StdMctsNode node, GameState gameState) {
//		// Play a random game from the current node using the default policy
//		// in this case, default policy is to select random moves until a final state is reached
//		Deque<StdMctsNode> stack = new LinkedList<StdMctsNode>();
//		Set<StdMctsNode> updatedAlready = new HashSet<StdMctsNode>();
//
//		stack.push(node);
//		while(!stack.isEmpty()) {
//			node = stack.pop();
//
//			// If the node is not null, and we haven't already updated it!
//			if(node != null && !updatedAlready.contains(node)) {
//				double result = gameState.getResult(node.getGameState().getPlayerJustMoved(), true);
//				node.update(result);
//				updatedAlready.add(node);
//				for(StdMctsNode parent : node.getParents()) {
//					stack.push(parent);
//				}
//			}
//		}
//	}
//
//	@Override
//	public int getCollisions() {
//		return this.collisions;
//	}
}
