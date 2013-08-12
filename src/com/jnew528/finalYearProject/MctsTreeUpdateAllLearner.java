package com.jnew528.finalYearProject;

import com.jnew528.finalYearProject.DirectedAcyclicGraph.Edge;
import com.jnew528.finalYearProject.DirectedAcyclicGraph.Node;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Jared
 * Date: 12/08/13
 * Time: 2:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class MctsTreeUpdateAllLearner extends MctsTreeUpdateAll {

	protected HashMap<GameState, Node> encounteredGameStates;

	public MctsTreeUpdateAllLearner() {
		super();
	}

	@Override
	public Move search(GameState gameState, int iterationCount) {
		return null;
	}
}
