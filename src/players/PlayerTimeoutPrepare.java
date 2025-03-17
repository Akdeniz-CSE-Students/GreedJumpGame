
package players;

import game.*;

public class PlayerTimeoutPrepare extends Player {
    public PlayerTimeoutPrepare(Board board) {
        super(board);

        // Intentionally exceed preparation timeout
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {}
    }

    @Override
    public Move nextMove() {
        return null;
    }
}
