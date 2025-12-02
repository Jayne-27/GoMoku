package MokuGame;

import UI.goMoku_Interface;

/**
 * Main entry point for the GoMoku game application.
 * Launches the command-line interface for the game.
 */
public class Main {
    /**
     * Starts the GoMoku game application.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        goMoku_Interface ui = new goMoku_Interface();
        ui.start();
    }
}