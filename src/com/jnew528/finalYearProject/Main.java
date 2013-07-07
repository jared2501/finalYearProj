package com.jnew528.finalYearProject;

import java.util.Scanner;
import java.util.Vector;

public class Main {

	public static void main(String[] args) {
		System.out.println("Play Tic-Tac-Toe:");

		GameState gameState = new HexState();

		while(!gameState.isFinalState()) {
			try {
				Move move = null;

				if(gameState.getPlayerJustMoved() == 2) {
					System.out.println(gameState);
//					move = getMoveFromUser(gameState);

					StdMctsTree mctsTree = new StdMctsTree(gameState);
					move = mctsTree.performSearch(50000);
				} else {
					System.out.println(gameState);
//					move = getMoveFromUser(gameState);

					StdMctsTree mctsTree = new StdMctsTree(gameState);
					move = mctsTree.performSearch(25000);
				}

				gameState = gameState.createChildStateFromMove(move);
			} catch (Exception e) { System.exit(1); }
		}

		System.out.println(gameState);
		try {
			System.out.println("Player " + gameState.getWinner() + " has won!");
		} catch (Exception e) { System.exit(1); }
	}

	public static Move getMoveFromUser(GameState gameState) {
		Move move = null;

		while(true) {
			try {
				Scanner scanIn = new Scanner(System.in);

				System.out.println("Possible moves:");

				StringBuilder sb = new StringBuilder(200);
				sb.append("[");
				for(int i = 0; i < gameState.getChildMoves().size(); i++) {
					sb.append(i + ":");
					sb.append(gameState.getChildMoves().get(i));
					sb.append(", ");
				}
				sb.append("]");
				System.out.println(sb);

				System.out.println("Select an index of a possible move above:");
				Integer selectedMove = Integer.parseInt(scanIn.nextLine());

				if(selectedMove > gameState.getChildMoves().size()) {
					throw new Exception();
				}

				Vector<Move> vec = gameState.getChildMoves();
				move = vec.get(selectedMove);
				break;
			} catch (Exception e) {
				System.out.println("Invalid index selected");
			}
		}

		return move;
	}
}
