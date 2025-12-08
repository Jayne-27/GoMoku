# Gomoku (Five in a Row) Game

A complete implementation of the classic Gomoku game with both console and GUI interfaces.

## Features

- **Two Game Modes:**
  - Console-based interface for terminal play
  - Graphical User Interface (GUI) with Swing
  
- **Game Options:**
  - Player vs Player
  - Player vs Computer (AI opponent using Minimax algorithm)
  
- **Additional Features:**
  - Save/Load game states to PostgreSQL database
  - Undo/Redo functionality
  - Game statistics tracking
  - Customizable board sizes (5x5 to 25x25)
  - Win detection for 5 in a row (horizontal, vertical, diagonal)

## Requirements

- Java 21 or higher
- PostgreSQL database (optional, for save/load features)

## How to Run

### Quick Start (No Build Required)

The game has been pre-built. Simply use one of the following batch files:

#### Console Version
```
run-console.bat
```

#### GUI Version
```
run-gui.bat
```

### Building from Source

If you want to rebuild the project:

```
build.bat clean package -DskipTests
```

## How to Play

### Console Version

1. Run `run-console.bat`
2. Follow the menu options:
   - **1**: Create a new board
   - **2**: Load a saved board from database
   - **3**: Start playing
   - **4**: Edit board manually
   - **5**: Save current board
   - **6**: List saved boards
   - **7**: Exit

3. When playing:
   - Enter coordinates as: `row col` (e.g., `7 7` for center of 15x15 board)
   - Player X always goes first
   - Choose to play against computer or another player

### GUI Version

1. Run `run-gui.bat`
2. Click **New Game** to start
3. Choose board size (5-25)
4. Check **Play vs Computer** for AI opponent
5. Click on the board to place stones
6. Use buttons to:
   - **Save Game**: Save current state
   - **Load Game**: Load previous game
   - **Statistics**: View game statistics
   - **About**: View game information

## Game Rules

- Players alternate placing stones on the board
- **Player X** uses black stones (●)
- **Player O** uses white stones (○)
- First player to get **5 stones in a row** wins (horizontal, vertical, or diagonal)
- If the board fills up with no winner, the game is a draw

## Database Setup (Optional)

For save/load functionality, you need PostgreSQL:

1. Install PostgreSQL
2. Create a database named `gomoku`:
   ```sql
   CREATE DATABASE gomoku;
   ```
3. Update database credentials in `Database.java` if needed:
   - Default URL: `jdbc:postgresql://localhost:5432/gomoku`
   - Default User: `postgres`
   - Default Password: `alma`

The game will automatically create the required tables on first run.

## Project Structure

```
GoMoku/
├── src/main/java/MokuGame/
│   ├── Computer/          # AI player implementation
│   ├── Core/             # Game logic and board
│   ├── Service/          # Game services and database
│   ├── UI/               # User interfaces
│   └── Main.java         # Console application entry point
├── target/
│   └── gomoku-game.jar   # Executable JAR
├── build.bat             # Maven build script
├── run-console.bat       # Run console version
└── run-gui.bat           # Run GUI version
```

## Troubleshooting

### "mvn is not recognized"
- The project includes `build.bat` which uses IntelliJ's bundled Maven
- Alternatively, install Maven separately and add it to PATH

### Database Connection Failed
- The game will work without a database, but save/load features won't be available
- Make sure PostgreSQL is running if you want to use save/load
- Check database credentials in `Database.java`

### GUI Not Appearing
- Make sure you're using Java 21 or higher
- Try running from command line to see error messages

## Controls

### Console
- Type coordinates as `row col` (e.g., `7 7`)
- Follow menu prompts
- Press `Ctrl+C` to exit

### GUI
- Click on intersections to place stones
- Use menu buttons for game options
- Close window to exit

## Tips

- The computer AI is challenging but beatable
- Try starting in the center for better positioning
- Block your opponent's four-in-a-row immediately
- Look for opportunities to create multiple threats at once

## Credits

Developed as a comprehensive Java project demonstrating:
- Object-Oriented Programming
- Design Patterns (MVC, Strategy)
- GUI Programming with Swing
- Database Integration
- AI Algorithms (Minimax with Alpha-Beta Pruning)

Enjoy the game!
