package com.jnew528.finalYearProject;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

	public static void main(String[] args) throws Exception{
		// Game type settings
		int numberOfGames = 3072;
		String gameType = "Gobang";
		int boardSize = 6;

		// Iteration settings
		int iterationsStart = 21000;
		int iterationsEnd = 25000;
		int iterationsStep = 1000;

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
		for(int iterations = iterationsStart; iterations <= iterationsEnd; iterations += iterationsStep) {
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

	public static void test() throws Exception {
		Random random = new Random();

		for(int i = 0; i < 1000000; i++) {
			TicTacToeState gameState1 = new TicTacToeState();
			GobangState gameState2 = new GobangState();

			while(!gameState1.isFinalState(false)) {
				System.out.println(gameState1);
				System.out.println(gameState2);

				// Select random move
				Vector<TicTacToeMove> moves = gameState1.getChildMoves();
				TicTacToeMove move = moves.get(random.nextInt(moves.size()));

				gameState1 = (TicTacToeState)gameState1.createChildStateFromMove(move);
				gameState2 = (GobangState)gameState2.createChildStateFromMove(move);
			}

			System.out.println("Winner gs1 is: " + gameState1.getWinner(false));
			System.out.println("Winner gs2 is: " + gameState2.getWinner(false));
			if(gameState1.getWinner(false) != gameState2.getWinner(false)) {
				System.exit(1);
			}

			System.out.println(gameState2);
			System.out.println(gameState1);
		}

		System.out.println("done!");
	}

	public static void debugRun() throws Exception {
		long startTime = System.nanoTime();

		StdMctsTree player1;
		StdMctsTree player2;
		player1 = new StdMctsTree();
		player2 = new StdMctsTree();
		GameState gameState = new GobangState();
		Callable game = new Game(gameState, player1, player2, 50000, true);
		game.call();

		long endTime = System.nanoTime();
		long duration = endTime - startTime;
		System.out.println("Duration: " + duration/1000000000);
	}

	public static Vector<Double> concurrentRun(int games, int iterations, String gameType, int boardSize, PrintWriter writer) throws Exception {
		long startTime = System.nanoTime();
		int cores = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(cores);
		Vector<Future<Game>> futures = new Vector<Future<Game>>();
		Vector<Double> output = new Vector<Double>();

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
			} else if(gameType == "Gobang") {
				gameState = new GobangState(boardSize);
			} else {
				throw new Exception("Unknown game type");
			}

			Callable game = new Game(gameState, player1, player2, iterations, false);
			futures.add(executor.submit(game));
		}

		executor.shutdown();

		// Write the results to a file as we get them
		double extendedPlayerWins = 0;

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
				output.add(1.0);
				extendedPlayerWins += 1;
			} else if(0 == gameState.getWinner(false)) {
				output.add(0.5);
				extendedPlayerWins += 0.5;
			} else {
				output.add(0.0);
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
		writer.println(extendedPlayerWins + " out of " + games);
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
