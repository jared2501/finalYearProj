package com.jnew528.finalYearProject;

import com.jnew528.finalYearProject.DirectedAcyclicGraph.Edge;
import com.jnew528.finalYearProject.DirectedAcyclicGraph.Node;

import java.util.HashMap;

public class MctsTreeLearner<T> extends MctsTreeUpdatePath {

	protected HashMap<GameState, Node> encounteredGameStates;

	public MctsTreeLearner() {
		super();
		encounteredGameStates = new HashMap();
	}

	@Override
	public Move search(GameState gameState, int iterationCount) {
		collisions = 0;
		Node root = gameState.getTransposition(encounteredGameStates);

		// NOTE!! In games like GoBang where rotating the board is a transposition, but the child moves change then we
		// neeed a way to change the transposition back into a normal board.
		// Transpositions dont matter when we're simulating since it just means we can never reach certain board stats
		// ever since theyre transposition is already in the tree so we consider them!
		if(root == null) {
			root = new Node(gameState);
			encounteredGameStates.put(gameState, root);
		} else {
			collisions++;
		}

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

		return gameState.convertMove(root.getGameState(), selectedEdge.getMove());
	}
}
