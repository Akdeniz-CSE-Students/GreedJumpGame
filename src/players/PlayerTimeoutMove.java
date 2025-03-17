package players;

import game.*;
import java.util.List;

public class PlayerTimeoutMove extends Player {
    private int moveCounter = 0;

    public PlayerTimeoutMove(Board board) {
        super(board);
    }

    @Override
    public Move nextMove() {
        moveCounter++;
        if (moveCounter > 3) {  // Simulates a timeout after a few moves
            try {
                Thread.sleep(5000); // Exceed timeout intentionally
            } catch (InterruptedException ignored) {}
        }
        List<Move> possibleMoves = board.getPossibleMoves();
        return possibleMoves.isEmpty() ? null : possibleMoves.get(0);
    }
}