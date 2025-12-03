package MokuGame.Computer;

import MokuGame.Core.GoMokuBoard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Enhanced computer player that uses Minimax algorithm with alpha-beta pruning
 * for intelligent move selection instead of random moves.
 */
public class computerPlayer {
    private static final Logger logger = LoggerFactory.getLogger(computerPlayer.class);

    // AI difficulty settings
    private final int MAX_DEPTH = 4; // Search depth for minimax
    private final int WIN_SCORE = 1000000; // Score for winning position
    private final int LOSE_SCORE = -1000000; // Score for losing position

    // Pattern scores for evaluation
    private final int FIVE_IN_ROW = 100000;
    private final int FOUR_IN_ROW = 10000;
    private final int THREE_IN_ROW = 1000;
    private final int TWO_IN_ROW = 100;
    private final int ONE_IN_ROW = 10;

    // AI plays as 'O' (Player2)
    private final char AI_PLAYER = GoMokuBoard.Player2;
    private final char HUMAN_PLAYER = GoMokuBoard.Player1;

    /**
     * Creates a new enhanced Computer player with Minimax AI.
     */
    public computerPlayer() {
        logger.info("Enhanced AI initialized with depth {}", MAX_DEPTH);
    }

    /**
     * Selects the best move using Minimax algorithm with alpha-beta pruning.
     *
     * @param board the game board
     * @return an array containing [row, column] of the selected move, or null if no moves available
     */
    public int[] selectMove(GoMokuBoard board) {
        List<int[]> availableMoves = getPrioritizedMoves(board);

        if (availableMoves.isEmpty()) {
            logger.warn("No available moves for AI player");
            return null;
        }

        // If it's the first move, play in center
        if (isFirstMove(board)) {
            int center = board.getRows() / 2;
            logger.info("AI playing first move at center ({}, {})", center, center);
            return new int[]{center, center};
        }

        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = null;

        logger.info("AI evaluating {} possible moves", availableMoves.size());

        // Evaluate each possible move
        for (int[] move : availableMoves) {
            // Make the move
            board.setCell(move[0], move[1], AI_PLAYER);

            // Use minimax to evaluate this move
            int score = minimax(board, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, false);

            // Undo the move
            board.setCell(move[0], move[1], GoMokuBoard.Empty);

            // Update best move if this score is better
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        logger.info("AI selected move at ({}, {}) with score {}", bestMove[0], bestMove[1], bestScore);
        return bestMove;
    }

    /**
     * Minimax algorithm with alpha-beta pruning.
     *
     * @param board current board state
     * @param depth remaining search depth
     * @param alpha alpha value for pruning
     * @param beta beta value for pruning
     * @param isMaximizing true if maximizing player (AI), false if minimizing (human)
     * @return the evaluated score for this position
     */
    private int minimax(GoMokuBoard board, int depth, int alpha, int beta, boolean isMaximizing) {
        // Check for terminal states
        if (isGameOver(board)) {
            return evaluateTerminalState(board);
        }

        // Depth limit reached
        if (depth == 0) {
            return evaluatePosition(board);
        }

        List<int[]> availableMoves = getPrioritizedMoves(board);

        if (isMaximizing) {
            // AI's turn (maximizing)
            int maxEval = Integer.MIN_VALUE;

            for (int[] move : availableMoves) {
                board.setCell(move[0], move[1], AI_PLAYER);
                int eval = minimax(board, depth - 1, alpha, beta, false);
                board.setCell(move[0], move[1], GoMokuBoard.Empty);

                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);

                // Alpha-beta pruning
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        } else {
            // Human's turn (minimizing)
            int minEval = Integer.MAX_VALUE;

            for (int[] move : availableMoves) {
                board.setCell(move[0], move[1], HUMAN_PLAYER);
                int eval = minimax(board, depth - 1, alpha, beta, true);
                board.setCell(move[0], move[1], GoMokuBoard.Empty);

                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);

                // Alpha-beta pruning
                if (beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }

    /**
     * Evaluates a terminal game state (win/loss/draw).
     */
    private int evaluateTerminalState(GoMokuBoard board) {
        char winner = getWinner(board);
        if (winner == AI_PLAYER) {
            return WIN_SCORE;
        } else if (winner == HUMAN_PLAYER) {
            return LOSE_SCORE;
        } else {
            return 0; // Draw
        }
    }

    /**
     * Evaluates the current board position using heuristic scoring.
     */
    private int evaluatePosition(GoMokuBoard board) {
        int score = 0;

        // Evaluate all possible lines (rows, columns, diagonals)
        score += evaluateLines(board);

        return score;
    }

    /**
     * Evaluates all lines (rows, columns, diagonals) on the board.
     */
    private int evaluateLines(GoMokuBoard board) {
        int score = 0;

        // Evaluate rows
        for (int i = 0; i < board.getRows(); i++) {
            score += evaluateLine(board, i, 0, 0, 1);
        }

        // Evaluate columns
        for (int j = 0; j < board.getColumns(); j++) {
            score += evaluateLine(board, 0, j, 1, 0);
        }

        // Evaluate diagonals (top-left to bottom-right)
        for (int i = 0; i < board.getRows(); i++) {
            score += evaluateLine(board, i, 0, 1, 1);
        }
        for (int j = 1; j < board.getColumns(); j++) {
            score += evaluateLine(board, 0, j, 1, 1);
        }

        // Evaluate diagonals (top-right to bottom-left)
        for (int i = 0; i < board.getRows(); i++) {
            score += evaluateLine(board, i, board.getColumns() - 1, 1, -1);
        }
        for (int j = board.getColumns() - 2; j >= 0; j--) {
            score += evaluateLine(board, 0, j, 1, -1);
        }

        return score;
    }

    /**
     * Evaluates a single line starting from (startRow, startCol) in direction (dRow, dCol).
     */
    private int evaluateLine(GoMokuBoard board, int startRow, int startCol, int dRow, int dCol) {
        int aiCount = 0;
        int humanCount = 0;
        int emptyCount = 0;

        // Count pieces in this line
        for (int i = 0; i < 5; i++) {
            int row = startRow + i * dRow;
            int col = startCol + i * dCol;

            if (!board.isValidPosition(row, col)) {
                break;
            }

            char cell = board.getCell(row, col);
            if (cell == AI_PLAYER) {
                aiCount++;
            } else if (cell == HUMAN_PLAYER) {
                humanCount++;
            } else {
                emptyCount++;
            }
        }

        // Score based on patterns
        if (aiCount == 5) return FIVE_IN_ROW;
        if (humanCount == 5) return -FIVE_IN_ROW;

        if (aiCount == 4 && emptyCount == 1) return FOUR_IN_ROW;
        if (humanCount == 4 && emptyCount == 1) return -FOUR_IN_ROW;

        if (aiCount == 3 && emptyCount == 2) return THREE_IN_ROW;
        if (humanCount == 3 && emptyCount == 2) return -THREE_IN_ROW;

        if (aiCount == 2 && emptyCount == 3) return TWO_IN_ROW;
        if (humanCount == 2 && emptyCount == 3) return -TWO_IN_ROW;

        if (aiCount == 1 && emptyCount == 4) return ONE_IN_ROW;
        if (humanCount == 1 && emptyCount == 4) return -ONE_IN_ROW;

        return 0;
    }

    /**
     * Gets prioritized list of available moves, focusing on positions near existing pieces.
     */
    private List<int[]> getPrioritizedMoves(GoMokuBoard board) {
        List<int[]> moves = new ArrayList<>();
        Set<String> moveSet = new HashSet<>();

        // First priority: positions adjacent to existing pieces
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                if (!board.isEmpty(i, j)) {
                    // Check all adjacent positions
                    for (int di = -1; di <= 1; di++) {
                        for (int dj = -1; dj <= 1; dj++) {
                            if (di == 0 && dj == 0) continue;

                            int ni = i + di;
                            int nj = j + dj;

                            if (board.isValidPosition(ni, nj) && board.isEmpty(ni, nj)) {
                                String key = ni + "," + nj;
                                if (!moveSet.contains(key)) {
                                    moves.add(new int[]{ni, nj});
                                    moveSet.add(key);
                                }
                            }
                        }
                    }
                }
            }
        }

        // If no adjacent moves (shouldn't happen in normal play), fall back to all empty positions
        if (moves.isEmpty()) {
            for (int i = 0; i < board.getRows(); i++) {
                for (int j = 0; j < board.getColumns(); j++) {
                    if (board.isEmpty(i, j)) {
                        moves.add(new int[]{i, j});
                    }
                }
            }
        }

        return moves;
    }

    /**
     * Checks if this is the first move of the game.
     */
    private boolean isFirstMove(GoMokuBoard board) {
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                if (!board.isEmpty(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if the game is over.
     */
    private boolean isGameOver(GoMokuBoard board) {
        return getWinner(board) != GoMokuBoard.Empty || isBoardFull(board);
    }

    /**
     * Determines the winner of the game.
     */
    private char getWinner(GoMokuBoard board) {
        // Check all possible win conditions
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                if (!board.isEmpty(i, j)) {
                    char player = board.getCell(i, j);
                    if (checkWinFromPosition(board, i, j, player)) {
                        return player;
                    }
                }
            }
        }
        return GoMokuBoard.Empty; // No winner
    }

    /**
     * Checks if placing a piece at (row, col) results in a win for the given player.
     */
    private boolean checkWinFromPosition(GoMokuBoard board, int row, int col, char player) {
        // Check all four directions
        int[][] directions = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};

        for (int[] dir : directions) {
            int count = 1; // Count the current piece

            // Check positive direction
            count += countInDirection(board, row, col, dir[0], dir[1], player);

            // Check negative direction
            count += countInDirection(board, row, col, -dir[0], -dir[1], player);

            if (count >= 5) {
                return true;
            }
        }
        return false;
    }

    /**
     * Counts consecutive pieces in a direction.
     */
    private int countInDirection(GoMokuBoard board, int row, int col, int dRow, int dCol, char player) {
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
     * Checks if the board is completely full.
     */
    private boolean isBoardFull(GoMokuBoard board) {
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                if (board.isEmpty(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }
}
