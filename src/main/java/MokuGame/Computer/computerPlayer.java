package MokuGame.Computer;

import MokuGame.Core.GoMokuBoard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Computer player that selects moves randomly from available positions.
 */
public class computerPlayer {
    private static final Logger logger = LoggerFactory.getLogger(computerPlayer.class);
    private final Random random;



    /**
     * Creates a new Computer player.
     */
    public computerPlayer() {
        this.random = new Random();
    }

    /**
     * Selects a random valid move from the board.
     *
     * @param board the game board
     * @return an array containing [row, column] of the selected move, or null if no moves available
     */

    public int[] selectMove(GoMokuBoard board) {
        List<int[]> availableMoves = getAvailableMoves(board);

        if (availableMoves.isEmpty()) {
            logger.warn("No available moves for AI player");
            return null;
        }

        int[] selectedMove = availableMoves.get(random.nextInt(availableMoves.size()));
        logger.info("AI selected move at ({}, {})", selectedMove[0], selectedMove[1]);

        return selectedMove;
    }

    /**
     *  list of all available empty positions on the board.
     *
     * @param board the game board
     * @return a list of available positions as [row, column] arrays
     */

    private List<int[]> getAvailableMoves(GoMokuBoard board) {
        List<int[]> moves = new ArrayList<>();

        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                if (board.isEmpty(i, j)) {
                    moves.add(new int[]{i, j});
                }
            }
        }

        return moves;
    }
}
