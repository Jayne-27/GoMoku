package MokuGame.Service;

import MokuGame.Core.GoMokuBoard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * H2 Database implementation - Embedded database requiring no installation.
 * Stores game data in a local file (./data/gomoku.h2.db)
 * This is a drop-in replacement for PostgreSQL Database class.
 */
public class H2Database {

    private static final Logger logger = LoggerFactory.getLogger(H2Database.class);

    // H2 embedded database - stored in ./data/ folder
    private static final String DB_URL = "jdbc:h2:./data/gomoku;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASS = "";

    /**
     * Initializes the H2 database by creating the boards table if it doesn't exist.
     * This method should be called when the application starts.
     */
    public void initializeDatabase() {
        String ddl = """
            CREATE TABLE IF NOT EXISTS boards (
                name        VARCHAR(255) PRIMARY KEY,
                rows        INT  NOT NULL,
                columns     INT  NOT NULL,
                board_data  TEXT NOT NULL,
                created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            );
            """;
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(ddl);
            logger.info("H2 Database connected successfully! Table 'boards' is ready.");
            logger.info("Game data is stored in: ./data/gomoku.h2.db");
        } catch (SQLException e) {
            logger.error("Failed to initialize H2 database. Save/Load features disabled.", e);
        }
    }

    /**
     * Establishes a connection to the H2 database.
     *
     * @return a Connection object to the database
     * @throws SQLException if connection fails
     */
    private Connection connect() throws SQLException {
        try {
            // Explicitly load H2 driver
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("H2 Driver not found in classpath", e);
        }
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    /**
     * Saves a game board to the database. If a board with the same name exists,
     * it will be updated with the new data.
     *
     * @param name the name to save the board under
     * @param board the game board to save
     * @return true if save was successful, false otherwise
     */
    public boolean saveBoard(String name, GoMokuBoard board) {
        String sql = """
            MERGE INTO boards (name, rows, columns, board_data, created_at)
            KEY (name)
            VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)
            """;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, board.getRows());
            pstmt.setInt(3, board.getColumns());
            pstmt.setString(4, board.serialize());

            pstmt.executeUpdate();
            logger.info("Board '{}' saved successfully to H2 database", name);
            return true;

        } catch (SQLException e) {
            logger.error("Failed to save board '{}'", name, e);
            return false;
        }
    }

    /**
     * Loads a game board from the database by name.
     *
     * @param name the name of the board to load
     * @return the loaded GoMokuBoard, or null if not found or error occurs
     */
    public GoMokuBoard loadBoard(String name) {
        String sql = "SELECT rows, columns, board_data FROM boards WHERE name = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int rows = rs.getInt("rows");
                int columns = rs.getInt("columns");
                String boardData = rs.getString("board_data");

                GoMokuBoard board = new GoMokuBoard(rows, columns);
                board.loadFromString(boardData);
                
                logger.info("Board '{}' loaded successfully from H2 database", name);
                return board;
            } else {
                logger.warn("No board found with name '{}'", name);
                return null;
            }

        } catch (SQLException e) {
            logger.error("Failed to load board '{}'", name, e);
            return null;
        }
    }

    /**
     * Retrieves a list of all saved board names from the database.
     *
     * @return an array of board names, empty array if none exist
     */
    public String[] listBoards() {
        String sql = "SELECT name FROM boards ORDER BY created_at DESC";
        List<String> names = new ArrayList<>();

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                names.add(rs.getString("name"));
            }
            
            logger.info("Retrieved {} board names from H2 database", names.size());

        } catch (SQLException e) {
            logger.error("Failed to list boards", e);
        }

        return names.toArray(new String[0]);
    }

    /**
     * Deletes a board from the database by name.
     *
     * @param name the name of the board to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteBoard(String name) {
        String sql = "DELETE FROM boards WHERE name = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            int affected = pstmt.executeUpdate();
            
            if (affected > 0) {
                logger.info("Board '{}' deleted successfully from H2 database", name);
                return true;
            } else {
                logger.warn("No board found with name '{}' to delete", name);
                return false;
            }

        } catch (SQLException e) {
            logger.error("Failed to delete board '{}'", name, e);
            return false;
        }
    }
}
