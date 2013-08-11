package com.jnew528.finalYearProject.DirectedAcyclicGraph;

import com.jnew528.finalYearProject.GameState;
import com.jnew528.finalYearProject.MctsTree;
import com.jnew528.finalYearProject.MctsTreeUpdatePath;
import com.jnew528.finalYearProject.Move;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Jared
 * Date: 12/08/13
 * Time: 2:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class MctsTreeLearner extends MctsTreeUpdatePath {

	protected HashMap<GameState, Node> encounteredGameStates;

	public MctsTreeLearner() {
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
