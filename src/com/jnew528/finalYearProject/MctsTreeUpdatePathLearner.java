package com.jnew528.finalYearProject;

import com.jnew528.finalYearProject.DirectedAcyclicGraph.Edge;
import com.jnew528.finalYearProject.DirectedAcyclicGraph.Node;

import java.util.HashMap;

public class MctsTreeUpdatePathLearner extends MctsTreeUpdatePath {

	protected HashMap<GameState, Node> encounteredGameStates;

	public MctsTreeUpdatePathLearner() {
		super();
		encounteredGameStates = new HashMap();
	}

	@Override
	public Move search(GameState gameState, int iterationCount) {
		collisions = 0;
		Node root = gameState.getTransposition(encounteredGameStates);

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

		return selectedEdge.getMove();
	}
}
