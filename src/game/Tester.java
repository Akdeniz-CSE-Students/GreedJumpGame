package game;

import java.io.*;

public class Tester {
    private static final boolean ENABLE_SNAPSHOTS = true; // ✅ Toggle snapshot saving

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java game.Tester <boardFile> <studentID>");
            return;
        }

        String boardFile = args[0];
        String studentID = args[1];

        // 1) Load the board data from disk
        BoardData data;
        try {
            data = loadBoardData(boardFile);
        } catch (IOException e) {
            System.err.println("Error loading board data: " + e.getMessage());
            return;
        }

        if (data == null) {
            System.err.println("Error: Board data is empty or corrupted.");
            return;
        }

        // 2) Create a fresh board for this player's game
        Board board = new Board(data.size, data.grid, data.startRow, data.startCol);

        // Ensure snapshot directory exists
        if (ENABLE_SNAPSHOTS) {
            new File("snapshots").mkdirs();
        }

        // 3) Instantiate the student's player with timeout enforcement
        Player player = null;
        try {
            player = Referee.initializePlayer(studentID, board);
            if (player == null) {
                System.out.println(studentID + " 1"); // Disqualified players get only 1 point
                return;
            }
        } catch (Exception e) {
            System.err.println("Error initializing player " + studentID + ": " + e.getMessage());
            System.out.println(studentID + " 1"); // Fail-safe: Assign minimum score
            return;
        }

        // 4) Run the game
        int finalScore = Referee.playGame(player, studentID, boardFile, ENABLE_SNAPSHOTS);

        // 5) Print the final score (shell script captures this)
        System.out.println(studentID + " " + finalScore);
    }

    // ✅ This method properly loads board data from a file
    private static BoardData loadBoardData(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists() || file.length() == 0) {
            throw new IOException("Board file " + filename + " is missing or empty.");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            int size = Integer.parseInt(br.readLine().trim());
            String[] start = br.readLine().split("\\s+");
            int startRow = Integer.parseInt(start[0]);
            int startCol = Integer.parseInt(start[1]);
            int[][] grid = new int[size][size];

            for (int r = 0; r < size; r++) {
                String[] rowVals = br.readLine().split("\\s+");
                for (int c = 0; c < size; c++) {
                    grid[r][c] = Integer.parseInt(rowVals[c]);
                }
            }

            return new BoardData(size, grid, startRow, startCol);
        }
    }

    // ✅ Helper class for board data storage
    static class BoardData {
        int size;
        int[][] grid;
        int startRow, startCol;
        BoardData(int s, int[][] g, int r, int c) {
            size = s; grid = g; startRow = r; startCol = c;
        }
    }
}
