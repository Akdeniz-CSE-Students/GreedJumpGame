package game;

public class Move {
    private final int dRow;
    private final int dCol;

    public Move(int dRow, int dCol) {
        this.dRow = dRow;
        this.dCol = dCol;
    }

    public int getDRow() {
        return dRow;
    }

    public int getDCol() {
        return dCol;
    }
}
