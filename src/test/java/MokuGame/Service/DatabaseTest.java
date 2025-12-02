package MokuGame.Service;

import MokuGame.Core.GoMokuBoard;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test suite for Database service methods.
 * Tests database operations including save, load, list, and delete functionality.
 * 
 * Note: These tests require a running PostgreSQL database with connection details:
 * - URL: jdbc:postgresql://localhost:5432/gomoku
 * - User: postgres
 * - Password: alma
 * 
 * The tests use a prefix to avoid conflicts with production data.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatabaseTest {

    private Database database;
    private static final String TEST_PREFIX = "test_";

    /**
     * Sets up database connection before each test.
     */
    @BeforeEach
    void setUp() {
        database = new Database();
        database.initializeDatabase();
    }

    /**
     * Cleans up test data after each test.
     */
    @AfterEach
    void tearDown() {
        // Clean up any test boards
        String[] boards = database.listBoards();
        for (String name : boards) {
            if (name.startsWith(TEST_PREFIX)) {
                database.deleteBoard(name);
            }
        }
    }

    @Test
    @Order(1)
    @DisplayName("Database initialization should succeed")
    void testInitialization() {
        assertDoesNotThrow(() -> database.initializeDatabase(),
            "Database initialization should not throw exception");
    }

    @Test
    @Order(2)
    @DisplayName("Save board should return true on success")
    void testSaveBoard() {
        GoMokuBoard board = new GoMokuBoard(15, 15);
        board.setCell(7, 7, 'X');
        board.setCell(7, 8, 'O');
        
        boolean result = database.saveBoard(TEST_PREFIX + "save_test", board);
        assertTrue(result, "Saving board should return true");
    }

    @Test
    @Order(3)
    @DisplayName("Load board should retrieve saved board correctly")
    void testLoadBoard() {
        GoMokuBoard original = new GoMokuBoard(10, 10);
        original.setCell(3, 3, 'X');
        original.setCell(4, 4, 'O');
        original.setCell(5, 5, 'X');
        
        String testName = TEST_PREFIX + "load_test";
        database.saveBoard(testName, original);
        
        GoMokuBoard loaded = database.loadBoard(testName);
        
        assertNotNull(loaded, "Loaded board should not be null");
        assertEquals(10, loaded.getRows(), "Loaded board should have same rows");
        assertEquals(10, loaded.getColumns(), "Loaded board should have same columns");
        assertEquals('X', loaded.getCell(3, 3), "Cell content should match");
        assertEquals('O', loaded.getCell(4, 4), "Cell content should match");
        assertEquals('X', loaded.getCell(5, 5), "Cell content should match");
    }

    @Test
    @Order(4)
    @DisplayName("Load non-existent board should return null")
    void testLoadNonExistentBoard() {
        GoMokuBoard result = database.loadBoard(TEST_PREFIX + "nonexistent_board_xyz");
        assertNull(result, "Loading non-existent board should return null");
    }

    @Test
    @Order(5)
    @DisplayName("List boards should return all saved board names")
    void testListBoards() {
        // Save multiple test boards
        database.saveBoard(TEST_PREFIX + "board1", new GoMokuBoard(15, 15));
        database.saveBoard(TEST_PREFIX + "board2", new GoMokuBoard(15, 15));
        database.saveBoard(TEST_PREFIX + "board3", new GoMokuBoard(15, 15));
        
        String[] boards = database.listBoards();
        
        assertNotNull(boards, "Board list should not be null");
        assertTrue(boards.length >= 3, "Should have at least 3 test boards");
        
        boolean foundBoard1 = false;
        boolean foundBoard2 = false;
        boolean foundBoard3 = false;
        
        for (String name : boards) {
            if (name.equals(TEST_PREFIX + "board1")) foundBoard1 = true;
            if (name.equals(TEST_PREFIX + "board2")) foundBoard2 = true;
            if (name.equals(TEST_PREFIX + "board3")) foundBoard3 = true;
        }
        
        assertTrue(foundBoard1 && foundBoard2 && foundBoard3, 
            "All saved boards should appear in list");
    }

    @Test
    @Order(6)
    @DisplayName("Update existing board should work")
    void testUpdateBoard() {
        String testName = TEST_PREFIX + "update_test";
        
        // Save initial board
        GoMokuBoard original = new GoMokuBoard(15, 15);
        original.setCell(5, 5, 'X');
        database.saveBoard(testName, original);
        
        // Update with new board
        GoMokuBoard updated = new GoMokuBoard(15, 15);
        updated.setCell(5, 5, 'X');
        updated.setCell(6, 6, 'O');
        database.saveBoard(testName, updated);
        
        // Load and verify
        GoMokuBoard loaded = database.loadBoard(testName);
        assertNotNull(loaded);
        assertEquals('X', loaded.getCell(5, 5), "Original cell should be preserved");
        assertEquals('O', loaded.getCell(6, 6), "New cell should be added");
    }

    @Test
    @Order(7)
    @DisplayName("Delete existing board should return true")
    void testDeleteExistingBoard() {
        String testName = TEST_PREFIX + "delete_test";
        database.saveBoard(testName, new GoMokuBoard(15, 15));
        
        boolean result = database.deleteBoard(testName);
        assertTrue(result, "Deleting existing board should return true");
        
        GoMokuBoard loaded = database.loadBoard(testName);
        assertNull(loaded, "Deleted board should not be loadable");
    }

    @Test
    @Order(8)
    @DisplayName("Delete non-existent board should return false")
    void testDeleteNonExistentBoard() {
        boolean result = database.deleteBoard(TEST_PREFIX + "nonexistent_xyz");
        assertFalse(result, "Deleting non-existent board should return false");
    }

    @Test
    @Order(9)
    @DisplayName("Save board with different dimensions")
    void testDifferentBoardSizes() {
        GoMokuBoard small = new GoMokuBoard(5, 5);
        GoMokuBoard large = new GoMokuBoard(20, 20);
        GoMokuBoard rect = new GoMokuBoard(10, 15);
        
        assertTrue(database.saveBoard(TEST_PREFIX + "small", small));
        assertTrue(database.saveBoard(TEST_PREFIX + "large", large));
        assertTrue(database.saveBoard(TEST_PREFIX + "rect", rect));
        
        GoMokuBoard loadedSmall = database.loadBoard(TEST_PREFIX + "small");
        GoMokuBoard loadedLarge = database.loadBoard(TEST_PREFIX + "large");
        GoMokuBoard loadedRect = database.loadBoard(TEST_PREFIX + "rect");
        
        assertEquals(5, loadedSmall.getRows());
        assertEquals(20, loadedLarge.getRows());
        assertEquals(10, loadedRect.getRows());
        assertEquals(15, loadedRect.getColumns());
    }

    @Test
    @Order(10)
    @DisplayName("Save empty board should work")
    void testSaveEmptyBoard() {
        GoMokuBoard emptyBoard = new GoMokuBoard(15, 15);
        assertTrue(database.saveBoard(TEST_PREFIX + "empty", emptyBoard));
        
        GoMokuBoard loaded = database.loadBoard(TEST_PREFIX + "empty");
        assertNotNull(loaded);
        
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                assertEquals('.', loaded.getCell(i, j), 
                    "Empty board should have all empty cells");
            }
        }
    }

    @Test
    @Order(11)
    @DisplayName("Save full board should work")
    void testSaveFullBoard() {
        GoMokuBoard fullBoard = new GoMokuBoard(5, 5);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                fullBoard.setCell(i, j, (i + j) % 2 == 0 ? 'X' : 'O');
            }
        }
        
        assertTrue(database.saveBoard(TEST_PREFIX + "full", fullBoard));
        
        GoMokuBoard loaded = database.loadBoard(TEST_PREFIX + "full");
        assertNotNull(loaded);
        
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                assertEquals((i + j) % 2 == 0 ? 'X' : 'O', loaded.getCell(i, j),
                    "Full board cells should match pattern");
            }
        }
    }

    @Test
    @Order(12)
    @DisplayName("Special characters in board name should work")
    void testSpecialCharactersInName() {
        String specialName = TEST_PREFIX + "game_2024-11-22_player1";
        GoMokuBoard board = new GoMokuBoard(15, 15);
        
        assertTrue(database.saveBoard(specialName, board));
        assertNotNull(database.loadBoard(specialName));
    }

    @Test
    @Order(13)
    @DisplayName("Multiple saves and loads should work")
    void testMultipleSavesAndLoads() {
        String testName = TEST_PREFIX + "multiple_test";
        
        for (int i = 0; i < 5; i++) {
            GoMokuBoard board = new GoMokuBoard(15, 15);
            board.setCell(i, i, 'X');
            database.saveBoard(testName, board);
            
            GoMokuBoard loaded = database.loadBoard(testName);
            assertNotNull(loaded);
            assertEquals('X', loaded.getCell(i, i));
        }
    }
}
