/**---------------------------------------------------

*Akdeniz University CSE201 Discrete Mathematics Programming Assignment 2

*Yahya Efe Kurucay

*06.04.2025

*Description: Player20220808005 

*Grade: -- 54% / 60%

*Website: https://docs.efekurucay.com

*---------------------------------------------------*/
/***
 *    ███████ ███████ ███████   |    ███████ ███████ ███████ 
 *    ██      ██      ██        |    ██      ██      ██      
 *    █████   █████   █████     |    █████   █████   █████   
 *    ██      ██      ██        |    ██      ██      ██      
 *    ███████ ██      ███████   |    ███████ ██      ███████ 
 *                            
 *                            
 */

package players;

import game.*;
import java.util.*;

/**
 * Player implementation for the Greed Jump Game using a Breadth-First Search (BFS) strategy.
 * This player aims to maximize board coverage by exploring all possible paths from the current position.
 * The strategy focuses on finding moves that lead to the highest number of reachable squares,
 * ensuring the player can make the most moves before getting stuck.
 */
public class Player20220808005 extends Player {
    
    /**
     * Constructor for the BFS-based player.
     * 
     * @param board The game board instance that this player will interact with
     */
    public Player20220808005(Board board) {
        super(board);
    }

    /**
     * Determines the next move by evaluating all possible moves and choosing the one
     * that leads to the maximum number of future possible moves.
     * 
     * @return The best move based on BFS analysis, or null if no moves are possible
     */
    @Override
    public Move nextMove() {
        // Get all legal moves from the current position
        List<Move> possibleMoves = board.getPossibleMoves();
        
        // If no moves are available, return null to indicate game over
        if (possibleMoves.isEmpty()) {
            return null;
        }
        
        // Initialize variables to track the best move found
        Move bestMove = possibleMoves.get(0);  // Default to first move as fallback
        int maxFutureMoves = -1;  // Track maximum number of future moves found
        
        // Get current player position for reference
        int currentRow = board.getPlayerRow();
        int currentCol = board.getPlayerCol();
        
        // Evaluate each possible move to find the optimal one
        for (Move move : possibleMoves) {
            // Create a temporary board to simulate the move without affecting the actual game state
            Board simulatedBoard = new Board(board);
            
            // Apply the move to the simulated board
            simulatedBoard.applyMove(move);
            
            // Count how many squares are reachable after this move using BFS
            int futureMoves = countFutureMovesBFS(simulatedBoard);
            
            // Update the best move if this one leads to more future possibilities
            if (futureMoves > maxFutureMoves) {
                maxFutureMoves = futureMoves;
                bestMove = move;
            }
        }
        
        return bestMove;
    }
    
    /**
     * Performs a Breadth-First Search to count all reachable squares from the current position.
     * This method helps evaluate the potential of each move by determining how many
     * squares can be visited after making that move.
     * 
     * @param simulatedBoard The board state to evaluate
     * @return The total number of reachable squares from the current position
     */
    private int countFutureMovesBFS(Board simulatedBoard) {
        // Track visited positions using a set to avoid counting duplicates
        Set<String> visited = new HashSet<>();
        
        // Initialize BFS queue with the starting board state
        Queue<Board> queue = new LinkedList<>();
        queue.add(simulatedBoard);
        
        // Mark the initial position as visited
        String initialPos = simulatedBoard.getPlayerRow() + "," + simulatedBoard.getPlayerCol();
        visited.add(initialPos);
        
        int reachableSquares = 0;  // Counter for total reachable squares
        
        // Perform BFS traversal
        while (!queue.isEmpty()) {
            Board currentBoard = queue.poll();
            reachableSquares++;  // Count this position as reachable
            
            // Get all possible moves from the current position
            List<Move> moves = currentBoard.getPossibleMoves();
            
            // Explore each possible move
            for (Move move : moves) {
                // Create a new board state for this move
                Board nextBoard = new Board(currentBoard);
                
                // Apply the move to the new board state
                nextBoard.applyMove(move);
                
                // Create a unique key for this position
                String posKey = nextBoard.getPlayerRow() + "," + nextBoard.getPlayerCol();
                
                // If this position hasn't been visited yet, add it to the queue
                if (!visited.contains(posKey)) {
                    visited.add(posKey);
                    queue.add(nextBoard);
                }
            }
        }
        
        return reachableSquares;
    }
}
