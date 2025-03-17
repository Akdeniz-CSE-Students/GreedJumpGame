package game;

import java.io.*;
import java.util.Random;

public class InstanceGenerator {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java game.InstanceGenerator <outputFile> <size>");
            return;
        }

        String filename = args[0];
        int size = Integer.parseInt(args[1]);

        generateBoard(filename, size);
    }

    private static void generateBoard(String filename, int size) {
        Random rand = new Random();

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println(size); // ✅ First line: board size
            int startRow = rand.nextInt(size);
            int startCol = rand.nextInt(size);
            writer.println(startRow + " " + startCol); // ✅ Second line: start position

            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    writer.print(rand.nextInt(9) + 1); // ✅ Random number between 1-9
                    if (j < size - 1) writer.print(" ");
                }
                writer.println();
            }
        } catch (IOException e) {
            System.err.println("Error generating board: " + e.getMessage());
        }
    }
}
