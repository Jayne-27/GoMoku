 package MokuGame.Service;

import MokuGame.Core.GoMokuBoard;
import MokuGame.Core.InvalidMoveException;
import MokuGame.Core.GameStateException;
import MokuGame.Core.GameConfig;
import MokuGame.Core.GameStatistics;
import MokuGame.Core.Move;
import java.util.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service class that manages GoMoku game logic including move validation,
 * turn management, and win detection.
 */
public class GoMoKuGameService {

    private static final Logger logger = LoggerFactory.getLogger(GoMoKuGameService.class);
    private final GoMokuBoard board;
    private final GameConfig config;
    private final GameStatistics statistics;
    private char currentPlayer;
    private boolean gameOver;
    private char winner;
    private final Stack<Move> moveHistory;
    private final Stack<Move> redoStack;

    /**
     * Creates a new game service with the specified board and configuration.
     *
     * @param board the game board to use
     * @param config the game configuration
     */
    public GoMoKuGameService(GoMokuBoard board, GameConfig config) {
        this.board = board;
        this.config = config;
        this.statistics = new GameStatistics();
        this.currentPlayer = GoMokuBoard.Player1;
        this.gameOver = false;
        this.winner = GoMokuBoard.Empty;
        this.moveHistory = new Stack<>();
        this.redoStack = new Stack<>();
        logger.info("New game service created with {}x{} board", board.getRows(), board.getColumns());
    }

    /**
     * Creates a new game service with the specified board and default configuration.
     *
     * @param board the game board to use
     */
    public GoMoKuGameService(GoMokuBoard board) {
        this(board, new GameConfig());
    }

    /**
     * Gets the current game board.
     *
     * @return the game board
     */
    public GoMokuBoard getBoard() {
        return board;
    }

    /**
     * Gets the current player.
     *
     * @return the current player character ('X' or 'O')
     */
    public char getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Checks if the game is over.
     *
     * @return true if the game has ended, false otherwise
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Gets the winner of the game.
     *
     * @return the winning player character, or '.' if no winner yet
     */
    public char getWinner() {
        return winner;
    }

    /**
     * Attempts to make a move at the specified position.
     *
     * @param row the row index
     * @param col the column index
     * @throws InvalidMoveException if the move is invalid
     * @throws GameStateException if the game is already over
     */
    public void makeMove(int row, int col) throws InvalidMoveException, GameStateException {
        if (gameOver) {
            throw new GameStateException("Cannot make move: game is already over");
        }

        if (!board.isValidPosition(row, col)) {
            throw new InvalidMoveException("Invalid position: (" + row + ", " + col + ") is out of bounds");
        }

        if (!board.isEmpty(row, col)) {
            throw new InvalidMoveException("Position (" + row + ", " + col + ") is already occupied");
        }

        board.setCell(row, col, currentPlayer);
        Move move = new Move(row, col, currentPlayer);
        moveHistory.push(move);
        redoStack.clear(); // Clear redo stack when new move is made
        logger.info("Player {} placed at ({}, {})", currentPlayer, row, col);

        if (checkWin(row, col)) {
            gameOver = true;
            winner = currentPlayer;
            logger.info("Player {} wins!", currentPlayer);
        } else if (isBoardFull()) {
            gameOver = true;
            logger.info("Game ended in a draw");
        } else {
            switchPlayer();
        }
    }

    /**
     * Switches the current player.
     */
    private void switchPlayer() {
        currentPlayer = (currentPlayer == GoMokuBoard.Player1)
            ? GoMokuBoard.Player2
            : GoMokuBoard.Player1;
    }

