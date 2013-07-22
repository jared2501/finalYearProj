package com.jnew528.finalYearProject;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

	public static void main(String[] args) throws Exception{
		// Game type settings
		int numberOfGames = 200;
		String gameType = "Hex";
		int boardSize = 5;

		// Iteration settings
		int iterationsStart = 100;
		int iterationsEnd = 500;
		int iterationsStep = 100;

		// New DIR name
		long unixTime = System.currentTimeMillis() / 1000L;
		String newDirName = "data" + System.getProperty("file.separator") + unixTime + "_" + gameType + "_boardsize_" + boardSize +
				"_numberOfGames_" + numberOfGames + "_iterStart_" + iterationsStart + "_iterEnd_" + iterationsEnd + "_iterStep_" + iterationsStep;

		// Start timer
		long startTime = System.nanoTime();
		// Create new dir to store texts
		File dir = new File(newDirName);
		dir.mkdir();

		// Create a new folder for the results

		PrintWriter writer = new PrintWriter(newDirName + System.getProperty("file.separator") + "results.m", "UTF-8");

		int i = 0;
		for(int iterations = iterationsStart; iterations < iterationsEnd; iterations += iterationsStep) {
			i++;
			PrintWriter log = new PrintWriter(newDirName + System.getProperty("file.separator") + "iteration_" + i + ".txt", "UTF-8");
			writer.print("results(:," + i +") = ");
			writer.print(concurrentRun(numberOfGames, iterations, gameType, boardSize, log).toString());
			writer.println(";");
			writer.flush();
		}

		long endTime = System.nanoTime();
		long duration = endTime - startTime;
		writer.println();
		writer.println("% duration: " + (double) duration/1000000000);
		writer.close();
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

	public static Vector<Integer> concurrentRun(int games, int iterations, String gameType, int boardSize, PrintWriter writer) throws Exception {
		long startTime = System.nanoTime();
		int cores = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(cores);
		Vector<Future<Game>> futures = new Vector<Future<Game>>();
		Vector<Integer> output = new Vector<Integer>();

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

			Callable game = new Game(gameState, player1, player2, iterations, false);
			futures.add(executor.submit(game));
		}

		executor.shutdown();

		// Write the results to a file as we get them
		int extendedPlayerWins = 0;

		writer.println("1) Game type:");
		writer.println(gameType);
		writer.println("2) Board size:");
		writer.println(boardSize);
		writer.println("3) Number of MCTS iterations per-player:");
		writer.println(iterations);
		writer.println();
		writer.flush();

		for(int i = 0; i < games; i++) {
			Future<Game> future = futures.get(i);
			Game game = future.get();
			GameState gameState = game.getGameState();
			int extendedPlayerNum = i < games / 2 ? 1 : 2;

			if(extendedPlayerNum == gameState.getWinner(false)) {
				output.add(1);
				extendedPlayerWins++;
			} else {
				output.add(0);
			}

			System.out.println("Game " + (i + 1) + " finished out of " + games);
			System.out.println("Extended player wins: " + extendedPlayerWins + " out of " + (i + 1));

			writer.println("Game " + (i + 1));

			writer.println("1) Extended player number:");
			writer.println(extendedPlayerNum);

			writer.println("2) Extended player collisions:");
			if(1 == extendedPlayerNum) {
				writer.println(game.getPlayer1Collisions());
			} else {
				writer.println(game.getPlayer2Collisions());
			}

			writer.println("3) Final game state:");
			writer.println(gameState);

			writer.println("4) Winning player:");
			writer.println(gameState.getWinner(false));

			writer.println("5) Duration in seconds:");
			writer.println((double) game.getDuration() / 1e9);

			writer.println();
			writer.flush();
		}

		long endTime = System.nanoTime();
		long duration = endTime - startTime;

		writer.println("4) Extended player wins:");
		writer.println(extendedPlayerWins + " out of " + (games + 1));
		writer.println("5) Total duration:");
		writer.println((double) duration/1000000000);

		writer.close();

		return output;
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
