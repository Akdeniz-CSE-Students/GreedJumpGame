package game;

public abstract class Player {
    protected Board board;

    public Player(Board board) {
        this.board = board;
    }

    /**
     * Each student must implement this method to define their move strategy.
     * The method should return the next move to be played.
     *  🚀 This method will now be wrapped with a timeout in Referee.java
     */
    public abstract Move nextMove();
}