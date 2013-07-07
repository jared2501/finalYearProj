package com.jnew528.finalYearProject;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Vector;

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
	public Integer getPlayerJustMoved() {
		return playerJustMoved;
	}

	@Override
	public boolean isFinalState() {
		// Final state is defined as all moves player on board!
		return childMoves.size() == 0;
	}

	@Override
	public GameState createChildStateFromMove(HexMove move) throws Exception {
		if(board[move.boardIndex] != 0) {
			throw new Exception("A player has already moved in position " + move.boardIndex);
		} else if(isFinalState()) {
			throw new Exception("Cannot make a move on a board state that has been won");
		}

		// Make the move and remvoe from possible child moves
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

	@Override
	public Double getResult(Integer playerNumber) throws Exception {
		Integer winner = getWinner(); // Throws exception if not in the final state

		// Hex doesnt have ties!
		if(winner == playerNumber) {
			return 1.0;
		} else {
			return 0.0;
		}
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

	@Override
	public Integer getWinner() throws Exception {
		if(winner != null) {
			return winner;
		}

		if(!isFinalState()) {
			throw new Exception("Cannot determine result of board that is not in final state");
		}

		winner = 0;

		mainLoop1:
		for(int i = 0; i < size; i++) {
			if(board[i] == 1) {
				LinkedList<Integer> stackToCheck = new LinkedList<Integer>();
				HashSet<Integer> visited = new HashSet<Integer>();

				stackToCheck.push(i);

				while(!stackToCheck.isEmpty()) {
					Integer checkPosition = stackToCheck.pop();

					if(checkPosition >= size*size-size) {
						winner = 1;
						break mainLoop1;
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
			}
		}

		if(winner != 0) {
			return winner;
		}


		// now do player 2
		mainLoop2:
		for(int i = 0; i < size; i++) {
			if(board[size*i] == 2) {
				LinkedList<Integer> stackToCheck = new LinkedList<Integer>();
				HashSet<Integer> visited = new HashSet<Integer>();

				stackToCheck.push(size*i);

				while(!stackToCheck.isEmpty()) {
					Integer checkPosition = stackToCheck.pop();

					if((checkPosition + 1) % 11 == 0) {
						winner = 2;
						break mainLoop2;
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
			}
		}

		// If player 1 is found to be the winner
		if(winner != 0) {
			return winner;
		}


		System.out.println("Error in hex winner algorithm!");
		System.exit(1);
		return winner; /// SHOULD NEVER GET HERE!!
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
}
