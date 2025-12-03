package MokuGame.Core;

/**
 * Exception thrown when operations are attempted in invalid game states.
 * For example, trying to make moves after the game has ended.
 */
public class GameStateException extends GameException {

    /**
     * Creates a new GameStateException with the specified message.
     *
     * @param message the error message describing the invalid game state
     */
    public GameStateException(String message) {
        super(message);
    }

    /**
     * Creates a new GameStateException with the specified message and cause.
     *
     * @param message the error message
     * @param cause the cause of this exception
     */
    public GameStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
