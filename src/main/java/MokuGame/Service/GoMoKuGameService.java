package MokuGame.Service;

import MokuGame.Core.GoMokuBoard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service class that manages GoMoku game logic including move validation,
 * turn management, and win detection.
 */
public class GoMoKuGameService {
    
    private static final Logger logger = LoggerFactory.getLogger(GoMoKuGameService.class);
    private final GoMokuBoard board;
    private char currentPlayer;
    private boolean gameOver;
    private char winner;
    private static final int WIN_LENGTH = 5;

    /**
     * Creates a new game service with the specified board.
     *
     * @param board the game board to use
     */
    public GoMoKuGameService(GoMokuBoard board) {
        this.board = board;
        this.currentPlayer = GoMokuBoard.Player1;
        this.gameOver = false;
        this.winner = GoMokuBoard.Empty;
        logger.info("New game service created with {}x{} board", board.getRows(), board.getColumns());
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
     * @return true if the move was valid and made, false otherwise
     */
    public boolean makeMove(int row, int col) {
        if (gameOver) {
            logger.warn("Attempted move after game over");
            return false;
        }

        if (!board.isValidPosition(row, col)) {
            logger.warn("Invalid position: ({}, {})", row, col);
            return false;
        }

        if (!board.isEmpty(row, col)) {
            logger.warn("Position ({}, {}) already occupied", row, col);
            return false;
        }

        board.setCell(row, col, currentPlayer);
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

        return true;
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
        
        return count >= WIN_LENGTH;
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
     * Resets the game to initial state, clearing the board.
     */
    public void reset() {
        board.clear();
        currentPlayer = GoMokuBoard.Player1;
        gameOver = false;
        winner = GoMokuBoard.Empty;
        logger.info("Game reset");
    }
}
