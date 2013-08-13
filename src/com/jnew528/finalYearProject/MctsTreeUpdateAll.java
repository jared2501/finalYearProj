package com.jnew528.finalYearProject;

import com.jnew528.finalYearProject.DirectedAcyclicGraph.Edge;
import com.jnew528.finalYearProject.DirectedAcyclicGraph.Node;
import com.jnew528.finalYearProject.DirectedAcyclicGraph.UpdatePath;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Jared
 * Date: 5/07/13
 * Time: 4:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class MctsTreeUpdateAll implements MctsTree {
	protected static Random random;
	protected int collisions;

	static {
		random = new Random();
	}

	public MctsTreeUpdateAll() {}

	@Override
	public Move search(GameState gameState, int iterationCount) {
		// We want to start a-fresh!
		HashMap<GameState, Node> encounteredGameStates = new HashMap();
		Node root = new Node(gameState);
		collisions = 0;

		encounteredGameStates.put(gameState, root);

		for(int i = 0; i < iterationCount; i++) {
			performIteration(root, encounteredGameStates);
		}

		// Select child with the selection policy
		// In this case, the child with the highest number of visits
		int highestVists = 0;
		Edge selectedEdge = root.getChildEdges().get(0);

		for(Edge edge : root.getChildEdges()) {
			if(edge.getVisits() > highestVists) {
				selectedEdge = edge;
				highestVists = edge.getVisits();
			}
		}

		return selectedEdge.getMove();
	}

	public void performIteration(Node root, HashMap<GameState, Node> encounteredGameStates) {
		Node node = root;

		// Traverse the tree until we reach a node on the edge of the current tree
		// i.e. it has untried moves or is a state with no children (i.e. terminal game state)
		select: do {
			node = utcSelect(node);

			// Expand the node if it has untried moves
			// if it doesnt, do nothing, because it is a terminal state and doesnt need expanding
			if(node.hasUntriedMoves()) {
				Move move = node.getUntriedMoves().get(random.nextInt(node.getUntriedMoves().size()));
				GameState newGameState = node.getGameState().createChildStateFromMove(move);

				// Check if there are any transpositions in the encountered states
				Node transposition = newGameState.getTransposition(encounteredGameStates);

				// If we have found a transposition, then make it a child of the current node and continue our iteration
				// from the new transposition
				if(transposition != null) {
					collisions++;
					Edge edgeBetweenNodeAndTransposition = node.addChild(transposition, move);
					node = transposition;
					continue select;
				} else {
					Node newNode = new Node(newGameState);
					Edge newEdge = node.addChild(newNode, move);
					node = newNode;
					encounteredGameStates.put(newGameState, newNode);
				}
			}

			GameState startingGameState = node.getGameState();
			GameState finalGameState = defaultPolicy(node, startingGameState);

			// Back propogate the result from the perspective of the player that just moved
			backpropogate(node, finalGameState);
			break;
		} while(true);
	}

	protected Node utcSelect(Node node) {
		while(!node.hasUntriedMoves() && node.hasChildren()) {
			Edge edge = UpdatePath.uctSelectChild(node);
			node = edge.getHead();
		}

		return node;
	}

	protected GameState defaultPolicy(Node node, GameState gameState) {
		// Play a random game from the current node using the default policy
		// in this case, default policy is to select random moves until a final state is reached
		while(!gameState.isFinalState(true)) {
			Vector<Move> moves = gameState.getChildMoves();
			Move move = moves.get(random.nextInt(moves.size()));
			gameState = gameState.createChildStateFromMove(move);
		}
		return gameState;
	}

	protected void backpropogate(Node finalNode, GameState gameState) {
		Deque<Node> stack = new ArrayDeque();
		stack.push(finalNode);

		while(stack.size() > 0) {
			Node current = stack.pop();

			for(Edge e : current.getParentEdges()) {
					// Update the edges tail
					double result = gameState.getResult(e.getTail().getGameState().getPlayerToMove(), true);
					e.update(result);
					e.getTail().incrementVisits();

					// And add it to the stack to have a geez at
					stack.push(e.getTail());
			}
		}
	}

	@Override
	public int getCollisions() {
		return this.collisions;
	}
}
