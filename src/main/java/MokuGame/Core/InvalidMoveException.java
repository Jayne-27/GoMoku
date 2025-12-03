package MokuGame.Core;

/**
 * Exception thrown when an invalid move is attempted in the game.
 * This includes moves on occupied cells, out-of-bounds positions, or moves after game over.
 */
public class InvalidMoveException extends GameException {

    /**
     * Creates a new InvalidMoveException with the specified message.
     *
     * @param message the error message describing why the move is invalid
     */
    public InvalidMoveException(String message) {
        super(message);
    }

    /**
     * Creates a new InvalidMoveException with the specified message and cause.
     *
     * @param message the error message
     * @param cause the cause of this exception
     */
    public InvalidMoveException(String message, Throwable cause) {
        super(message, cause);
    }
}
