package game;

import java.io.*;
import java.util.concurrent.*;

public class Referee {
    private static final int INIT_TIMEOUT_SECONDS = 2;  // ⏳ Max time for player constructor
    private static final int MOVE_TIMEOUT_SECONDS = 1;  // ⏳ Max time per move

    public static int playGame(Player player, String studentID, String boardFile, boolean enableSnapshots) {
        int moves = 0;
        int score = 1; // Minimum participation score (avoid complete failure)
        int boardNumber = extractBoardNumber(boardFile);
        File snapshotFile = new File(String.format("snapshots/Player%s_Board%d.txt", studentID, boardNumber));

        ExecutorService executor = Executors.newSingleThreadExecutor(); // Timeout manager

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(snapshotFile))) {
            if (enableSnapshots) {
                saveSnapshot(player.board, writer, 0, true);
            }

            while (!player.board.isGameOver()) {
                Future<Move> futureMove = executor.submit(player::nextMove); // Fix: Ensure we return a `Move` object
                Move move;

                try {
                    move = futureMove.get(MOVE_TIMEOUT_SECONDS, TimeUnit.SECONDS); // ⏳ Timeout for moves
                } catch (TimeoutException e) {
                    System.err.println("⏳ Timeout: Player " + studentID + " took too long to move! Ending game.");
                    break;
                } catch (Exception e) {
                    System.err.println("❌ Runtime error in nextMove() for Player " + studentID + ": " + e.getMessage());
                    break;
                }

                if (move == null) break;
                if (!player.board.applyMove(move)) { // Fix: Ensure the board uses the correct move type
                    System.err.println("🚫 Invalid move by Player " + studentID + ": " + move);
                    break;
                }

                moves++;
                score = player.board.getScore();
                player.board.printBoard();

                if (enableSnapshots) {
                    saveSnapshot(player.board, writer, moves, false);
                }

                Thread.sleep(0);
            }
        } catch (IOException e) {
            System.err.println("📝 Error writing snapshot for Player " + studentID + ": " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("🔴 Interrupted during execution for Player " + studentID);
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdownNow();
        }

        return score;
    }

    public static Player initializePlayer(String studentID, Board board) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Player> future = executor.submit(() -> {
            try {
                Class<?> playerClass = Class.forName("players.Player" + studentID);
                return (Player) playerClass.getDeclaredConstructor(Board.class).newInstance(board);
            } catch (Exception e) {
                return null; // ❌ Failed to initialize
            }
        });

        try {
            return future.get(INIT_TIMEOUT_SECONDS, TimeUnit.SECONDS); // ⏳ Max 2 sec for initialization
        } catch (TimeoutException e) {
            System.err.println("⏳ Timeout: Player " + studentID + " took too long to initialize!");
            return null; // ❌ Disqualified due to constructor timeout
        } catch (Exception e) {
            System.err.println("❌ Error initializing player " + studentID + ": " + e.getMessage());
            return null;
        } finally {
            executor.shutdownNow();
        }
    }

    private static int extractBoardNumber(String boardFile) {
        try {
            return Integer.parseInt(boardFile.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static void saveSnapshot(Board board, BufferedWriter writer, int step, boolean isInitial) throws IOException {
        writer.write((isInitial ? "Initial Board:\n" : "Step " + step + ":\n"));
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                if (i == board.getPlayerRow() && j == board.getPlayerCol()) {
                    writer.write("* ");
                } else if (board.isVisited(i, j)) {
                    writer.write("  "); // ✅ Ensures visited cells are empty
                } else {
                    writer.write(board.getValueAt(i, j) + " ");
                }
            }
            writer.newLine();
        }
        writer.newLine();
    }
}