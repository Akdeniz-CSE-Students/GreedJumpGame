package players;
import game.*;

import java.util.List;

public class Player12345 extends Player {

    public Player12345(Board board) {
        super(board);
    }

    @Override
    public Move nextMove() {
        List<Move> possibleMoves = board.getPossibleMoves();
        if (possibleMoves.isEmpty()) return null;

        // Simple strategy: Always pick the first available direction
        return possibleMoves.get(0);
    }
}
