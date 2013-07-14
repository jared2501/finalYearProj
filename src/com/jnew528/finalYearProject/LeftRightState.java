package com.jnew528.finalYearProject;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Jared
 * Date: 15/07/13
 * Time: 1:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class LeftRightState implements GameState<LeftRightMove> {
	private Integer playerJustMoved;
	private int size;

	private int player1R;
	private int player2R;
	private int iteration;

	LeftRightState() {
		this(300);
	}

	LeftRightState(Integer size) {
		this.playerJustMoved = 2;
		this.size = size;

		this.iteration = 0;
		this.player1R = 0;
		this.player2R = 0;
	}

	LeftRightState(LeftRightState clone) {
		this.playerJustMoved = clone.playerJustMoved;
		this.size = clone.size;

		this.iteration = clone.iteration;
		this.player1R = clone.player1R;
		this.player2R = clone.player2R;
	}

	@Override
	public Integer getPlayerJustMoved() {
		return this.playerJustMoved;
	}

	@Override
	public Integer getPlayerToMove() {
		return 3 - this.playerJustMoved;
	}

	@Override
	public boolean isFinalState(boolean quickCheck) {
		return this.iteration == size;
	}

	@Override
	public Double getResult(Integer playerNumber, boolean quickCheck) {
		if(!isFinalState(quickCheck)) {
			return null;
		}

		if(this.player1R > this.player2R) {
			if(playerNumber == 1) {
				return 1.0;
			} else {
				return 0.0;
			}
		} else if(this.player1R < this.player2R) {
			if(playerNumber == 2) {
				return 1.0;
			} else {
				return 0.0;
			}
		}

		return 0.5;
	}

	@Override
	public Integer getWinner(boolean quickCheck) {
		if(!isFinalState(quickCheck)) {
			return null;
		}

		if(this.player1R > this.player2R) {
			return 1;
		} else if(this.player1R < this.player2R) {
			return 2;
		}

		return 0;
	}

	@Override
	public GameState createChildStateFromMove(LeftRightMove move) {
		if(isFinalState(true)) {
			System.out.println("Game of left right has already ended...");
			return null;
		}

		LeftRightState gameState = new LeftRightState(this);
		gameState.playerJustMoved = 3 - gameState.playerJustMoved;

		// Even iterations = player 1
		// Else if Odd iterations = player 2
		if(gameState.iteration % 2 == 0 && move.getRight()) {
			gameState.player1R++;
		} else if(move.getRight()) {
			gameState.player2R++;
		}

		gameState.iteration++;

		return gameState;
	}

	@Override
	public Vector getChildMoves() {
		Vector<LeftRightMove> output = new Vector<LeftRightMove>();

		output.add(new LeftRightMove("L"));
		output.add(new LeftRightMove("R"));

		return output;
	}

	@Override
	public StdMctsNode getTransposition(HashMap<GameState, StdMctsNode> set) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public String toString() {
		return null;
	}

	@Override
	public boolean equals(Object other){
		if(other == null) return false;
		if(other == this) return true;
		if(this.getClass() != other.getClass()) return false;
		HexState otherHexState = (HexState)other;
		return otherHexState.size == this.size
				&& otherHexState.playerJustMoved == this.playerJustMoved
				&& Arrays.equals(otherHexState.board, this.board);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		// Dont need player just moved in hashcode since board implicitly has this in it
		result = prime * result + Arrays.hashCode(board);

		return result;
	}
}
