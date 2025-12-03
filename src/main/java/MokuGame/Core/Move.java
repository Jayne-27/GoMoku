package MokuGame.Core;

/**
 * Represents a single move in the Gomoku game.
 * Contains the position and player information for the move.
 */
public class Move {
    private final int row;
    private final int col;
    private final char player;

    /**
     * Creates a new move with the specified position and player.
     *
     * @param row the row index of the move
     * @param col the column index of the move
     * @param player the player who made the move
     */
    public Move(int row, int col, char player) {
        this.row = row;
        this.col = col;
        this.player = player;
    }

    /**
     * Gets the row index of this move.
     *
     * @return the row index
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the column index of this move.
     *
     * @return the column index
     */
    public int getCol() {
        return col;
    }

    /**
     * Gets the player who made this move.
     *
     * @return the player character
     */
    public char getPlayer() {
        return player;
    }

    @Override
    public String toString() {
        return "Move{" +
                "row=" + row +
                ", col=" + col +
                ", player=" + player +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Move move = (Move) obj;
        return row == move.row && col == move.col && player == move.player;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(row, col, player);
    }
}
