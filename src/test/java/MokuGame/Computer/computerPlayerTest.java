package MokuGame.Computer;

import MokuGame.Core.GoMokuBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test suite for computerPlayer AI functionality.
 * Tests random move selection and move validation.
 */
class computerPlayerTest {

    private computerPlayer ai;
    private GoMokuBoard board;

    /**
     * Sets up AI player and board before each test.
     */
    @BeforeEach
    void setUp() {
        ai = new computerPlayer();
        board = new GoMokuBoard(15, 15);
    }

    @Test
    @DisplayName("AI should select a valid move on empty board")
    void testSelectMoveEmptyBoard() {
        int[] move = ai.selectMove(board);
        
        assertNotNull(move, "AI should return a move");
        assertEquals(2, move.length, "Move should have 2 coordinates");
        assertTrue(board.isValidPosition(move[0], move[1]), 
            "AI move should be within board bounds");
        assertTrue(board.isEmpty(move[0], move[1]), 
            "AI should select empty cell");
    }

    @Test
    @DisplayName("AI should select valid move on partially filled board")
    void testSelectMovePartialBoard() {
        // Fill some cells
        board.setCell(7, 7, 'X');
        board.setCell(7, 8, 'O');
        board.setCell(8, 7, 'X');
        
        int[] move = ai.selectMove(board);
        
        assertNotNull(move, "AI should return a move");
        assertTrue(board.isValidPosition(move[0], move[1]), 
            "AI move should be valid");
        assertTrue(board.isEmpty(move[0], move[1]), 
            "AI should not select occupied cell");
    }

    @Test
    @DisplayName("AI should return null when board is full")
    void testSelectMoveFullBoard() {
        GoMokuBoard smallBoard = new GoMokuBoard(3, 3);
        
        // Fill entire board
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                smallBoard.setCell(i, j, (i + j) % 2 == 0 ? 'X' : 'O');
            }
        }
        
        int[] move = ai.selectMove(smallBoard);
        assertNull(move, "AI should return null when no moves available");
    }

    @Test
    @DisplayName("AI should work with different board sizes")
    void testDifferentBoardSizes() {
        GoMokuBoard smallBoard = new GoMokuBoard(5, 5);
        int[] smallMove = ai.selectMove(smallBoard);
        assertNotNull(smallMove);
        assertTrue(smallBoard.isValidPosition(smallMove[0], smallMove[1]));
        
        GoMokuBoard largeBoard = new GoMokuBoard(25, 25);
        int[] largeMove = ai.selectMove(largeBoard);
        assertNotNull(largeMove);
        assertTrue(largeBoard.isValidPosition(largeMove[0], largeMove[1]));
    }

    @Test
    @DisplayName("AI should select from remaining cells only")
    void testSelectFromRemainingCells() {
        GoMokuBoard tinyBoard = new GoMokuBoard(2, 2);
        
        // Fill 3 out of 4 cells
        tinyBoard.setCell(0, 0, 'X');
        tinyBoard.setCell(0, 1, 'O');
        tinyBoard.setCell(1, 0, 'X');
        
        int[] move = ai.selectMove(tinyBoard);
        
        assertNotNull(move, "AI should find the last empty cell");
        assertEquals(1, move[0], "AI should select row 1");
        assertEquals(1, move[1], "AI should select column 1");
    }

    @Test
    @DisplayName("AI should make different moves over multiple calls")
    void testRandomness() {
        GoMokuBoard largeBoard = new GoMokuBoard(15, 15);
        boolean foundDifferentMove = false;
        
        int[] firstMove = ai.selectMove(largeBoard);
        
        // Try multiple times to find a different move
        for (int i = 0; i < 50; i++) {
            GoMokuBoard testBoard = new GoMokuBoard(15, 15);
            int[] move = ai.selectMove(testBoard);
            
            if (move[0] != firstMove[0] || move[1] != firstMove[1]) {
                foundDifferentMove = true;
                break;
            }
        }
        
        assertTrue(foundDifferentMove, 
            "AI should exhibit randomness in move selection");
    }

    @Test
    @DisplayName("AI should handle single available cell")
    void testSingleAvailableCell() {
        GoMokuBoard smallBoard = new GoMokuBoard(2, 2);
        smallBoard.setCell(0, 0, 'X');
        smallBoard.setCell(0, 1, 'O');
        smallBoard.setCell(1, 1, 'X');
        
        int[] move = ai.selectMove(smallBoard);
        
        assertNotNull(move);
        assertEquals(1, move[0]);
        assertEquals(0, move[1]);
    }

    @Test
    @DisplayName("Multiple AI instances should work independently")
    void testMultipleAIInstances() {
        computerPlayer ai1 = new computerPlayer();
        computerPlayer ai2 = new computerPlayer();
        
        int[] move1 = ai1.selectMove(board);
        int[] move2 = ai2.selectMove(board);
        
        assertNotNull(move1);
        assertNotNull(move2);
        assertTrue(board.isValidPosition(move1[0], move1[1]));
        assertTrue(board.isValidPosition(move2[0], move2[1]));
    }

    @Test
    @DisplayName("AI should work with rectangular boards")
    void testRectangularBoard() {
        GoMokuBoard rectBoard = new GoMokuBoard(10, 20);
        
        int[] move = ai.selectMove(rectBoard);
        
        assertNotNull(move);
        assertTrue(move[0] >= 0 && move[0] < 10, "Row should be in bounds");
        assertTrue(move[1] >= 0 && move[1] < 20, "Column should be in bounds");
    }

    @Test
    @DisplayName("AI should handle board with one empty cell in corner")
    void testOneEmptyCellInCorner() {
        GoMokuBoard board = new GoMokuBoard(3, 3);
        
        // Fill all except bottom-right corner
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i != 2 || j != 2) {
                    board.setCell(i, j, 'X');
                }
            }
        }
        
        int[] move = ai.selectMove(board);
        
        assertNotNull(move);
        assertEquals(2, move[0], "Should select row 2");
        assertEquals(2, move[1], "Should select column 2");
    }

    @Test
    @DisplayName("AI selection should always return valid coordinates")
    void testAlwaysValidCoordinates() {
        // Run multiple iterations to ensure consistency
        for (int iteration = 0; iteration < 20; iteration++) {
            GoMokuBoard testBoard = new GoMokuBoard(15, 15);
            
            // Make some random moves
            for (int i = 0; i < 5; i++) {
                testBoard.setCell(i, i, 'X');
            }
            
            int[] move = ai.selectMove(testBoard);
            
            assertNotNull(move, "AI should always return a move when possible");
            assertEquals(2, move.length, "Move should have 2 elements");
            assertTrue(testBoard.isValidPosition(move[0], move[1]), 
                "Move should be valid");
            assertTrue(testBoard.isEmpty(move[0], move[1]), 
                "Move should be to empty cell");
        }
    }

    @Test
    @DisplayName("AI should handle nearly full board")
    void testNearlyFullBoard() {
        GoMokuBoard board = new GoMokuBoard(5, 5);
        
        // Fill all but 2 cells
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (!(i == 2 && j == 2) && !(i == 3 && j == 3)) {
                    board.setCell(i, j, 'X');
                }
            }
        }
        
        int[] move = ai.selectMove(board);
        
        assertNotNull(move, "AI should find move on nearly full board");
        assertTrue((move[0] == 2 && move[1] == 2) || (move[0] == 3 && move[1] == 3),
            "AI should select one of the two empty cells");
    }
}
