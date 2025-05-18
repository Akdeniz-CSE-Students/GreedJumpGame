package players;

import game.*;
import java.util.*;

public class Player20220808022 extends Player {
    private static final Random rand = new Random();
    

    public Player20220808022(Board board) {
        super(board);
    }

    @Override
    public Move nextMove() {
        List<Move> possibleMoves = board.getPossibleMoves();
        if (possibleMoves.isEmpty()) return null;

        int size = board.getSize();
        int depth = (size <= 10) ? 3 : (size <= 25 ? 2 : 1);
        int rolloutCount = (size <= 10) ? 4 : (size <= 25 ? 2 : 1);
        int dfsBranchLimit = (size <= 10) ? 10 : 5;

        Move bestMove = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (Move move : possibleMoves) {
            Board simBoard = new Board(board);
            simBoard.applyMove(move);
            double score = dfsWithRollout(simBoard, depth, rolloutCount, dfsBranchLimit);

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private double dfsWithRollout(Board board, int depth, int rollouts, int limit) {
        if (depth == 0 || board.isGameOver()) {
            double avg = 0;
            for (int i = 0; i < rollouts; i++) {
                Board sim = new Board(board);
                avg += guidedRollout(sim);
            }
            return avg / rollouts;
        }

        double bestScore = Double.NEGATIVE_INFINITY;
        List<Move> possibleMoves = board.getPossibleMoves();
        possibleMoves.sort((a, b) -> Integer.compare(
                evaluateHeuristic(board, b), evaluateHeuristic(board, a)));

        int checked = 0;
        for (Move move : possibleMoves) {
            if (checked++ >= limit) break;

            Board simBoard = new Board(board);
            simBoard.applyMove(move);
            double score = dfsWithRollout(simBoard, depth - 1, rollouts, limit);
            bestScore = Math.max(bestScore, score);
        }

        return bestScore;
    }

    private int guidedRollout(Board board) {
        while (!board.isGameOver()) {
            List<Move> moves = board.getPossibleMoves();
            if (moves.isEmpty()) break;

            double epsilon = 0.3 - board.getCoveragePercentage() * 0.2;
            Move move;

            if (rand.nextDouble() < epsilon) {
                move = moves.get(rand.nextInt(moves.size()));
            } else {
                move = getBestMoveByHeuristic(board, moves);
            }

            board.applyMove(move);
        }

        return board.getScore();
    }

    private Move getBestMoveByHeuristic(Board board, List<Move> moves) {
        Move best = null;
        int bestVal = Integer.MIN_VALUE;
        for (Move move : moves) {
            int score = evaluateHeuristic(board, move);
            if (score > bestVal) {
                bestVal = score;
                best = move;
            }
        }
        return best;
    }

    private int evaluateHeuristic(Board board, Move move) {
        int x = board.getPlayerRow();
        int y = board.getPlayerCol();
        int dx = move.getDRow();
        int dy = move.getDCol();
        int tx = x + dx;
        int ty = y + dy;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));

        if (tx < 0 || ty < 0 || tx >= board.getSize() || ty >= board.getSize()) return 0;

        int value = board.getValueAt(tx, ty);
        int mobility = countPossibleMovesFrom(board, tx, ty);
        int jumpBonus = steps * 3;
        int tilePenalty = (value <= 1) ? -10 : 0;

        return value * 3 + mobility * 2 + jumpBonus + tilePenalty;
    }

    private int countPossibleMovesFrom(Board board, int row, int col) {
        int count = 0;
        int[][] dirs = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };

        for (int[] dir : dirs) {
            int dx = dir[0], dy = dir[1];
            int steps = 1;
            while (true) {
                int nx = row + dx * steps;
                int ny = col + dy * steps;
                if (nx < 0 || ny < 0 || nx >= board.getSize() || ny >= board.getSize()) break;
                int value = board.getValueAt(nx, ny);
                if (value == -1) {
                    steps++;
                    continue;
                }
                int tx = row + dx * value;
                int ty = col + dy * value;
                if (tx >= 0 && ty >= 0 && tx < board.getSize() && ty < board.getSize()
                        && board.getValueAt(tx, ty) != -1) {
                    count++;
                }
                break;
            }
        }

        return count;
    }
}