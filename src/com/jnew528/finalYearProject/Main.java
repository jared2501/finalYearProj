package com.jnew528.finalYearProject;

import java.io.PrintWriter;
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
		long startTime = System.nanoTime();
		int cores = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(cores);
		Vector<Future<Game>> futures = new Vector<Future<Game>>();

		int games = 1000;
		String gameType = "Hex";
		int boardSize = 7;

		for(int i = 0; i < games; i++) {
			StdMctsTree player1;
			StdMctsTree player2;
			GameState gameState;

			if(i < games / 2) {
				player1 = new ExtendedMctsTree();
				player2 = new StdMctsTree();
			} else {
				player1 = new StdMctsTree();
				player2 = new ExtendedMctsTree();
			}

			if(gameType == "LeftRight") {
				gameState = new LeftRightState(boardSize);
			} else if(gameType == "Hex") {
				gameState = new HexState(boardSize);
			} else {
				throw new Exception("Unknown game type");
			}

			Callable game = new Game(gameState, player1, player2, 20000, false);
			futures.add(executor.submit(game));
		}

		executor.shutdown();

		// Write the results to a file as we get them
		long unixTime = System.currentTimeMillis() / 1000L;
		PrintWriter writer = new PrintWriter(unixTime + "_gameresults_" + games + "_" + gameType + ".txt", "UTF-8");
		int extendedPlayerWins = 0;

		for(int i = 0; i < games; i++) {
			Future<Game> future = futures.get(i);
			Game game = future.get();
			GameState gameState = game.getGameState();
			int extendedPlayerNum = i < games / 2 ? 1 : 2;

			if(extendedPlayerNum == gameState.getWinner(false)) {
				extendedPlayerWins++;
			}

			System.out.println("Game " + (i + 1) + " finished out of " + games);
			System.out.println("Extended player wins: " + extendedPlayerWins + " out of " + (i + 1));


			writer.println("Game " + (i + 1));

			writer.println("1) Game type:");
			writer.println(gameType);

			writer.println("2) Board size:");
	 		writer.println(boardSize);

			writer.println("3) Extended player number:");
			writer.println(extendedPlayerNum);

			writer.println("4) Number of MCTS iterations per-player:");
			writer.println(game.getIterations());

			writer.println("5) Extended player collisions:");
			if(1 == extendedPlayerNum) {
				writer.println(game.getPlayer1Collisions());
			} else {
				writer.println(game.getPlayer2Collisions());
			}

			writer.println("6) Final game state:");
			writer.println(gameState);

			writer.println("7) Winning player:");
			writer.println(gameState.getWinner(false));

			writer.println("8) Duration in seconds:");
			writer.println((double) game.getDuration() / 1e9);

			writer.println();
			writer.flush();
		}

		long endTime = System.nanoTime();
		long duration = endTime - startTime;

		writer.println("Extended player wins:");
		writer.println(extendedPlayerWins + " out of " + (games + 1));
		writer.println("Total duration:");
		writer.println((double) duration/1000000000);

		writer.close();
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
