package UI;

import MokuGame.Computer.computerPlayer;
import MokuGame.Core.GoMokuBoard;
import MokuGame.Service.GoMoKuGameService;
import MokuGame.Service.Database;

import java.util.Scanner;

public class goMoku_Interface {

    private final Scanner scanner = new Scanner(System.in);
    private GoMoKuGameService gameService;
    private final Database database = new Database();
    private final computerPlayer ai = new computerPlayer();
    private boolean playingAgainstComputer = false;

    public goMoku_Interface() {
        database.initializeDatabase(); // connects to PostgreSQL with password "alma"
    }

    public void start() {
        System.out.println("""
            
           WELCOME TO GO-MOKU GAME, LETS PLAYYYYYY...           
                     Five in a Row              
            
            """);

        while (true) {
            showMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> createNewBoard();
                case "2" -> loadBoardFromDatabase();
                case "3" -> { if (gameService != null) playGame(); else noBoardError(); }
                case "4" -> { if (gameService != null) editBoard(); else noBoardError(); }
                case "5" -> { if (gameService != null) saveBoardToDatabase(); else noBoardError(); }
                case "6" -> listSavedBoards();
                case "7" -> { System.out.println("Thanks for playing! Goodbye!"); return; }
                default -> System.out.println("Invalid choice! Please enter 1–7");
            }
        }
    }

    private void showMainMenu() {

        System.out.println("   MAIN MENU");
        System.out.println("1. Create New Board");
        System.out.println("2. Load Saved Board");
        System.out.println("3. Play Game" + (gameService != null ? " (ready)" : ""));
        System.out.println("4. Edit Board (place stones manually)");
        System.out.println("5. Save Current Board");
        System.out.println("6. List Saved Boards");
        System.out.println("7. Exit");
        System.out.print("Choose (1-7): ");
    }

    private void noBoardError() {
        System.out.println("No board! Create one (1) or load one (2) first.");
    }

    private void createNewBoard() {
        System.out.print("Enter board size (default 15): ");
        String input = scanner.nextLine().trim();
        int size = input.isEmpty() ? 15 : Integer.parseInt(input);

        if (size < 5) {
            System.out.println("Minimum size is 5x5! Using 15x15.");
            size = 15;
        }

        GoMokuBoard board = new GoMokuBoard(size, size);
        gameService = new GoMoKuGameService(board);
        playingAgainstComputer = false;

        System.out.println("New " + size + "x" + size + " board created!");
        System.out.println(board);
    }

    private void loadBoardFromDatabase() {
        System.out.print("Enter saved game name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) return;

        GoMokuBoard loaded = database.loadBoard(name);
        if (loaded != null) {
            gameService = new GoMoKuGameService(loaded);
            playingAgainstComputer = false;
            System.out.println("Board '" + name + "' loaded successfully!");
            System.out.println(loaded);
        } else {
            System.out.println("No board found with name: " + name);
        }
    }

    private void playGame() {
        System.out.print("Play against Computer? (y/n): ");
        playingAgainstComputer = scanner.nextLine().trim().equalsIgnoreCase("y");

        while (!gameService.isGameOver()) {
            System.out.println(gameService.getBoard());

            if (playingAgainstComputer && gameService.getCurrentPlayer() == 'O') {
                System.out.println("Computer (O) is thinking...");
                int[] move = ai.selectMove(gameService.getBoard());
                if (move != null) {
                    gameService.makeMove(move[0], move[1]);
                    System.out.println("Computer played: " + move[0] + " " + move[1]);
                }
            } else {
                System.out.print("Player " + gameService.getCurrentPlayer() + " → enter row col: ");
                String[] parts = scanner.nextLine().trim().split("\\s+");
                if (parts.length != 2) {
                    System.out.println("Please enter two numbers (e.g 2 2)");
                    continue;
                }
                try {
                    int row = Integer.parseInt(parts[0]);
                    int col = Integer.parseInt(parts[1]);
                    if (!gameService.makeMove(row, col)) {
                        System.out.println("Invalid! Try again.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter numbers only!");
                }
            }
        }

        System.out.println(gameService.getBoard());
        char winner = gameService.getWinner();
        if (winner == 'X') {
            System.out.println("PLAYER X WINS!");
        } else if (winner == 'O') {
            System.out.println(playingAgainstComputer ? "COMPUTER WINS!" : "PLAYER O WINS!");
        } else {
            System.out.println("IT'S A DRAW!");
        }

        System.out.print("\nSave this game? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            saveBoardToDatabase();
        }
    }

    private void editBoard() {
        System.out.println("Edit Mode — type: row col X   or   row col O   or   row col .   (or 'done')");
        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            if (line.equalsIgnoreCase("done")) break;

            String[] p = line.split("\\s+");
            if (p.length != 3) {
                System.out.println("Format: row col X/O/.");
                continue;
            }
            try {
                int r = Integer.parseInt(p[0]);
                int c = Integer.parseInt(p[1]);
                char stone = p[2].toUpperCase().charAt(0);
                if (stone == 'X' || stone == 'O' || stone == '.') {
                    gameService.getBoard().setCell(r, c, stone);
                    System.out.println(gameService.getBoard());
                } else {
                    System.out.println("Use X, O, or . only");
                }
            } catch (Exception e) {
                System.out.println("Invalid input!");
            }
        }
    }

    private void saveBoardToDatabase() {
        System.out.print("Save as (name): ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            name = "save_" + System.currentTimeMillis();
        }
        if (database.saveBoard(name, gameService.getBoard())) {
            System.out.println("Game saved as: " + name + " → visible in pgAdmin!");
        } else {
            System.out.println("Save failed!");
        }
    }

    private void listSavedBoards() {
        String[] list = database.listBoards();
        System.out.println("\nSaved Boards (" + list.length + "):");
        if (list.length == 0) {
            System.out.println("   (none)");
        } else {
            for (int i = 0; i < list.length; i++) {
                System.out.println("   " + (i + 1) + ". " + list[i]);
            }
        }
        System.out.println();
    }

    public static void main(String[] args) {
        new goMoku_Interface().start();
    }
}