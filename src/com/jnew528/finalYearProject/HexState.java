package com.jnew528.finalYearProject;

import java.util.*;

public class HexState implements GameState<HexMove> {
	private Integer[] board;
	private Integer playerJustMoved = 2;
	private Integer size = 11;
	private Vector<HexMove> childMoves;
	private Integer winner;

	public HexState() {
		// From a blank board state players can move anywhere
		childMoves = new Vector<HexMove>(size*size);
		board = new Integer[size*size];

		for(int i = 0; i < size*size; i++) {
			board[i] = 0;
			try {
				childMoves.add(new HexMove(i, size));
			} catch (Exception e) {
				System.exit(1);
			}
		}
	}

	private HexState(Integer[] board, Integer playerJustMoved, Integer size, Vector<HexMove> childMoves) {
		this.board = board;
		this.playerJustMoved = playerJustMoved;
		this.size = size;
		this.childMoves = childMoves;
	}

	public HexState(HexState hexState) {
		this(hexState.board.clone(), hexState.playerJustMoved, hexState.size, (Vector<HexMove>) hexState.childMoves.clone());
	}

	@Override
	public Integer getPlayerToMove() {
		return 3 - playerJustMoved;
	}

	@Override
	public Integer getPlayerJustMoved() {
		return playerJustMoved;
	}



	@Override
	public boolean isFinalState(boolean quickCheck) {
		if(quickCheck) {
			// Final state is defined as all moves player on board!
			return childMoves.size() == 0;
		} else {
			Integer winner = determineWinner();
			return winner != null;
		}
	}

	@Override
	public Double getResult(Integer playerNumber, boolean quickCheck) {
		if(quickCheck) {
			if(childMoves.size() == 0) { // If we are quick checking then only determin winner if we know we have to
				Integer winner = determineWinner();

				// Hex doesn't have ties!
				if(winner == playerNumber) {
					return 1.0;
				} else {
					return 0.0;
				}
			} else {
				return null;
			}
		} else {
			Integer winner = determineWinner(); // Otherwise determine if theres a winner immediately since only way in hex to know

			if(winner == null) {
				return null;
			} else {
				// Hex doesn't have ties!
				if(winner == playerNumber) {
					return 1.0;
				} else {
					return 0.0;
				}
			}
		}
	}

	@Override
	public Integer getWinner(boolean quickCheck) {
		if(quickCheck) {
			if(childMoves.size() == 0) {
				return determineWinner();
			} else {
				return null;
			}
		} else {
			return determineWinner();
		}
	}



	@Override
	public GameState createChildStateFromMove(HexMove move) {
		if(board[move.boardIndex] != 0) {
			System.out.println("A player has already moved in position " + move.boardIndex);
			return null;
		} else if(isFinalState(true)) { // quick check the final state
			System.out.println("Cannot make a move on a board state that has been won");
			return null;
		}

		// Make the move and remove from possible child moves
		HexState newHexState = new HexState(this);
		newHexState.playerJustMoved = 3 - newHexState.playerJustMoved;
		newHexState.board[move.boardIndex] = newHexState.playerJustMoved;
		newHexState.childMoves.remove(move);

		return newHexState;
	}

	@Override
	public Vector<HexMove> getChildMoves() {
		return (Vector<HexMove>) childMoves.clone();
	}



	private Vector<Integer> getNearbyPositions(int boardPosition, int player) {
		Vector<Integer> nearBy = new Vector<Integer>(6);

		// Right (If on board, not on right edge, and correct player number)
		if((boardPosition + 1) < size*size && (boardPosition + 1) % size != 0 && board[boardPosition + 1] == player) {
			nearBy.add(boardPosition + 1);
		}
		// Bottom R
		if((boardPosition + 11) < size*size && board[boardPosition + 11] == player) {
			nearBy.add(boardPosition + 11);
		}
		// Bottom L
		if((boardPosition + 10) < size*size && (boardPosition + 10 + 1) % 11 != 0 && board[boardPosition + 10] == player) {
			nearBy.add(boardPosition + 10);
		}
		// Left
		if((boardPosition - 1) > 0 && (boardPosition - 1 + 1) % 11 != 0 && board[boardPosition - 1] == player) {
			nearBy.add(boardPosition - 1);
		}
		// Top L
		if((boardPosition - 11) > 0 && board[boardPosition - 11] == player) {
			nearBy.add(boardPosition - 11);
		}
		// Top R
		if((boardPosition - 10) > 0 && (boardPosition - 10) % 11 != 0 && board[boardPosition - 10] == player) {
			nearBy.add(boardPosition - 10);
		}

		return nearBy;
	}

	public Integer determineWinner() {
		// Cache the result of this method call
		if(winner != null) {
			return winner;
		}

		winner = 0;

		LinkedList<Integer> stackToCheck = new LinkedList<Integer>();
		HashSet<Integer> visited = new HashSet<Integer>();

		for(int i = 0; i < size; i++) {
			if(board[i] == 1) {
				stackToCheck.push(i);
			}
		}

		while(!stackToCheck.isEmpty()) {
			Integer checkPosition = stackToCheck.pop();

			if(checkPosition >= size*size-size) {
				winner = 1;
				break;
			}

			if(!visited.contains(checkPosition)) {
				visited.add(checkPosition);
				Vector<Integer> neighbours = getNearbyPositions(checkPosition, 1);

				for(Integer neighbour : neighbours) {
					if(!visited.contains(neighbour)) {
						stackToCheck.push(neighbour);
					}
				}
			}
		}

		if(winner != 0) {
			return winner;
		}


		// now do player 2
		stackToCheck = new LinkedList<Integer>();
		visited = new HashSet<Integer>();


		for(int i = 0; i < size; i++) {
			if(board[size*i] == 2) {
				stackToCheck.push(size*i);
			}
		}

		while(!stackToCheck.isEmpty()) {
			Integer checkPosition = stackToCheck.pop();

			if((checkPosition + 1) % 11 == 0) {
				winner = 2;
				break;
			}

			if(!visited.contains(checkPosition)) {
				visited.add(checkPosition);
				Vector<Integer> neighbours = getNearbyPositions(checkPosition, 2);

				for(Integer neighbour : neighbours) {
					if(!visited.contains(neighbour)) {
						stackToCheck.push(neighbour);
					}
				}
			}
		}

		// If player 1 is found to be the winner
		if(winner != 0) {
			return winner;
		}

		return null;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(121);

		for(int i = 0; i < size*size; i++) {
			if(board[i] == 0) {
				sb.append(String.format("%03d", i) + "  ");
			} else if(board[i] == 1) {
				sb.append("  X  ");
			} else {
				sb.append("  O  ");
			}

			// Make a new line
			if((i+1) % 11 == 0) {
				sb.append(System.lineSeparator());

				// And push each line across to make it look like a rhombus
				for(int j = 0; j < 2*((i+1)/size); j++) {
					sb.append(" ");
				}
			}
		}

		return sb.toString();
	}

	@Override
	public boolean equals(Object other){
		if(other == null) return false;
		if(other == this) return true;
		if(this.getClass() != other.getClass()) return false;
		HexState otherHexState = (HexState)other;
		return otherHexState.size == this.size
			&& otherHexState.playerJustMoved == this.playerJustMoved
			&& Arrays.deepEquals(otherHexState.board, this.board);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + playerJustMoved.hashCode();
		result = prime * result + size.hashCode();
		result = prime * result + Arrays.hashCode(board);

		return result;
	}
}