    /**
     * Checks if the board is completely full.
     *
     * @return true if no empty cells remain, false otherwise
     */
    private boolean isBoardFull() {
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                if (board.isEmpty(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if the last move at the specified position resulted in a win.
     *
     * @param row the row of the last move
     * @param col the column of the last move
     * @return true if this move wins the game, false otherwise
     */
    private boolean checkWin(int row, int col) {
        char player = board.getCell(row, col);

        return checkDirection(row, col, 0, 1, player) ||  // Horizontal
               checkDirection(row, col, 1, 0, player) ||  // Vertical
               checkDirection(row, col, 1, 1, player) ||  // Diagonal \
               checkDirection(row, col, 1, -1, player);   // Diagonal /
    }

    /**
     * Checks if there are 5 or more consecutive pieces in a specific direction.
     *
     * @param row the starting row
     * @param col the starting column
     * @param dRow the row direction (-1, 0, or 1)
     * @param dCol the column direction (-1, 0, or 1)
     * @param player the player to check for
     * @return true if 5 or more consecutive pieces found, false otherwise
     */
    private boolean checkDirection(int row, int col, int dRow, int dCol, char player) {
        int count = 1; // Count the current piece

        // Check in positive direction
        count += countInDirection(row, col, dRow, dCol, player);

        // Check in negative direction
        count += countInDirection(row, col, -dRow, -dCol, player);

        return count >= config.getWinLength();
    }

    /**
     * Counts consecutive pieces in a specific direction from a starting position.
     *
     * @param row the starting row
     * @param col the starting column
     * @param dRow the row direction
     * @param dCol the column direction
     * @param player the player to count for
     * @return the count of consecutive pieces in that direction
     */
    private int countInDirection(int row, int col, int dRow, int dCol, char player) {
        int count = 0;
        int r = row + dRow;
        int c = col + dCol;

        while (board.isValidPosition(r, c) && board.getCell(r, c) == player) {
            count++;
            r += dRow;
            c += dCol;
        }

        return count;
    }

    /**
     * Undoes the last move if possible.
     *
     * @return true if a move was undone, false if no moves to undo
     * @throws GameStateException if the game is already over
     */
    public boolean undo() throws GameStateException {
        if (gameOver) {
            throw new GameStateException("Cannot undo: game is already over");
        }

        if (moveHistory.isEmpty()) {
            return false;
        }

        Move lastMove = moveHistory.pop();
        board.setCell(lastMove.getRow(), lastMove.getCol(), GoMokuBoard.Empty);
        redoStack.push(lastMove);
        switchPlayer(); // Switch back to the previous player
        logger.info("Undid move at ({}, {})", lastMove.getRow(), lastMove.getCol());
        return true;
    }

    /**
     * Redoes the last undone move if possible.
     *
     * @return true if a move was redone, false if no moves to redo
     * @throws GameStateException if the game is already over
     */
    public boolean redo() throws GameStateException {
        if (gameOver) {
            throw new GameStateException("Cannot redo: game is already over");
        }

        if (redoStack.isEmpty()) {
            return false;
        }

        Move moveToRedo = redoStack.pop();
        board.setCell(moveToRedo.getRow(), moveToRedo.getCol(), moveToRedo.getPlayer());
        moveHistory.push(moveToRedo);
        switchPlayer(); // Switch to the next player
        logger.info("Redid move at ({}, {})", moveToRedo.getRow(), moveToRedo.getCol());

        // Check if this redo results in a win
        if (checkWin(moveToRedo.getRow(), moveToRedo.getCol())) {
            gameOver = true;
            winner = moveToRedo.getPlayer();
            logger.info("Player {} wins after redo!", moveToRedo.getPlayer());
        }

        return true;
    }

    /**
     * Checks if undo is possible.
     *
     * @return true if undo is possible, false otherwise
     */
    public boolean canUndo() {
        return !moveHistory.isEmpty() && !gameOver;
    }

    /**
     * Checks if redo is possible.
     *
     * @return true if redo is possible, false otherwise
     */
    public boolean canRedo() {
        return !redoStack.isEmpty() && !gameOver;
    }

    /**
     * Starts tracking a new game for statistics.
     */
    public void startNewGame() {
        statistics.startGame();
        logger.info("New game started for statistics tracking");
    }

    /**
     * Ends the current game and records the result in statistics.
     */
    public void endGame() {
        if (gameOver) {
            statistics.endGame(winner);
            logger.info("Game ended with winner: {}", winner);
        }
    }

    /**
     * Gets the game statistics.
     *
     * @return the game statistics instance
     */
    public GameStatistics getStatistics() {
        return statistics;
    }

    /**
     * Resets the game to initial state, clearing the board.
     */
    public void reset() {
        board.clear();
        currentPlayer = GoMokuBoard.Player1;
        gameOver = false;
        winner = GoMokuBoard.Empty;
        moveHistory.clear();
        redoStack.clear();
        logger.info("Game reset");
    }
}
