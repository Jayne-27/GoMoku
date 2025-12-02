package MokuGame.Core;

/**
 * Represents the game board for Go-Moku.
 * The board is a rectangular grid where players place their pieces.
 */

public class GoMokuBoard {

    private final int rows;
    private final int columns;
    private final char[][] grid;


    public static final char Empty = '.';
    public static final char Player1 = 'X';
    public static final char Player2 = 'O';

    /**
     * Creates a new game board with specified dimensions using Constructor.
     *
     * @param rows the number of rows
     * @param columns the number of columns
     */

    public GoMokuBoard(int rows, int columns){
        this.rows = rows;
        this.columns = columns;
        this.grid = new char[rows][columns];
        ConstructBoard();

    }

    /**
     * Constructs the board with empty cells, depending on the dimension given.
     */

    private void ConstructBoard(){
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                grid[i][j] = Empty;
            }
        }
    }


    /**
     * Gets the number of rows on the board.
     *
     * @return the number of rows
     */
    public int getRows(){
        return rows;
    }


    /**
     * Gets the number of columns on the board.
     *
     * @return the number of columns
     */
    public int getColumns(){
        return columns;
    }


    /**
     * Gets the cell value at the specified position.
     *
     * @param row the row index
     * @param column the column index
     * @return the character at the specified position
     */
    public char getCell(int row, int column){
        return grid[row][column];
    }

    /**
     * Sets the cell value at the specified position on the goku board.
     *
     * @param row the row index
     * @param column the column index
     * @param player the player character to place
     */
    public void setCell(int row, int column, char player) {
        grid[row][column] = player;
    }

    /**
     * it Checks if the specified position is within board boundaries.
     *
     * @param row the row index
     * @param column the column index
     * @return true if the position is valid, false otherwise
     */
    public boolean isValidPosition(int row, int column) {
        return row >= 0 && row < rows && column >= 0 && column < columns;
    }

    /**
     * Checks if the specified cell is empty.
     *
     * @param row the row index
     * @param column the column index
     * @return true if the cell is empty, false otherwise
     */
    public boolean isEmpty(int row, int column) {
        return grid[row][column] == Empty;
    }

    /**
     * This Clears the entire board, setting all cells to empty for a new game.
     */
    public void clear() {
        ConstructBoard();
    }



    /**
     * Returns a string representation of the board for display.
     *
     * @return a formatted string showing the current board state
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Column headers for the goku game
        sb.append("   ");
                for(int i = 0; i< columns; i++){
                    sb.append(i).append(" ");
                }
                sb.append("\n");

        // Board rows to display where the goku game is played
               for(int i = 0; i < rows; i++){
                   sb.append(i).append(" ");// print row number
                   for (int j = 0; j < columns; j++){
                       sb.append(grid[i][j]).append(" ");// print each cell + space
                   }
                   sb.append("\n");
               }
               return sb.toString();
    }

    /**
     * creates a single string representing the board.
     * It keeps rows in order and separates them with |, so the board can be saved or transmitted
     *
     * @return a string representation of the board grid
     */

    public String serialize() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < rows; i++) {         // for each row
            for (int j = 0; j < columns; j++) {  // for each column
                sb.append(grid[i][j]);           // add the stone (., X, or O)
            }
            if (i < rows - 1) {                  // don't add | after last row
                sb.append("|");
            }
        }
        return sb.toString();                    // return the full string
    }


    /**string back to board
     *
     *
     * @param data the serialized board data
     */
    public void loadFromString(String data) {
        String[] rowData = data.split("\\|");//splits the string into rows, because | separate each row in the serialized format.
        for (int i = 0; i < rows && i < rowData.length; i++) { // Loop through each column of the current row
            for (int j = 0; j < columns && j < rowData[i].length(); j++) {
                grid[i][j] = rowData[i].charAt(j);
            }
        }
    }



}
