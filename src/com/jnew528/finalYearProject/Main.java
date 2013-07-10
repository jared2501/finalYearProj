package com.jnew528.finalYearProject;

import java.util.Scanner;
import java.util.Vector;

public class Main {

	public static void main(String[] args) throws Exception{
		long startTime = System.nanoTime();

//		for(int i = 0; i < 10; i ++) {
			GameState gameState = new TicTacToeState();

			while(!gameState.isFinalState(false)) {
				Move move = null;

				if(gameState.getPlayerJustMoved() == 2) {
					System.out.println(gameState);
//						move = getMoveFromUserAlt(gameState);

					StdMctsTree mctsTree = new ExtendedMctsTree(gameState);
					move = mctsTree.performSearch(10000);
				} else {
					System.out.println(gameState);
//						move = getMoveFromUser(gameState);

					StdMctsTree mctsTree = new ExtendedMctsTree(gameState);
					move = mctsTree.performSearch(10000);
				}

				gameState = gameState.createChildStateFromMove(move);
			}

			System.out.println(gameState);
			System.out.println("Player " + gameState.getWinner(false) + " has won!");
//		}

		long endTime = System.nanoTime();
		long duration = endTime - startTime;
		System.out.println("Duration: " + duration/1000000000);
	}



	public static HexMove getMoveFromUserAlt(GameState gameState) {
		HexMove move = null;

		while(true) {
			try {
				Scanner scanIn = new Scanner(System.in);
				System.out.println("Select a possible move above:");
				Integer selectedMove = Integer.parseInt(scanIn.nextLine());
				move = new HexMove(selectedMove);
				GameState gameState1 = gameState.createChildStateFromMove(move);
				break;
			} catch (Exception e) {
				System.out.println("Invalid move entered");
			}
		}

		return move;
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
