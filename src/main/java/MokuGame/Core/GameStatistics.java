package MokuGame.Core;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

/**
 * Tracks and manages game statistics for the Gomoku game.
 * Provides functionality to record game results, calculate statistics,
 * and persist data to external files.
 */
public class GameStatistics {
    private static final String STATS_FILE = "gomoku_stats.properties";

    // Basic statistics
    private int totalGames;
    private int player1Wins;
    private int player2Wins;
    private int draws;

    // Time-based statistics
    private long totalGameTime; // in milliseconds
    private long shortestGameTime; // in milliseconds
    private long longestGameTime; // in milliseconds

    // Current game tracking
    private LocalDateTime currentGameStartTime;
    private boolean currentGameActive;

    /**
     * Creates a new GameStatistics instance and loads existing data.
     */
    public GameStatistics() {
        reset();
        loadStatistics();
    }

    /**
     * Starts tracking a new game.
     */
    public void startGame() {
        currentGameStartTime = LocalDateTime.now();
        currentGameActive = true;
    }

    /**
     * Ends the current game and records the result.
     *
     * @param winner the winning player ('X', 'O', or '.' for draw)
     */
    public void endGame(char winner) {
        if (!currentGameActive) {
            return;
        }

        long gameDuration = java.time.Duration.between(currentGameStartTime, LocalDateTime.now()).toMillis();

        totalGames++;
        totalGameTime += gameDuration;

        if (gameDuration < shortestGameTime || shortestGameTime == 0) {
            shortestGameTime = gameDuration;
        }
        if (gameDuration > longestGameTime) {
            longestGameTime = gameDuration;
        }

        if (winner == GoMokuBoard.Player1) {
            player1Wins++;
        } else if (winner == GoMokuBoard.Player2) {
            player2Wins++;
        } else {
            draws++;
        }

        currentGameActive = false;
        saveStatistics();
    }

    /**
     * Gets the total number of games played.
     *
     * @return total games played
     */
    public int getTotalGames() {
        return totalGames;
    }

    /**
     * Gets the number of wins for Player 1.
     *
     * @return Player 1 wins
     */
    public int getPlayer1Wins() {
        return player1Wins;
    }

    /**
     * Gets the number of wins for Player 2.
     *
     * @return Player 2 wins
     */
    public int getPlayer2Wins() {
        return player2Wins;
    }

    /**
     * Gets the number of draws.
     *
     * @return number of draws
     */
    public int getDraws() {
        return draws;
    }

    /**
     * Gets the win rate for Player 1 as a percentage.
     *
     * @return Player 1 win rate (0.0 to 100.0)
     */
    public double getPlayer1WinRate() {
        return totalGames > 0 ? (double) player1Wins / totalGames * 100.0 : 0.0;
    }

    /**
     * Gets the win rate for Player 2 as a percentage.
     *
     * @return Player 2 win rate (0.0 to 100.0)
     */
    public double getPlayer2WinRate() {
        return totalGames > 0 ? (double) player2Wins / totalGames * 100.0 : 0.0;
    }

    /**
     * Gets the draw rate as a percentage.
     *
     * @return draw rate (0.0 to 100.0)
     */
    public double getDrawRate() {
        return totalGames > 0 ? (double) draws / totalGames * 100.0 : 0.0;
    }

    /**
     * Gets the average game duration in seconds.
     *
     * @return average game duration in seconds
     */
    public double getAverageGameDuration() {
        return totalGames > 0 ? (double) totalGameTime / totalGames / 1000.0 : 0.0;
    }

    /**
     * Gets the shortest game duration in seconds.
     *
     * @return shortest game duration in seconds
     */
    public double getShortestGameDuration() {
        return shortestGameTime / 1000.0;
    }

    /**
     * Gets the longest game duration in seconds.
     *
     * @return longest game duration in seconds
     */
    public double getLongestGameDuration() {
        return longestGameTime / 1000.0;
    }

    /**
     * Gets a formatted string representation of the statistics.
     *
     * @return formatted statistics string
     */
    public String getStatisticsSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Gomoku Game Statistics ===\n");
        sb.append(String.format("Total Games: %d\n", totalGames));
        sb.append(String.format("Player 1 Wins: %d (%.1f%%)\n", player1Wins, getPlayer1WinRate()));
        sb.append(String.format("Player 2 Wins: %d (%.1f%%)\n", player2Wins, getPlayer2WinRate()));
        sb.append(String.format("Draws: %d (%.1f%%)\n", draws, getDrawRate()));

        if (totalGames > 0) {
            sb.append(String.format("Average Game Duration: %.1f seconds\n", getAverageGameDuration()));
            sb.append(String.format("Shortest Game: %.1f seconds\n", getShortestGameDuration()));
            sb.append(String.format("Longest Game: %.1f seconds\n", getLongestGameDuration()));
        }

        return sb.toString();
    }

    /**
     * Resets all statistics to zero.
     */
    public void reset() {
        totalGames = 0;
        player1Wins = 0;
        player2Wins = 0;
        draws = 0;
        totalGameTime = 0;
        shortestGameTime = 0;
        longestGameTime = 0;
        currentGameActive = false;
        saveStatistics();
    }

    /**
     * Loads statistics from the properties file.
     */
    private void loadStatistics() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(STATS_FILE)) {
            props.load(fis);
            totalGames = Integer.parseInt(props.getProperty("totalGames", "0"));
            player1Wins = Integer.parseInt(props.getProperty("player1Wins", "0"));
            player2Wins = Integer.parseInt(props.getProperty("player2Wins", "0"));
            draws = Integer.parseInt(props.getProperty("draws", "0"));
            totalGameTime = Long.parseLong(props.getProperty("totalGameTime", "0"));
            shortestGameTime = Long.parseLong(props.getProperty("shortestGameTime", "0"));
            longestGameTime = Long.parseLong(props.getProperty("longestGameTime", "0"));
        } catch (IOException | NumberFormatException e) {
            // File doesn't exist or is corrupted, use defaults
        }
    }

    /**
     * Saves statistics to the properties file.
     */
    private void saveStatistics() {
        Properties props = new Properties();
        props.setProperty("totalGames", String.valueOf(totalGames));
        props.setProperty("player1Wins", String.valueOf(player1Wins));
        props.setProperty("player2Wins", String.valueOf(player2Wins));
        props.setProperty("draws", String.valueOf(draws));
        props.setProperty("totalGameTime", String.valueOf(totalGameTime));
        props.setProperty("shortestGameTime", String.valueOf(shortestGameTime));
        props.setProperty("longestGameTime", String.valueOf(longestGameTime));

        try (FileOutputStream fos = new FileOutputStream(STATS_FILE)) {
            props.store(fos, "Gomoku Game Statistics - " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        } catch (IOException e) {
            System.err.println("Failed to save statistics: " + e.getMessage());
        }
    }
}
