package com.jnew528.finalYearProject;

import java.util.HashMap;
import java.util.Vector;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Jared
 * Date: 24/07/13
 * Time: 7:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class GobangState implements GameState<GobangMove> {
	int size;
	int numInRow;
	Integer playerJustMoved;
	int[][] board;
	// Stores the x and y position of the last place you moved on the board
	int winner;
	boolean isFinalState;


	GobangState(int size, int numInRow) {
		this.size = size;
		this.numInRow = numInRow;
		this.playerJustMoved = 2;
		this.board = new int[size][size];

		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				this.board[i][j] = 0;
			}
		}

		this.winner = 0;
		this.isFinalState = false;
	}

	GobangState() {
		this(11, 5);
	}

	GobangState(int boardSize) {
		this(boardSize, 5);
	}

	private int[][] deepCopy(int[][] input) {
		int[][] target = new int[input.length][];
		for (int i=0; i <input.length; i++) {
			target[i] = Arrays.copyOf(input[i], input[i].length);
		}
		return target;
	}

	GobangState(GobangState oldState) {
		this.size = oldState.size;
		this.numInRow = oldState.numInRow;
		this.playerJustMoved = oldState.playerJustMoved;
		this.board = deepCopy(oldState.board);
		this.winner = oldState.winner;
		this.isFinalState = oldState.isFinalState;
	}

	@Override
	public Integer getPlayerJustMoved() {
		return playerJustMoved;
	}

	@Override
	public Integer getPlayerToMove() {
		return 3 - playerJustMoved;
	}

	@Override
	public boolean isFinalState(boolean quickCheck) {
		return isFinalState;
	}

	@Override
	public Double getResult(Integer playerNumber, boolean quickCheck) {
		if(!isFinalState) {
			return null;
		} else {
			if(winner == playerNumber) {
				return 1.0;
			} else if(winner == 0) {
				return 0.5;
			} else {
				return 0.0;
			}
		}
	}

	@Override
	public Integer getWinner(boolean quickCheck) {
		if(!isFinalState) {
			return null;
		}

		return winner;
	}

	public GameState createChildStateFromMove(TicTacToeMove move) {
		GobangMove newMove = new GobangMove(move.boardIndex/3, move.boardIndex%3);
		return this.createChildStateFromMove(newMove);
	}

	@Override
	public GameState createChildStateFromMove(GobangMove move) {
		if(this.board[move.r][move.c] != 0) {
			System.out.println("Players already moved at this position");
			return null;
		}

		GobangState newState = new GobangState(this);
		newState.playerJustMoved = 3 - newState.playerJustMoved;
		newState.board[move.r][move.c] = newState.playerJustMoved; // Make move
		newState.determineWinner(move.r, move.c); // See if theres a winner from the last place moved
		newState.isFinalState = newState.winner != 0 || !newState.hasChildMoves(); // If there is a winner, or theres no more moves, its a finalstate

		return newState;
	}

	private boolean hasChildMoves() {
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				if(this.board[i][j] == 0) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public Vector getChildMoves() {
		Vector<GobangMove> childMoves = new Vector<GobangMove>();

		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				if(this.board[i][j] == 0) {
					childMoves.add(new GobangMove(i,j));
				}
			}
		}

		return childMoves;
	}

	private int[][] rotateBoardCW90deg(int[][] input) {
		int[][] output = new int[input.length][input.length];

		for(int i = 0; i < input.length; i++) {
			for(int j = 0; j < input.length; j++) {
				output[i][j] = input[input.length - j - 1][i];
			}
		}

		return output;
	}

	private int[][] reflectBoardHorizontal(int[][] input) {
		int[][] output = new int[input.length][input.length];

		for(int i = 0; i < input.length; i++) {
			output[i] = input[input.length - 1 - i].clone();
		}

		return output;
	}

	private int[][] reflectBoardVertical(int[][] input) {
		int[][] output = new int[input.length][input.length];

		for(int i = 0; i < input.length; i++) {
			for(int j = 0; j < input.length; j++) {
				output[i][j] = input[i][input.length - 1 - j];
			}
		}

		return output;
	}

	@Override
	public StdMctsNode getTransposition(HashMap<GameState, StdMctsNode> encounteredGamestates) {
		if(encounteredGamestates.containsKey(this)) {
			return encounteredGamestates.get(this);
		}

		int[][] newBoard = this.board;

		// Rotate 3 times
		for(int i = 0; i < 3; i++) {
			newBoard = rotateBoardCW90deg(newBoard);
			GobangState temp = new GobangState(this);
			temp.board = newBoard;
			GameState test = ((GameState) temp);
			if(encounteredGamestates.get(test) != null) {
				return encounteredGamestates.get(test);
			}
		}

		// Reflect horizontal
		newBoard = reflectBoardHorizontal(this.board);
		GobangState temp = new GobangState(this);
		temp.board = newBoard;
		GameState test = ((GameState) temp);
		if(encounteredGamestates.get(test) != null) {
			return encounteredGamestates.get(test);
		}

		// Reflect vertical
		newBoard = reflectBoardVertical(this.board);
		temp = new GobangState(this);
		temp.board = newBoard;
		test = ((GameState) temp);
		if(encounteredGamestates.get(test) != null) {
			return encounteredGamestates.get(test);
		}

		return null;
	}

	private void determineWinner(int lastR, int lastC) {
		// Edge case: first move
		if(lastR < 0 || lastC < 0) {
			this.winner = 0;
		}

		// Now we search from the position the player that just moved has played and try and fine 5 in a row!
		int[] count = {0,0,0,0, 0,0,0,0}; // U,R,D,L, UL,UR,DR,DL
		boolean[] status = {true,true,true,true, true,true,true,true};
		for(int i = 1; status[0] || status[1] || status[2] || status[3]  ||  status[4] || status[5] || status[6] || status[7]; i++) {
			// Look up
			if(status[0] && lastR-i >= 0 && board[lastR-i][lastC] == playerJustMoved) {
				count[0]++;
			} else {
				status[0] = false;
			}

			// Look right
			if(status[1] && lastC+i < size && board[lastR][lastC+i] == playerJustMoved) {
				count[1]++;
			} else {
				status[1] = false;
			}

			// Look down
			if(status[2] && lastR+i < size && board[lastR+i][lastC] == playerJustMoved) {
				count[2]++;
			} else {
				status[2] = false;
			}

			// Look left
			if(status[3] && lastC-i >= 0 && board[lastR][lastC-i] == playerJustMoved) {
				count[3]++;
			} else {
				status[3] = false;
			}


			// Look UL
			if(status[4] && lastR-i >= 0 && lastC-i >= 0 && board[lastR-i][lastC-i] == playerJustMoved) {
				count[4]++;
			} else {
				status[4] = false;
			}

			// Look UR
			if(status[5] && lastR-i > -1 && lastC+i < size && board[lastR-i][lastC+i] == playerJustMoved) {
				count[5]++;
			} else {
				status[5] = false;
			}

			// Look DR
			if(status[6] && lastR+i < size && lastC+i < size && board[lastR+i][lastC+i] == playerJustMoved) {
				count[6]++;
			} else {
				status[6] = false;
			}

			// Look DL
			if(status[7] && lastR+i < size && lastC-i > -1 && board[lastR+i][lastC-i] == playerJustMoved) {
				count[7]++;
			} else {
				status[7] = false;
			}


			// Check if either up/down or left/right or UL+DR or UR/DL sum to 5!
			if(count[0] + count[2] + 1 >= numInRow || count[1] + count[3] + 1 >= numInRow  ||  count[4] + count[6] + 1 >= numInRow || count[5] + count[7] + 1 >= numInRow) {
				this.winner = playerJustMoved;
				return;
			}
		}

		this.winner = 0;
		return;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(); // default size is 16 chars which shouild be enough

		for(int i = 0; i < size; i++) { // rows
			for(int j = 0; j < size; j++) { // cols
				if(board[i][j] == 1) {
					sb.append('X');
				} else if(board[i][j] == 2) {
					sb.append('O');
				} else {
					sb.append('_');
				}
			}
			sb.append(System.lineSeparator());
		}

		sb.append(System.lineSeparator());
		return sb.toString();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + playerJustMoved.hashCode();
		result = prime * result + Arrays.deepHashCode(board);

		return result;
	}

	@Override
	public boolean equals(Object other){
		if(other == null) return false;
		if(other == this) return true;
		if(this.getClass() != other.getClass()) return false;
		GobangState otherGBState = (GobangState)other;
		return otherGBState.playerJustMoved == this.playerJustMoved
				&& Arrays.deepEquals(otherGBState.board, this.board);
	}
}
