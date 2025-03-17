package players;

import game.*;

import java.util.List;
import java.util.Random;

public class Player67890 extends Player {
    private final Random random;

    public Player67890(Board board) {
        super(board);
        this.random = new Random();
    }

    @Override
    public Move nextMove() {
        List<Move> possibleMoves = board.getPossibleMoves();
        if (possibleMoves.isEmpty()) {
            return null;
        }

        return possibleMoves.get(random.nextInt(possibleMoves.size())); // ✅ Picks a random direction
    }
}
