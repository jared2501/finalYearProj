package com.jnew528.finalYearProject;

import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

	public static void main(String[] args) throws Exception{
		concurrentRun();
	}

	public static void debugRun() throws Exception {
		long startTime = System.nanoTime();

		StdMctsTree player1;
		StdMctsTree player2;
		player1 = new StdMctsTree();
		player2 = new StdMctsTree();
		GameState gameState = new LeftRightState();
		Callable game = new Game(gameState, player1, player2, 1000, true);
		game.call();

		long endTime = System.nanoTime();
		long duration = endTime - startTime;
		System.out.println("Duration: " + duration/1000000000);
	}

	public static void concurrentRun() throws Exception {
		int cores = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(cores);
		Vector<Future<GameState>> futures = new Vector<Future<GameState>>();

		long startTime = System.nanoTime();
		int extendedPlayerWins = 0;
		int iterations = 200;

		for(int i = 0; i < iterations; i++) {
			StdMctsTree player1;
			StdMctsTree player2;

			if(i < iterations / 2) {
				player1 = new ExtendedMctsTree();
				player2 = new StdMctsTree();
			} else {
				player1 = new StdMctsTree();
				player2 = new ExtendedMctsTree();
			}

			GameState gameState = new LeftRightState(200);

			Callable game = new Game(gameState, player1, player2, 250, false);
			futures.add(executor.submit(game));
		}

		executor.shutdown();

		for(int i = 0; i < iterations; i++) {
			Future<GameState> future = futures.get(i);
			GameState gameState = future.get();
			int extendedPlayerNum = i < iterations / 2 ? 1 : 2;

			if(gameState.getWinner(false) == extendedPlayerNum) {
				extendedPlayerWins++;
			}
		}

		long endTime = System.nanoTime();
		long duration = endTime - startTime;
		System.out.println("Duration: " + duration/1000000000);
		System.out.println("Extended Player wins: " + extendedPlayerWins);
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
