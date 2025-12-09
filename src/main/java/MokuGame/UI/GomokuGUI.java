package MokuGame.UI;

import MokuGame.Computer.computerPlayer;
import MokuGame.Core.GoMokuBoard;
import MokuGame.Core.InvalidMoveException;
import MokuGame.Core.GameStateException;
import MokuGame.Service.GoMoKuGameService;
import MokuGame.Service.H2Database;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Swing-based Graphical User Interface for the Gomoku game.
 * Provides visual gameplay with interactive board, menus, and game controls.
 */
public class GomokuGUI extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(GomokuGUI.class);

    // Game components
    private GoMoKuGameService gameService;
    private computerPlayer ai;
    private H2Database database;
    private boolean playingAgainstComputer = false;

    // UI components
    private BoardPanel boardPanel;
    private JLabel statusLabel;
    private JLabel currentPlayerLabel;
    private JButton newGameButton;
    private JButton saveGameButton;
    private JButton loadGameButton;
    private JButton undoButton;
    private JButton redoButton;
    private JCheckBox aiCheckBox;

    // Board styling
    private static final int CELL_SIZE = 40;
    private static final int BOARD_PADDING = 20;
    private Color boardColor = new Color(128, 0, 32); // Burgundy
    private Color lineColor = Color.BLACK;
    private Color playerXColor = Color.BLACK;
    private Color playerOColor = Color.WHITE;

    /**
     * Constructor for the Gomoku GUI.
     */
    public GomokuGUI() {
        this.database = new H2Database();
        this.ai = new computerPlayer();

        database.initializeDatabase();
        initializeGUI();
        showWelcomeDialog();

        logger.info("Gomoku GUI initialized successfully");
    }

    /**
     * Initializes the main GUI components.
     */
    private void initializeGUI() {
        setTitle("Gomoku Game - Five in a Row");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel with controls
        setupTopPanel();

        // Center panel with board
        setupCenterPanel();

        // Bottom panel with status
        setupBottomPanel();

        pack();
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);
    }

    /**
     * Sets up the top control panel.
     */
    private void setupTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout());

        newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> showNewGameDialog());

        saveGameButton = new JButton("Save Game");
        saveGameButton.addActionListener(e -> saveCurrentGame());
        saveGameButton.setEnabled(false);

        loadGameButton = new JButton("Load Game");
        loadGameButton.addActionListener(e -> showLoadGameDialog());

        undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> undoMove());
        undoButton.setEnabled(false);

        redoButton = new JButton("Redo");
        redoButton.addActionListener(e -> redoMove());
        redoButton.setEnabled(false);

        aiCheckBox = new JCheckBox("Play vs Computer");
        aiCheckBox.setSelected(false);

        JButton statisticsButton = new JButton("Statistics");
        statisticsButton.addActionListener(e -> showStatistics());

        JButton aboutButton = new JButton("About");
        aboutButton.addActionListener(e -> showAboutDialog());

        topPanel.add(newGameButton);
        topPanel.add(saveGameButton);
        topPanel.add(loadGameButton);
        topPanel.add(new JSeparator(SwingConstants.VERTICAL));
        topPanel.add(undoButton);
        topPanel.add(redoButton);
        topPanel.add(new JSeparator(SwingConstants.VERTICAL));
        topPanel.add(aiCheckBox);
        topPanel.add(new JSeparator(SwingConstants.VERTICAL));
        topPanel.add(statisticsButton);
        topPanel.add(aboutButton);

        add(topPanel, BorderLayout.NORTH);
    }

    /**
     * Sets up the center panel with the game board.
     */
    private void setupCenterPanel() {
        boardPanel = new BoardPanel();
        JScrollPane scrollPane = new JScrollPane(boardPanel);
        scrollPane.setPreferredSize(new Dimension(700, 700));
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Sets up the bottom status panel.
     */
    private void setupBottomPanel() {
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        currentPlayerLabel = new JLabel("Current Player: X", SwingConstants.CENTER);
        currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 14));

        statusLabel = new JLabel("Click 'New Game' to start playing!", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        bottomPanel.add(currentPlayerLabel);
        bottomPanel.add(statusLabel);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Custom panel for drawing the game board.
     */
    private class BoardPanel extends JPanel {
        private int boardSize = 15;

        public BoardPanel() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    handleBoardClick(e.getX(), e.getY());
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawBoard((Graphics2D) g);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(boardSize * CELL_SIZE + 2 * BOARD_PADDING,
                               boardSize * CELL_SIZE + 2 * BOARD_PADDING);
        }

        private void drawBoard(Graphics2D g2d) {
            // Set rendering hints for better quality
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw board background
            g2d.setColor(boardColor);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Draw grid lines
            g2d.setColor(lineColor);
            g2d.setStroke(new BasicStroke(1));

            for (int i = 0; i < boardSize; i++) {
                // Vertical lines
                int x = BOARD_PADDING + i * CELL_SIZE;
                g2d.drawLine(x, BOARD_PADDING, x, BOARD_PADDING + (boardSize - 1) * CELL_SIZE);

                // Horizontal lines
                int y = BOARD_PADDING + i * CELL_SIZE;
                g2d.drawLine(BOARD_PADDING, y, BOARD_PADDING + (boardSize - 1) * CELL_SIZE, y);
            }

            // Draw stones
            if (gameService != null) {
                for (int row = 0; row < boardSize; row++) {
                    for (int col = 0; col < boardSize; col++) {
                        if (!gameService.getBoard().isEmpty(row, col)) {
                            drawStone(g2d, row, col, gameService.getBoard().getCell(row, col));
                        }
                    }
                }
            }
        }

        private void drawStone(Graphics2D g2d, int row, int col, char player) {
            int x = BOARD_PADDING + col * CELL_SIZE;
            int y = BOARD_PADDING + row * CELL_SIZE;
            int stoneSize = (int) (CELL_SIZE * 0.8);

            // Draw stone
            g2d.setColor(player == 'X' ? playerXColor : playerOColor);
            g2d.fillOval(x - stoneSize/2, y - stoneSize/2, stoneSize, stoneSize);

            // Draw stone border
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(x - stoneSize/2, y - stoneSize/2, stoneSize, stoneSize);
        }

        public void setBoardSize(int size) {
            this.boardSize = size;
            repaint();
        }
    }

    /**
     * Handles board click events.
     */
    private void handleBoardClick(int x, int y) {
        if (gameService == null || gameService.isGameOver()) {
            return;
        }

        // If playing against computer and it's computer's turn, ignore clicks
        if (playingAgainstComputer && gameService.getCurrentPlayer() == 'O') {
            statusLabel.setText("It's the computer's turn!");
            return;
        }

        // Convert click coordinates to board coordinates
        int col = Math.round((x - BOARD_PADDING) / (float) CELL_SIZE);
        int row = Math.round((y - BOARD_PADDING) / (float) CELL_SIZE);

        if (row >= 0 && row < boardPanel.boardSize && col >= 0 && col < boardPanel.boardSize) {
            try {
                gameService.makeMove(row, col);
                boardPanel.repaint();
                updateStatusLabels();
                updateUndoRedoButtons();

                // Check for game end
                if (gameService.isGameOver()) {
                    handleGameEnd();
                } else if (playingAgainstComputer && gameService.getCurrentPlayer() == 'O') {
                    // Computer's turn
                    makeComputerMove();
                }

            } catch (InvalidMoveException | GameStateException e) {
                statusLabel.setText("Invalid move: " + e.getMessage());
            }
        }
    }

    /**
     * Makes a computer move with a delay for better UX.
     */
    private void makeComputerMove() {
        statusLabel.setText("Computer is thinking...");

        // Use a background thread for AI calculation to avoid freezing UI
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Thread.sleep(500); // Small delay for better UX

                ai.selectMove(gameService.getBoard());
                return null;
            }

            @Override
            protected void done() {
                try {
                    int[] move = ai.selectMove(gameService.getBoard());

                    if (move != null) {
                        gameService.makeMove(move[0], move[1]);
                        boardPanel.repaint();
                        updateStatusLabels();
                        updateUndoRedoButtons();

                        if (gameService.isGameOver()) {
                            handleGameEnd();
                        }
                    }
                } catch (Exception e) {
                    statusLabel.setText("Computer made invalid move: " + e.getMessage());
                }
            }
        };

        worker.execute();
    }

    /**
     * Handles game end scenarios.
     */
    private void handleGameEnd() {
        char winner = gameService.getWinner();
        String message;

        if (winner == 'X') {
            message = "Player X wins!";
        } else if (winner == 'O') {
            message = playingAgainstComputer ? "Computer wins!" : "Player O wins!";
        } else {
            message = "It's a draw!";
        }

        statusLabel.setText(message);
        currentPlayerLabel.setText("Game Over");

        // Show game end dialog
        showGameEndDialog(message);
    }

    /**
     * Updates the status labels.
     */
    private void updateStatusLabels() {
        if (gameService == null) {
            currentPlayerLabel.setText("No active game");
            statusLabel.setText("Click 'New Game' to start playing!");
            return;
        }

        if (gameService.isGameOver()) {
            currentPlayerLabel.setText("Game Over");
        } else {
            currentPlayerLabel.setText("Current Player: " + gameService.getCurrentPlayer());
            statusLabel.setText("Click on the board to make your move");
        }
    }

    /**
     * Updates the undo/redo button states.
     */
    private void updateUndoRedoButtons() {
        if (gameService != null) {
            undoButton.setEnabled(gameService.canUndo());
            redoButton.setEnabled(gameService.canRedo());
        } else {
            undoButton.setEnabled(false);
            redoButton.setEnabled(false);
        }
    }

    /**
     * Undoes the last move.
     */
    private void undoMove() {
        if (gameService == null) return;

        try {
            if (gameService.undo()) {
                boardPanel.repaint();
                updateStatusLabels();
                updateUndoRedoButtons();
                statusLabel.setText("Move undone");
            } else {
                statusLabel.setText("No moves to undo");
            }
        } catch (GameStateException e) {
            statusLabel.setText("Cannot undo: " + e.getMessage());
        }
    }

    /**
     * Redoes the last undone move.
     */
    private void redoMove() {
        if (gameService == null) return;

        try {
            if (gameService.redo()) {
                boardPanel.repaint();
                updateStatusLabels();
                updateUndoRedoButtons();
                statusLabel.setText("Move redone");

                // Check for game end after redo
                if (gameService.isGameOver()) {
                    handleGameEnd();
                }
            } else {
                statusLabel.setText("No moves to redo");
            }
        } catch (GameStateException e) {
            statusLabel.setText("Cannot redo: " + e.getMessage());
        }
    }

    /**
     * Shows the welcome dialog.
     */
    private void showWelcomeDialog() {
        JOptionPane.showMessageDialog(this,
            "Welcome to the enhanced Gomoku game!\n\n" +
            "• Click 'New Game' to start playing\n" +
            "• Choose to play against the computer or another player\n" +
            "• Click on the board to make your moves\n" +
            "• Save and load games as needed\n\n" +
            "Enjoy the game!",
            "Welcome to Gomoku!",
            JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows the new game dialog.
     */
    private void showNewGameDialog() {
        String input = JOptionPane.showInputDialog(this,
            "Enter board size (5-25):",
            "New Game",
            JOptionPane.QUESTION_MESSAGE);

        if (input != null && !input.trim().isEmpty()) {
            try {
                int boardSize = Integer.parseInt(input.trim());
                if (boardSize < 5) boardSize = 5;
                if (boardSize > 25) boardSize = 25;

                createNewGame(boardSize);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a valid number for board size.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Creates a new game.
     */
    private void createNewGame(int boardSize) {
        GoMokuBoard board = new GoMokuBoard(boardSize, boardSize);
        gameService = new GoMoKuGameService(board);
        playingAgainstComputer = aiCheckBox.isSelected();

        boardPanel.setBoardSize(boardSize);
        saveGameButton.setEnabled(true);
        updateUndoRedoButtons();

        statusLabel.setText("New " + boardSize + "x" + boardSize + " game started!");
        updateStatusLabels();

        logger.info("New game created with {}x{} board", boardSize, boardSize);
    }

    /**
     * Saves the current game.
     */
    private void saveCurrentGame() {
        if (gameService == null) return;

        String name = JOptionPane.showInputDialog(this,
            "Enter a name for this game:",
            "Save Game",
            JOptionPane.QUESTION_MESSAGE);

        if (name != null && !name.trim().isEmpty()) {
            if (database.saveBoard(name.trim(), gameService.getBoard())) {
                statusLabel.setText("Game saved as: " + name);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Could not save the game.",
                    "Save Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Shows the load game dialog.
     */
    private void showLoadGameDialog() {
        String[] savedGames = database.listBoards();

        if (savedGames.length == 0) {
            JOptionPane.showMessageDialog(this,
                "There are no saved games to load.",
                "No Saved Games",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String selectedGame = (String) JOptionPane.showInputDialog(this,
            "Select a game to load:",
            "Load Game",
            JOptionPane.QUESTION_MESSAGE,
            null,
            savedGames,
            savedGames[0]);

        if (selectedGame != null) {
            GoMokuBoard loaded = database.loadBoard(selectedGame);
            if (loaded != null) {
                gameService = new GoMoKuGameService(loaded);
                playingAgainstComputer = false;
                aiCheckBox.setSelected(false);

                boardPanel.setBoardSize(loaded.getRows());
                saveGameButton.setEnabled(true);
                updateUndoRedoButtons();

                statusLabel.setText("Game '" + selectedGame + "' loaded successfully!");
                updateStatusLabels();

                logger.info("Game '{}' loaded successfully", selectedGame);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Could not load the selected game.",
                    "Load Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Shows the statistics dialog.
     */
    private void showStatistics() {
        if (gameService == null || gameService.getStatistics() == null) {
            JOptionPane.showMessageDialog(this,
                "No game statistics available. Play some games first!",
                "No Statistics",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
            gameService.getStatistics().getStatisticsSummary(),
            "Game Statistics",
            JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows the about dialog.
     */
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
            "Enhanced Gomoku Game v2.0\n\n" +
            "A sophisticated implementation of the classic Five in a Row game.\n\n" +
            "Features:\n" +
            "• Intelligent AI opponent using Minimax algorithm\n" +
            "• Game statistics tracking\n" +
            "• Undo/Redo functionality\n" +
            "• Save/Load game states\n" +
            "• Modern Swing graphical interface\n\n" +
            "Enjoy the game!",
            "About Gomoku",
            JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows the game end dialog.
     */
    private void showGameEndDialog(String message) {
        Object[] options = {"Play Again", "Save Game", "Exit"};

        int choice = JOptionPane.showOptionDialog(this,
            message + "\n\nWhat would you like to do?",
            "Game Over",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);

        switch (choice) {
            case 0: // Play Again
                showNewGameDialog();
                break;
            case 1: // Save Game
                saveCurrentGame();
                break;
            case 2: // Exit
                System.exit(0);
                break;
        }
    }

    /**
     * Main method to launch the application.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Use default look and feel
            }

            new GomokuGUI().setVisible(true);
        });
    }
}
