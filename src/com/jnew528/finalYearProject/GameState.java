package com.jnew528.finalYearProject;

import java.util.ArrayList;
import java.util.Vector;

// All implementations of this interface should be immutable
public interface GameState<M extends Move> {
	// Get the player number that has just moved (1 or 2)
	public Integer getPlayerJustMoved();

	// Determines if the current game state is a final state
	public boolean isFinalState();

	// Creates a new game state with a move made from the opposite player to getPlayerJustMoved()
	public GameState createChildStateFromMove(M move) throws Exception;

	// Get all legal child moves of the current state
	// NB/ Will be empty if there are no current possible moves. This usually indicates a final board state.
	public Vector<M> getChildMoves();

	// Gets the result of a final board state
	// Throws an exception if the current board state is not a final game state
	public Double getResult(Integer playerNumber) throws Exception;

	// Returns the player number that has won for printing reasons
	public Integer getWinner() throws Exception;
}