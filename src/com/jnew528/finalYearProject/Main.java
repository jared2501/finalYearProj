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
		int numberOfGames = 1000;
		String gameType = "LeftRight";
		int boardSize = 100;

		// Iteration settings
		int iterationsStart = 200;
		int iterationsEnd = 900;
		int iterationsStep = 100;

		// New DIR name
		long unixTime = System.currentTimeMillis() / 1000L;
		String newDirName = "data" + System.getProperty("file.separator") + unixTime + "_LEARNING_" + gameType + "_boardsize_" + boardSize +
				"_numberOfGames_" + numberOfGames + "_iterStart_" + iterationsStart + "_iterEnd_" + iterationsEnd + "_iterStep_" + iterationsStep;

		// Start timer
		long startTime = System.nanoTime();
		File dir = new File(newDirName);
		// Create new dir to store texts
		dir.mkdir();

		// Create a new folder for the results

		PrintWriter resultsWriter = new PrintWriter(newDirName + System.getProperty("file.separator") + "results.m", "UTF-8");
		int cores = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(cores);
		Vector<Future<Vector<Double>>> futures = new Vector();

		int i = 0;
		for(int iterations = iterationsStart; iterations <= iterationsEnd; iterations += iterationsStep) {
			i++;
			PrintWriter iterationWriter = new PrintWriter(newDirName + System.getProperty("file.separator") + "iteration_" + i + ".txt", "UTF-8");
			SetOfGames setOfGames = new SetOfGames(numberOfGames, iterations, gameType, boardSize, iterationWriter);
			futures.add(executor.submit(setOfGames));
		}

		executor.shutdown();

        i = 0;
		for(int iterations = iterationsStart; iterations <= iterationsEnd; iterations += iterationsStep) {
            Future<Vector<Double>> future = futures.get(i);
			Vector<Double> results = future.get();
			resultsWriter.print("results(:," + i +") = ");
			resultsWriter.print(results);
			resultsWriter.println(";");
			resultsWriter.flush();
            i++;
        }

		long endTime = System.nanoTime();
		long duration = endTime - startTime;
		resultsWriter.println();
		resultsWriter.println("% duration: " + (double) duration/1000000000);
		resultsWriter.close();
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
