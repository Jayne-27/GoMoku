package MokuGame.Core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test suite for GoMokuBoard core functionality.
 * Tests board creation, cell operations, serialization, and validation.
 */
class GoMokuBoardTest {

    private GoMokuBoard board;

    /**
     * Sets up a standard 15x15 board before each test.
     */
    @BeforeEach
    void setUp() {
        board = new GoMokuBoard(15, 15);
    }

    @Test
    @DisplayName("Board should initialize with correct dimensions")
    void testBoardDimensions() {
        assertEquals(15, board.getRows(), "Board should have 15 rows");
        assertEquals(15, board.getColumns(), "Board should have 15 columns");
    }

    @Test
    @DisplayName("New board should be empty")
    void testNewBoardIsEmpty() {
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                assertEquals(GoMokuBoard.Empty, board.getCell(i, j),
                    "All cells should be empty initially");
            }
        }
    }

    @Test
    @DisplayName("Set and get cell should work correctly")
    void testSetAndGetCell() {
        board.setCell(7, 7, GoMokuBoard.Player1);
        assertEquals(GoMokuBoard.Player1, board.getCell(7, 7),
            "Cell should contain Player1 after setting");
        
        board.setCell(8, 8, GoMokuBoard.Player2);
        assertEquals(GoMokuBoard.Player2, board.getCell(8, 8),
            "Cell should contain Player2 after setting");
    }

    @Test
    @DisplayName("isEmpty should return true for empty cells")
    void testIsEmpty() {
        assertTrue(board.isEmpty(5, 5), "Empty cell should return true");
        
        board.setCell(5, 5, GoMokuBoard.Player1);
        assertFalse(board.isEmpty(5, 5), "Occupied cell should return false");
    }

    @Test
    @DisplayName("isValidPosition should validate coordinates")
    void testIsValidPosition() {
        assertTrue(board.isValidPosition(0, 0), "Top-left corner should be valid");
        assertTrue(board.isValidPosition(14, 14), "Bottom-right corner should be valid");
        assertTrue(board.isValidPosition(7, 7), "Middle position should be valid");
        
        assertFalse(board.isValidPosition(-1, 0), "Negative row should be invalid");
        assertFalse(board.isValidPosition(0, -1), "Negative column should be invalid");
        assertFalse(board.isValidPosition(15, 0), "Row beyond board should be invalid");
        assertFalse(board.isValidPosition(0, 15), "Column beyond board should be invalid");
        assertFalse(board.isValidPosition(20, 20), "Far out of bounds should be invalid");
    }

    @Test
    @DisplayName("Clear should reset all cells to empty")
    void testClear() {
        board.setCell(5, 5, GoMokuBoard.Player1);
        board.setCell(7, 7, GoMokuBoard.Player2);
        board.setCell(9, 9, GoMokuBoard.Player1);
        
        board.clear();
        
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                assertEquals(GoMokuBoard.Empty, board.getCell(i, j),
                    "All cells should be empty after clear");
            }
        }
    }

    @Test
    @DisplayName("Serialize should create string representation")
    void testSerialize() {
        board.setCell(0, 0, 'X');
        board.setCell(0, 1, 'O');
        
        String serialized = board.serialize();
        
        assertNotNull(serialized, "Serialized string should not be null");
        assertTrue(serialized.contains("X"), "Serialized string should contain X");
        assertTrue(serialized.contains("O"), "Serialized string should contain O");
        assertTrue(serialized.contains("|"), "Serialized string should contain row separator");
    }

    @Test
    @DisplayName("loadFromString should restore board state")
    void testLoadFromString() {
        // Set up original board
        board.setCell(0, 0, 'X');
        board.setCell(1, 1, 'O');
        board.setCell(2, 2, 'X');
        
        String serialized = board.serialize();
        
        // Create new board and load
        GoMokuBoard newBoard = new GoMokuBoard(15, 15);
        newBoard.loadFromString(serialized);
        
        assertEquals('X', newBoard.getCell(0, 0), "Loaded board should match original");
        assertEquals('O', newBoard.getCell(1, 1), "Loaded board should match original");
        assertEquals('X', newBoard.getCell(2, 2), "Loaded board should match original");
    }

    @Test
    @DisplayName("Serialize and load should preserve complete board state")
    void testSerializeAndLoadCompleteBoard() {
        // Fill board with pattern
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if ((i + j) % 3 == 0) {
                    board.setCell(i, j, 'X');
                } else if ((i + j) % 3 == 1) {
                    board.setCell(i, j, 'O');
                }
            }
        }
        
        String serialized = board.serialize();
        GoMokuBoard restored = new GoMokuBoard(15, 15);
        restored.loadFromString(serialized);
        
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                assertEquals(board.getCell(i, j), restored.getCell(i, j),
                    "Restored board should match original at (" + i + "," + j + ")");
            }
        }
    }

    @Test
    @DisplayName("Different board sizes should work")
    void testDifferentBoardSizes() {
        GoMokuBoard small = new GoMokuBoard(5, 5);
        assertEquals(5, small.getRows());
        assertEquals(5, small.getColumns());
        
        GoMokuBoard large = new GoMokuBoard(25, 25);
        assertEquals(25, large.getRows());
        assertEquals(25, large.getColumns());
    }

    @Test
    @DisplayName("Rectangular boards should work")
    void testRectangularBoard() {
        GoMokuBoard rect = new GoMokuBoard(10, 20);
        assertEquals(10, rect.getRows());
        assertEquals(20, rect.getColumns());
        
        assertTrue(rect.isValidPosition(9, 19), "Bottom-right should be valid");
        assertFalse(rect.isValidPosition(10, 20), "Just beyond bounds should be invalid");
    }

    @Test
    @DisplayName("toString should create visual representation")
    void testToString() {
        board.setCell(0, 0, 'X');
        board.setCell(1, 1, 'O');
        
        String display = board.toString();
        
        assertNotNull(display, "toString should not return null");
        assertTrue(display.contains("X"), "Display should contain X");
        assertTrue(display.contains("O"), "Display should contain O");
        assertTrue(display.contains("0"), "Display should contain row/column numbers");
    }

    @Test
    @DisplayName("All three player constants should be unique")
    void testPlayerConstants() {
        assertNotEquals(GoMokuBoard.Empty, GoMokuBoard.Player1);
        assertNotEquals(GoMokuBoard.Empty, GoMokuBoard.Player2);
        assertNotEquals(GoMokuBoard.Player1, GoMokuBoard.Player2);
    }

    @Test
    @DisplayName("Board edge cells should work correctly")
    void testBoardEdges() {
        // Test all four corners
        board.setCell(0, 0, 'X');
        board.setCell(0, 14, 'X');
        board.setCell(14, 0, 'X');
        board.setCell(14, 14, 'X');
        
        assertEquals('X', board.getCell(0, 0));
        assertEquals('X', board.getCell(0, 14));
        assertEquals('X', board.getCell(14, 0));
        assertEquals('X', board.getCell(14, 14));
    }

    @Test
    @DisplayName("Empty board serialize should work")
    void testEmptyBoardSerialize() {
        String serialized = board.serialize();
        assertNotNull(serialized);
        assertFalse(serialized.contains("X"));
        assertFalse(serialized.contains("O"));
    }

    @Test
    @DisplayName("Single cell board should work")
    void testSingleCellBoard() {
        GoMokuBoard tiny = new GoMokuBoard(1, 1);
        assertEquals(1, tiny.getRows());
        assertEquals(1, tiny.getColumns());
        
        tiny.setCell(0, 0, 'X');
        assertEquals('X', tiny.getCell(0, 0));
    }

    @Test
    @DisplayName("Load empty string should not crash")
    void testLoadEmptyString() {
        assertDoesNotThrow(() -> board.loadFromString(""),
            "Loading empty string should not throw exception");
    }
}
