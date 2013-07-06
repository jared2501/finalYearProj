package com.jnew528.finalYearProject;

public class TicTacToeMove implements Move {
	protected int boardIndex;

	TicTacToeMove(Integer inputBoardIndex) throws Exception {
		if(inputBoardIndex > 8 || inputBoardIndex < 0) {
			throw new Exception("Move specified is invalid");
		}

		boardIndex = inputBoardIndex;
	}

	TicTacToeMove(String inputBoardIndex) throws Exception {
		this(Integer.parseInt(inputBoardIndex));
	}

	public String toString() {
		return "" + boardIndex;
	}
}