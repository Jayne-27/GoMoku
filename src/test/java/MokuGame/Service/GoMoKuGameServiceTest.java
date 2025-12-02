package MokuGame.Service;

import MokuGame.Core.GoMokuBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive JUnit test suite for GoMoKuGameService business logic.
 * Tests game mechanics, move validation, win detection, and state management.
 */
class GoMoKuGameServiceTest {

    private GoMoKuGameService service;
    private GoMokuBoard board;

    /**
     * Sets up a fresh game service before each test.
     */
    @BeforeEach
    void setUp() {
        board = new GoMokuBoard(15, 15);
        service = new GoMoKuGameService(board);
    }

    @Test
    @DisplayName("New game should start with Player X")
    void testInitialPlayer() {
        assertEquals('X', service.getCurrentPlayer(), "Game should start with Player X");
    }

    @Test
    @DisplayName("New game should not be over")
    void testInitialGameState() {
        assertFalse(service.isGameOver(), "New game should not be over");
        assertEquals('.', service.getWinner(), "New game should have no winner");
    }

    @Test
    @DisplayName("Valid move should be accepted")
    void testValidMove() {
        assertTrue(service.makeMove(7, 7), "Valid move should be accepted");
        assertEquals('X', board.getCell(7, 7), "Cell should contain X after move");
    }

    @Test
    @DisplayName("Move on occupied cell should be rejected")
    void testOccupiedCellMove() {
        service.makeMove(5, 5);
        assertFalse(service.makeMove(5, 5), "Move on occupied cell should be rejected");
    }

    @Test
    @DisplayName("Move outside board bounds should be rejected")
    void testOutOfBoundsMove() {
        assertFalse(service.makeMove(-1, 5), "Negative row should be rejected");
        assertFalse(service.makeMove(5, -1), "Negative column should be rejected");
        assertFalse(service.makeMove(20, 5), "Row beyond board should be rejected");
        assertFalse(service.makeMove(5, 20), "Column beyond board should be rejected");
    }

    @Test
    @DisplayName("Players should alternate turns")
    void testPlayerAlternation() {
        assertEquals('X', service.getCurrentPlayer());
        service.makeMove(0, 0);
        assertEquals('O', service.getCurrentPlayer());
        service.makeMove(1, 1);
        assertEquals('X', service.getCurrentPlayer());
    }

    @Test
    @DisplayName("Horizontal win should be detected")
    void testHorizontalWin() {
        // Player X creates horizontal line
        for (int i = 0; i < 5; i++) {
            service.makeMove(7, i);     // X
            if (i < 4) {
                service.makeMove(8, i); // O
            }
        }
        assertTrue(service.isGameOver(), "Game should be over after 5 in a row");
        assertEquals('X', service.getWinner(), "X should win");
    }

    @Test
    @DisplayName("Vertical win should be detected")
    void testVerticalWin() {
        // Player X creates vertical line
        for (int i = 0; i < 5; i++) {
            service.makeMove(i, 7);     // X
            if (i < 4) {
                service.makeMove(i, 8); // O
            }
        }
        assertTrue(service.isGameOver(), "Game should be over after 5 in a column");
        assertEquals('X', service.getWinner(), "X should win");
    }

    @Test
    @DisplayName("Diagonal win (top-left to bottom-right) should be detected")
    void testDiagonalWinDescending() {
        // Player X creates diagonal line \
        for (int i = 0; i < 5; i++) {
            service.makeMove(i, i);         // X
            if (i < 4) {
                service.makeMove(i, i + 1); // O
            }
        }
        assertTrue(service.isGameOver(), "Game should be over after diagonal 5");
        assertEquals('X', service.getWinner(), "X should win");
    }

    @Test
    @DisplayName("Diagonal win (bottom-left to top-right) should be detected")
    void testDiagonalWinAscending() {
        // Player X creates diagonal line /
        for (int i = 0; i < 5; i++) {
            service.makeMove(4 - i, i);     // X
            if (i < 4) {
                service.makeMove(5 - i, i); // O
            }
        }
        assertTrue(service.isGameOver(), "Game should be over after diagonal 5");
        assertEquals('X', service.getWinner(), "X should win");
    }

    @Test
    @DisplayName("Draw should be detected when board is full")
    void testDrawDetection() {
        // Fill board without creating 5 in a row (checkerboard pattern won't work)
        // We'll fill strategically to avoid wins
        GoMokuBoard smallBoard = new GoMokuBoard(5, 5);
        GoMoKuGameService smallService = new GoMoKuGameService(smallBoard);
        
        // Pattern that fills board without wins
        int[][] pattern = {
            {0,0}, {0,1}, {0,2}, {0,3}, {0,4},
            {1,1}, {1,0}, {1,3}, {1,2}, {1,4},
            {2,2}, {2,1}, {2,0}, {2,4}, {2,3},
            {3,3}, {3,2}, {3,1}, {3,0}, {3,4},
            {4,4}, {4,3}, {4,2}, {4,1}, {4,0}
        };
        
        for (int[] move : pattern) {
            smallService.makeMove(move[0], move[1]);
        }
        
        assertTrue(smallService.isGameOver(), "Game should be over when board is full");
        assertEquals('.', smallService.getWinner(), "Full board without winner should be a draw");
    }

