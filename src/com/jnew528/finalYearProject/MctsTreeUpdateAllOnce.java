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
public class MctsTreeUpdateAllOnce extends MctsTreeUpdateAll {

	@Override
	protected void backpropogate(Node finalNode, GameState gameState) {
		HashSet<Node> seenNodes = new HashSet();
		Deque<Node> stack = new ArrayDeque();
		stack.push(finalNode);

		while(stack.size() > 0) {
			Node current = stack.pop();

			for(Edge e : current.getParentEdges()) {

				if(!seenNodes.contains(e.getTail())) {
					seenNodes.add(e.getTail());

					// Update the edges tail
					double result = gameState.getResult(e.getTail().getGameState().getPlayerToMove(), true);
					e.update(result);
					e.getTail().incrementVisits();

					// And add it to the stack to have a geez at
					stack.push(e.getTail());
				}
			}
		}

		finalNode.incrementVisits();
	}
}
