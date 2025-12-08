# QUICK START GUIDE

## Ready to Play! 🎮

Your Gomoku game is **fully compiled and ready to play**!

---

## Choose Your Version:

### 🖥️ **Console Version** (Text-based)
**Double-click:** `run-console.bat`

**What you'll see:**
```
WELCOME TO GO-MOKU GAME, LETS PLAYYYYYY...
     Five in a Row

   MAIN MENU
1. Create New Board
2. Load Saved Board
3. Play Game
...
```

**Quick Play:**
1. Type `1` and press Enter (Create New Board)
2. Press Enter to use default 15x15 board
3. Type `3` and press Enter (Play Game)
4. Type `n` for 2-player mode OR `y` to play vs Computer
5. Start playing by entering coordinates like: `7 7`

---

### 🎨 **GUI Version** (Graphical)
**Double-click:** `run-gui.bat`

**What you'll see:**
- A beautiful graphical game board
- Click to place stones
- Visual game interface

**Quick Play:**
1. Click **"New Game"** button
2. Enter board size (or just press OK for default)
3. Check **"Play vs Computer"** if you want AI opponent
4. Click on the board to play!

---

## Game Controls

### Console
- **Enter coordinates:** Type row number, space, column number
  - Example: `7 7` (center of 15x15 board)
- **Player X** (Black) always goes first
- **Player O** (White/Computer) goes second

### GUI
- **Click intersections** to place stones
- Black stone appears for your move
- White stone for opponent/computer

---

## Win Condition
**Get 5 stones in a row** (horizontal, vertical, or diagonal)

---

## Notes

⚠️ **Database Warning:** 
If you see a PostgreSQL connection error, **ignore it**! 
The game works perfectly without a database. 
You just won't be able to save/load games.

✅ **Everything else works:**
- Creating games
- Playing against human or computer
- Win detection
- Full gameplay

---

## Have Fun! 🎉

The AI is smart - can you beat it?

For more details, see [README.md](README.md)