    @Test
    @DisplayName("Moves after game over should be rejected")
    void testMovesAfterGameOver() {
        // Create winning condition
        for (int i = 0; i < 5; i++) {
            service.makeMove(7, i);
            if (i < 4) {
                service.makeMove(8, i);
            }
        }
        
        assertTrue(service.isGameOver());
        assertFalse(service.makeMove(9, 9), "Move after game over should be rejected");
    }

    @Test
    @DisplayName("Reset should clear board and restart game")
    void testReset() {
        service.makeMove(5, 5);
        service.makeMove(6, 6);
        
        service.reset();
        
        assertEquals('X', service.getCurrentPlayer(), "Current player should reset to X");
        assertFalse(service.isGameOver(), "Game should not be over after reset");
        assertEquals('.', service.getWinner(), "Winner should be cleared after reset");
        assertTrue(board.isEmpty(5, 5), "Board should be empty after reset");
        assertTrue(board.isEmpty(6, 6), "Board should be empty after reset");
    }

    @Test
    @DisplayName("Win should work at board edges")
    void testWinAtBoardEdge() {
        // Test horizontal win at top edge
        for (int i = 0; i < 5; i++) {
            service.makeMove(0, i);
            if (i < 4) {
                service.makeMove(1, i);
            }
        }
        assertTrue(service.isGameOver(), "Should detect win at board edge");
        assertEquals('X', service.getWinner());
    }

    @Test
    @DisplayName("Win should work at board corners")
    void testWinAtCorner() {
        // Test diagonal win starting from corner
        for (int i = 0; i < 5; i++) {
            service.makeMove(i, i);
            if (i < 4) {
                service.makeMove(i, i + 1);
            }
        }
        assertTrue(service.isGameOver(), "Should detect win starting from corner");
        assertEquals('X', service.getWinner());
    }

    @Test
    @DisplayName("Exactly 5 in a row should win")
    void testExactlyFiveWins() {
        // Place exactly 5 X's in a row
        for (int i = 0; i < 5; i++) {
            service.makeMove(7, i);
            if (i < 4) {
                service.makeMove(8, i);
            }
        }
        assertTrue(service.isGameOver());
        assertEquals('X', service.getWinner());
    }

    @Test
    @DisplayName("More than 5 in a row should still win")
    void testMoreThanFiveWins() {
        // Place 6 X's in a row
        for (int i = 0; i < 6; i++) {
            service.makeMove(7, i);
            if (i < 5) {
                service.makeMove(8, i);
            }
        }
        assertTrue(service.isGameOver());
        assertEquals('X', service.getWinner());
    }

    @Test
    @DisplayName("Four in a row should not win")
    void testFourInRowNotWin() {
        for (int i = 0; i < 4; i++) {
            service.makeMove(7, i);
            service.makeMove(8, i);
        }
        assertFalse(service.isGameOver(), "Four in a row should not end game");
    }

    @Test
    @DisplayName("Player O can win")
    void testPlayerOWin() {
        // X makes moves that don't win
        service.makeMove(0, 0);
        service.makeMove(1, 0); // O
        service.makeMove(0, 1);
        service.makeMove(1, 1); // O
        service.makeMove(0, 2);
        service.makeMove(1, 2); // O
        service.makeMove(0, 3);
        service.makeMove(1, 3); // O
        service.makeMove(2, 0);
        service.makeMove(1, 4); // O wins
        
        assertTrue(service.isGameOver());
        assertEquals('O', service.getWinner(), "Player O should be able to win");
    }

    @Test
    @DisplayName("GetBoard should return the same board instance")
    void testGetBoard() {
        assertSame(board, service.getBoard(), "getBoard should return same board instance");
    }

    @Test
    @DisplayName("Different board sizes should work")
    void testDifferentBoardSizes() {
        GoMokuBoard smallBoard = new GoMokuBoard(10, 10);
        GoMoKuGameService smallService = new GoMoKuGameService(smallBoard);
        
        assertTrue(smallService.makeMove(5, 5), "Should work on smaller board");
        assertEquals('X', smallBoard.getCell(5, 5));
    }

    @Test
    @DisplayName("Non-square boards should work")
    void testNonSquareBoard() {
        GoMokuBoard rectBoard = new GoMokuBoard(10, 15);
        GoMoKuGameService rectService = new GoMoKuGameService(rectBoard);
        
        assertTrue(rectService.makeMove(5, 10), "Should work on rectangular board");
        assertEquals('X', rectBoard.getCell(5, 10));
    }
}
