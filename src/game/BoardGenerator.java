package game;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class BoardGenerator {
    private static final int NUM_BOARDS = 5;
    private static final int SIZE = 5;

    public static void main(String[] args) {
        // Make sure boards/ folder exists
        new File("boards").mkdirs();

        for (int i = 1; i <= NUM_BOARDS; i++) {
            BoardData data = generateRandomBoardData(SIZE);
            String filename = "boards/board" + i + ".dat";
            try {
                saveBoardData(data, filename);
                System.out.println("Generated " + filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static BoardData generateRandomBoardData(int size) {
        Random rand = new Random();
        int[][] grid = new int[size][size];
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                grid[r][c] = 1 + rand.nextInt(9);
            }
        }
        int startRow = rand.nextInt(size);
        int startCol = rand.nextInt(size);

        return new BoardData(size, grid, startRow, startCol);
    }

    private static void saveBoardData(BoardData data, String filename) throws IOException {
        try (FileWriter fw = new FileWriter(filename)) {
            // Format:
            // First line: size
            // Second line: startRow startCol
            // Next 'size' lines: row of grid
            fw.write(data.size + "\n");
            fw.write(data.startRow + " " + data.startCol + "\n");
            for (int r = 0; r < data.size; r++) {
                for (int c = 0; c < data.size; c++) {
                    fw.write(data.grid[r][c] + (c < data.size - 1 ? " " : ""));
                }
                fw.write("\n");
            }
        }
    }

    static class BoardData {
        int size;
        int[][] grid;
        int startRow, startCol;

        BoardData(int sz, int[][] gr, int sr, int sc) {
            size = sz; grid = gr; startRow = sr; startCol = sc;
        }
    }
}
