package MokuGame.Core;

/**
 * Base exception class for Gomoku game-related errors.
 * Provides a foundation for more specific game exceptions.
 */
public class GameException extends Exception {

    /**
     * Creates a new GameException with the specified message.
     *
     * @param message the error message
     */
    public GameException(String message) {
        super(message);
    }

    /**
     * Creates a new GameException with the specified message and cause.
     *
     * @param message the error message
     * @param cause the cause of this exception
     */
    public GameException(String message, Throwable cause) {
        super(message, cause);
    }
}
