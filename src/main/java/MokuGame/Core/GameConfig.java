package MokuGame.Core;

import java.io.*;
import java.util.Properties;

/**
 * Configuration manager for the Gomoku game.
 * Handles loading and saving game settings from external configuration files.
 */
public class GameConfig {

    private static final String CONFIG_FILE = "gomoku.properties";
    private final Properties properties;

    // Default configuration values
    private static final int DEFAULT_BOARD_SIZE = 15;
    private static final int DEFAULT_WIN_LENGTH = 5;
    private static final String DEFAULT_AI_DIFFICULTY = "EASY";
    private static final boolean DEFAULT_SOUND_ENABLED = true;
    private static final boolean DEFAULT_ANIMATIONS_ENABLED = true;

    /**
     * Creates a new GameConfig instance and loads configuration from file.
     */
    public GameConfig() {
        properties = new Properties();
        loadDefaultConfig();
        loadConfigFromFile();
    }

    /**
     * Loads default configuration values.
     */
    private void loadDefaultConfig() {
        properties.setProperty("board.size", String.valueOf(DEFAULT_BOARD_SIZE));
        properties.setProperty("game.winLength", String.valueOf(DEFAULT_WIN_LENGTH));
        properties.setProperty("ai.difficulty", DEFAULT_AI_DIFFICULTY);
        properties.setProperty("ui.sound.enabled", String.valueOf(DEFAULT_SOUND_ENABLED));
        properties.setProperty("ui.animations.enabled", String.valueOf(DEFAULT_ANIMATIONS_ENABLED));
    }

    /**
     * Loads configuration from the properties file.
     */
    private void loadConfigFromFile() {
        File configFile = new File(CONFIG_FILE);
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                properties.load(fis);
            } catch (IOException e) {
                System.err.println("Warning: Could not load configuration file: " + e.getMessage());
            }
        }
    }

    /**
     * Saves the current configuration to file.
     */
    public void saveConfig() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            properties.store(fos, "Gomoku Game Configuration");
        } catch (IOException e) {
            System.err.println("Error: Could not save configuration file: " + e.getMessage());
        }
    }

    /**
     * Gets the board size from configuration.
     *
     * @return the board size
     */
    public int getBoardSize() {
        return Integer.parseInt(properties.getProperty("board.size"));
    }

    /**
     * Sets the board size in configuration.
     *
     * @param size the board size to set
     */
    public void setBoardSize(int size) {
        if (size >= 5 && size <= 25) {
            properties.setProperty("board.size", String.valueOf(size));
        } else {
            throw new IllegalArgumentException("Board size must be between 5 and 25");
        }
    }

    /**
     * Gets the win length from configuration.
     *
     * @return the win length
     */
    public int getWinLength() {
        return Integer.parseInt(properties.getProperty("game.winLength"));
    }

    /**
     * Sets the win length in configuration.
     *
     * @param length the win length to set
     */
    public void setWinLength(int length) {
        if (length >= 3 && length <= 10) {
            properties.setProperty("game.winLength", String.valueOf(length));
        } else {
            throw new IllegalArgumentException("Win length must be between 3 and 10");
        }
    }

    /**
     * Gets the AI difficulty from configuration.
     *
     * @return the AI difficulty level
     */
    public String getAiDifficulty() {
        return properties.getProperty("ai.difficulty");
    }

    /**
     * Sets the AI difficulty in configuration.
     *
     * @param difficulty the AI difficulty to set
     */
    public void setAiDifficulty(String difficulty) {
        if ("EASY".equals(difficulty) || "MEDIUM".equals(difficulty) || "HARD".equals(difficulty)) {
            properties.setProperty("ai.difficulty", difficulty);
        } else {
            throw new IllegalArgumentException("AI difficulty must be EASY, MEDIUM, or HARD");
        }
    }

    /**
     * Checks if sound is enabled.
     *
     * @return true if sound is enabled, false otherwise
     */
    public boolean isSoundEnabled() {
        return Boolean.parseBoolean(properties.getProperty("ui.sound.enabled"));
    }

    /**
     * Sets sound enabled/disabled.
     *
     * @param enabled true to enable sound, false to disable
     */
    public void setSoundEnabled(boolean enabled) {
        properties.setProperty("ui.sound.enabled", String.valueOf(enabled));
    }

    /**
     * Checks if animations are enabled.
     *
     * @return true if animations are enabled, false otherwise
     */
    public boolean isAnimationsEnabled() {
        return Boolean.parseBoolean(properties.getProperty("ui.animations.enabled"));
    }

    /**
     * Sets animations enabled/disabled.
     *
     * @param enabled true to enable animations, false to disable
     */
    public void setAnimationsEnabled(boolean enabled) {
        properties.setProperty("ui.animations.enabled", String.valueOf(enabled));
    }

    /**
     * Gets a configuration property value.
     *
     * @param key the property key
     * @return the property value, or null if not found
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Sets a configuration property value.
     *
     * @param key the property key
     * @param value the property value
     */
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
}
