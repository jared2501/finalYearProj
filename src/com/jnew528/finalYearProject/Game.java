package com.jnew528.finalYearProject;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Jared
 * Date: 14/07/13
 * Time: 11:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class Game implements Callable<GameState> {

	private GameState gameState;
	private StdMctsTree player1;
	private StdMctsTree player2;
	private Integer iterations;

	Game(GameState gameState, StdMctsTree player1, StdMctsTree player2, Integer iterations) {
		this.gameState = gameState;
		this.player1 = player1;
		this.player2 = player2;
		this.iterations = iterations;
	}

	@Override
	public GameState call() {
		System.out.println("Game started");

		while(!gameState.isFinalState(false)) {
			Move move = null;

			if( gameState.getPlayerToMove() ==  1) {
				move = player1.performSearch(gameState, this.iterations);
			} else {
				move = player2.performSearch(gameState, this.iterations);
			}

			gameState = gameState.createChildStateFromMove(move);
		}

		System.out.println("Game finished");

		this.player1 = null;
		this.player2 = null;

		return this.gameState;
	}

}
