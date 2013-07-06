package com.jnew528.finalYearProject;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

public final class TicTacToeState implements GameState<TicTacToeMove> {
	private Integer[] board = {0,0,0,0,0,0,0,0,0};
	private Integer playerJustMoved = 2;
	private Vector<TicTacToeMove> childMoves;
	private Random random = new Random();

	public TicTacToeState() {}

	// Copies an existing board state
	public TicTacToeState(TicTacToeState copyMe) {
		this(copyMe.board.clone(), copyMe.playerJustMoved);
	}

	// Creates a board state from the input parameters
	private TicTacToeState(Integer[] board, Integer playerJustMoved) {
		this.board = board;
		this.playerJustMoved = playerJustMoved;
	}


	@Override
	public Integer getPlayerJustMoved() {
		return playerJustMoved;
	}

	@Override
	public boolean isFinalState() {
		// A final state exists when there are no places left to move, or when someone has made three in a row.
		return !isPlacesLeftToMove() || getPlayerWithLineOfThree() != 0;
	}

	@Override
	public GameState createChildStateFromMove(TicTacToeMove move) throws Exception {
		// Only allow moves that lead to a valid board, or on boards that are not in a final state
		if(board[move.boardIndex] != 0) {
			throw new Exception("A player has already moved in position " + move.boardIndex);
		} else if(isFinalState()) {
			throw new Exception("Cannot make a move on a board state that has been won");
		}

		// The player who's turn it is makes the move
		TicTacToeState newTicTacToeState = new TicTacToeState(this);
		newTicTacToeState.playerJustMoved = 3 - newTicTacToeState.playerJustMoved;
		newTicTacToeState.board[move.boardIndex] = newTicTacToeState.playerJustMoved;
		return newTicTacToeState;
	}

	// Returns all possible child moves, or an empty vector if there are no possible child moves for whatever reason
	@Override
	public Vector<TicTacToeMove> getChildMoves() {
		if(childMoves != null) {
			return childMoves;
		}

		Vector<TicTacToeMove> moves = new Vector();

		// Check if we are in a final state first, if we are then there are no child moves
		if(isFinalState()) {
			return moves;
		}

		// If were not then find all possible moves
		for(int i = 0; i < 9; i++) {
			if(board[i] == 0) {
				try {
					moves.add(new TicTacToeMove(i));
				} catch (Exception e) {}
			}
		}

		childMoves = moves;

		return moves;
	}

	@Override
	public Double getResult(Integer playerNumber) throws Exception {
		Integer playerWithLineOfThree = getPlayerWithLineOfThree();

		if(isPlacesLeftToMove()) {
			if(playerWithLineOfThree == 0) {
				throw new Exception("Cannot determine result of board that is not in final state");
			} else if(playerWithLineOfThree == playerNumber) {
				return 1.0;
			} else {
				return 0.0;
			}
		} else {
			if(playerWithLineOfThree == 0) {
				return 0.5;
			} else if(playerWithLineOfThree == playerNumber) {
				return 1.0;
			} else {
				return 0.0;
			}
		}
	}

	@Override
	public Integer getWinner() throws Exception {
		if(getPlayerWithLineOfThree() != 0) {
			return getPlayerWithLineOfThree();
		} else {
			if(isPlacesLeftToMove()) {
				throw new Exception("Cannot get a winner of a board not in its final state");
			} else {
				return 0;
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(); // default size is 16 chars which shouild be enough

		for(int i = 0; i < 9; i++) {
			if(board[i] == 1) {
				sb.append('X');
			} else if(board[i] == 2) {
				sb.append('O');
			} else {
				sb.append('_');
			}

			if(i == 2 || i == 5) {
				sb.append(System.lineSeparator());
			}
		}

		return sb.toString();
	}


	// Assumes that the current board state can only be in two states: a single player has a line of 3,
	// or no players have a line of three. I.e. assumes class never lets board get in an invalid state!
	private Integer getPlayerWithLineOfThree() {
		// Horizontal
		if(board[0] == board[1] && board[0] == board[2] && board[0] != 0) {
			return board[0];
		} else if(board[3] == board[4] && board[3] == board[5] && board[3] != 0) {
			return board[3];
		} else if(board[6] == board[7] && board[6] == board[8] && board[6] != 0) {
			return board[6];
		}

		// Vertical
		if(board[0] == board[3] && board[0] == board[6] && board[0] != 0) {
			return board[0];
		} else if(board[1] == board[4] && board[1] == board[7] && board[1] != 0) {
			return board[1];
		} else if(board[2] == board[5] && board[2] == board[8] && board[2] != 0) {
			return board[2];
		}

		// Diagonals
		if(board[0] == board[4] && board[0] == board[8] && board[0] != 0) {
			return board[0];
		} else if(board[2] == board[4] && board[2] == board[6] && board[2] != 0) {
			return board[2];
		}

		return 0;
	}

	// Determines if there are any places left to move on the board
	private boolean isPlacesLeftToMove() {
		boolean placesLeftToMove = false;

		for(Integer position : board) {
			if(position == 0) {
				placesLeftToMove = true;
				break;
			}
		}

		return placesLeftToMove;
	}
}