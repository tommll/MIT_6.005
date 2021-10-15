package game;

import java.util.Objects;

/**
 * @ThreadSafe
 * A mutable Minesweeper board which implements the Board interface
 */
public class ConcreteBoard implements Board {
    private final int width;
    private final int height;
    private final Square[][] grid;
    public int numFlagged = 0;
    public int numDug = 0;
    private final Object lock = new Object();
    /*
    Rep-invariant:
        + width, height > 0
        + numFlagged, numDug >= 0
        + (numFlagged + numDug) <= (width * height)
        + If a square is dug, the number it reveals must equal the number of bombs of
            its surrounding neighbors
        + When a square with a bomb is dug,
            the neighborBomb of its surrounding neighbors must each decrease by 1 and
            the numDug variable is increased by 1
        + When a square is flagged successfully, the numFlagged variable is increased by 1
        + When a square is un-flagged successfully, the numFlagged variable is decreased by 1
    Rep-exposure:
        + width, height and grid are all private final
        + Other fields are primitives, get methods don't affect field value
    Thread-safe argument:
        + Square class is thread-safe
        + All read & modify methods are synchronized by 1 lock
        + Lock object is private final, not exposed to outside
     */

    public ConcreteBoard(int width, int height) {
        this.width = width;
        this.height = height;
        grid = new Square[width + 1][height + 1];

        // initialize bombs
        for (int i = 1; i <= width; i++){
            for (int j = 1; j <= height; j++) {
                if (Math.random() <= 0.2)
                    grid[i][j] = new Square(true);
                else
                    grid[i][j] = new Square(false);
            }
        }

        // set number of bombs for neighbors
        for (int i = 1; i <= width; i++){
            for (int j = 1; j <= height; j++){

                if (grid[i][j].isHasBomb()) {
                    if (isValid(i - 1, j - 1))
                        grid[i-1][j-1].setNeighborBombs(grid[i-1][j-1].getNeighborBombs() + 1);

                    if (isValid(i - 1, j))
                        grid[i-1][j].setNeighborBombs(grid[i-1][j].getNeighborBombs() + 1);

                    if (isValid(i - 1, j + 1))
                        grid[i-1][j+1].setNeighborBombs(grid[i-1][j+1].getNeighborBombs() + 1);

                    if (isValid(i, j - 1))
                        grid[i][j-1].setNeighborBombs(grid[i][j-1].getNeighborBombs() + 1);

                    if (isValid(i, j + 1))
                        grid[i][j+1].setNeighborBombs(grid[i][j+1].getNeighborBombs() + 1);

                    if (isValid(i + 1, j - 1))
                        grid[i+1][j-1].setNeighborBombs(grid[i+1][j-1].getNeighborBombs() + 1);

                    if (isValid(i + 1, j))
                        grid[i+1][j].setNeighborBombs(grid[i+1][j].getNeighborBombs() + 1);

                    if (isValid(i + 1, j + 1))
                        grid[i+1][j+1].setNeighborBombs(grid[i+1][j+1].getNeighborBombs() + 1);
                }
            }
        }

        checkRep();
    }

    @Override
    public boolean flag(int x, int y) {
        synchronized (lock) {
            if (!isValid(x, y)) return false;

            Square square = grid[x][y];

            if (square.getStatus() == Status.DUG) {
                return false;
            } else {
                if (square.getStatus() == Status.UNTOUCHED) {
                    square.setStatus(Status.FLAGGED);
                    numFlagged++;
                }

                checkRep();
                return true;
            }
        }
    }

    @Override
    public boolean dig(int x, int y) {
        synchronized (lock) {
            if (!isValid(x, y)) return false;

            Square square = grid[x][y];

            if (square.getStatus() == Status.DUG) {
                return true;
            } else if (!square.isHasBomb()) {
                expand(x, y);
                checkRep();
                return true;
            } else {
                revealBomb(x, y);
                square.setStatus(Status.DUG);
                numDug++;
                checkRep();
                return false;
            }
        }
    }

    /**
     * Reveal the bomb if there's any at location (i,j)
     *
     * @param i
     * @param j
     * @return None. If there's indeed a bomb then all surrounding neighbors all get their neighborBomb field
     *          value decreased by 1
     */
    private void revealBomb(int i, int j){
        synchronized (lock) {
            if (grid[i][j].isHasBomb()) {
                if (isValid(i - 1, j - 1))
                    grid[i - 1][j - 1].setNeighborBombs(grid[i - 1][j - 1].getNeighborBombs() - 1);

                if (isValid(i - 1, j))
                    grid[i - 1][j].setNeighborBombs(grid[i - 1][j].getNeighborBombs() - 1);

                if (isValid(i - 1, j + 1))
                    grid[i - 1][j + 1].setNeighborBombs(grid[i - 1][j + 1].getNeighborBombs() - 1);

                if (isValid(i, j - 1))
                    grid[i][j - 1].setNeighborBombs(grid[i][j - 1].getNeighborBombs() - 1);

                if (isValid(i, j + 1))
                    grid[i][j + 1].setNeighborBombs(grid[i][j + 1].getNeighborBombs() - 1);

                if (isValid(i + 1, j - 1))
                    grid[i + 1][j - 1].setNeighborBombs(grid[i + 1][j - 1].getNeighborBombs() - 1);

                if (isValid(i + 1, j))
                    grid[i + 1][j].setNeighborBombs(grid[i + 1][j].getNeighborBombs() - 1);

                if (isValid(i + 1, j + 1))
                    grid[i + 1][j + 1].setNeighborBombs(grid[i + 1][j + 1].getNeighborBombs() - 1);
            }
        }
    }

    @Override
    public String[][] currentState() {
        String[][] gameState = new String[0][0];

        synchronized (lock) {
            gameState = new String[width + 1][height + 1];

            for (int i = 1; i <= width; i++) {
                for (int j = 1; j <= height; j++) {
                    gameState[i][j] = grid[i][j].toString();
                }
            }
        }
        return gameState;
    }

    @Override
    public boolean unflag(int x, int y) {
        synchronized (lock) {
            if (!isValid(x, y)) return false;

            Square square = grid[x][y];

            if (square.getStatus() == Status.DUG || square.getStatus() == Status.UNTOUCHED) {
                return false;
            } else {
                square.setStatus(Status.UNTOUCHED);
                checkRep();
                numFlagged--;
                return true;
            }
        }
    }

    @Override
    public String getStatus(int x, int y) {
        synchronized (lock) {
            return grid[x][y].getStatus().toString();
        }
    }

    @Override
    public boolean hasBomb(int x, int y) {
        synchronized (lock) {
            return grid[x][y].isHasBomb();
        }
    }

    /**
     * Expand blank space from the selected square at (x,y) recursively.
     *
     * @param x
     * @param y
     * @return number of neighboring bombs, -1 if square has a bomb or has invalid coordinates
     */
    public int expand(int x, int y){
        synchronized (lock) {
            if (!isValid(x, y) || grid[x][y].isHasBomb())
                return -1;

            if (grid[x][y].getStatus() == Status.DUG)
                return grid[x][y].getNeighborBombs();

            grid[x][y].setStatus(Status.DUG);
            numDug++;

            // has > 0 neighbor bombs, return false
            if (grid[x][y].getNeighborBombs() != 0)
                return grid[x][y].getNeighborBombs();

            // has 0 neighbor bombs, keep expanding
            expand(x - 1, y - 1);
            expand(x - 1, y);
            expand(x - 1, y + 1);
            expand(x, y - 1);
            expand(x, y + 1);
            expand(x + 1, y - 1);
            expand(x + 1, y);
            expand(x + 1, y + 1);

            return 0;
        }
    }

    /**
     * Helper method to preserve the rep-invariant
     */
    private void checkRep(){
        synchronized (lock) {
            Objects.requireNonNull(height);
            Objects.requireNonNull(width);
            Objects.requireNonNull(grid);

            if (width <= 0 || height <= 0 || numDug < 0 || numFlagged < 0
                    || (numFlagged + numDug) > width * height)
                throw new IllegalStateException("flag: " + numFlagged + ", dug: " + numDug);

            // check the numbers
            for (int i = 1; i <= width; i++) {
                for (int j = 1; j <= height; j++) {
                    if (!isNeighborValid(i, j))
                        throw new IllegalStateException("(" + i + ", " + j + "): hasBomb: " + grid[i][j].isHasBomb() + ", status: " + grid[i][j].getStatus() + ", neighborBomb: " + grid[i][j].getNeighborBombs());
                }
            }
        }
    }

    /**
     * Helper method to check validity of coordinates
     * @param x
     * @param y
     * @return true if x and y are within the allowed coordinate system of the board
     */
    private boolean isValid(int x, int y) {
        synchronized (lock) {
            return x > 0 && x <= width && y > 0 && y <= height;
        }
    }

    /**
     * Helper method to check whether the neighboring squares and current square
     * preserve the rep-invariant
     * @param i
     * @param j
     * @return
     */
    private boolean isNeighborValid(int i, int j){
        synchronized (lock) {
            if (grid[i][j].isHasBomb()) {
                if (isValid(i - 1, j - 1) && grid[i - 1][j - 1].getNeighborBombs() == 0)
                    return false;

                if (isValid(i - 1, j) && grid[i - 1][j].getNeighborBombs() == 0)
                    return false;

                if (isValid(i - 1, j + 1) && grid[i - 1][j + 1].getNeighborBombs() == 0)
                    return false;

                if (isValid(i, j - 1) && grid[i][j - 1].getNeighborBombs() == 0)
                    return false;

                if (isValid(i, j + 1) && grid[i][j + 1].getNeighborBombs() == 0)
                    return false;

                if (isValid(i + 1, j - 1) && grid[i + 1][j - 1].getNeighborBombs() == 0)
                    return false;

                if (isValid(i + 1, j) && grid[i + 1][j].getNeighborBombs() == 0)
                    return false;

                if (isValid(i + 1, j + 1) && grid[i + 1][j + 1].getNeighborBombs() == 0)
                    return false;

                return true;
            } else {
                // check the numbers
                int bombs = 0;

                if (isValid(i - 1, j - 1) && grid[i - 1][j - 1].isHasBomb())
                    bombs++;

                if (isValid(i - 1, j) && grid[i - 1][j].isHasBomb())
                    bombs++;

                if (isValid(i - 1, j + 1) && grid[i - 1][j + 1].isHasBomb())
                    bombs++;

                if (isValid(i, j - 1) && grid[i][j - 1].isHasBomb())
                    bombs++;

                if (isValid(i, j + 1) && grid[i][j + 1].isHasBomb())
                    bombs++;

                if (isValid(i + 1, j - 1) && grid[i + 1][j - 1].isHasBomb())
                    bombs++;

                if (isValid(i + 1, j) && grid[i + 1][j].isHasBomb())
                    bombs++;

                if (isValid(i + 1, j + 1) && grid[i + 1][j + 1].isHasBomb())
                    bombs++;

                return grid[i][j].getNeighborBombs() == bombs;
            }
        }
    }

    /**
     * A string presentation of the game state
     * @return
     */
    public String toString() {
        synchronized (lock) {
            String[][] game = currentState();
            StringBuilder result = new StringBuilder();
            boolean isFirstLine = true;
            boolean isFirstColumn = true;

            for (String[] arr : game) {
                if (!isFirstLine) {
                    isFirstColumn = true;

                    for (String x : arr) {
                        if (!isFirstColumn)
                            result.append(x + "| ");
                        else
                            isFirstColumn = false;
                    }
                    result.append("\n");
                } else
                    isFirstLine = false;
            }
            return new String(result);
        }
    }

    /**
     * A string representation of the hidden numbers and bomb
     * @return
     */
    public String endGame() {
        synchronized (lock) {
            StringBuilder result = new StringBuilder();

            for (int i = 1; i <= width; i++) {
                for (int j = 1; j <= height; j++) {
                    if (grid[i][j].isHasBomb())
                        result.append("X |");
                    else
                        result.append(grid[i][j].getNeighborBombs() + " |");
                }
                result.append("\n");
            }

            return new String(result);
        }
    }

    public int getWidth() {
        synchronized (lock) {
            return width;
        }
    }

    public int getHeight() {
        synchronized (lock) {
            return height;
        }
    }

    public int getNumFlagged() {
        synchronized (lock) {
            return numFlagged;
        }
    }

    public int getNumDug() {
        synchronized (lock) {
            return numDug;
        }
    }


}

enum Status {UNTOUCHED, FLAGGED, DUG}

/**
 * @ThreadSafe
 * A mutable square in the Minesweeper board
 */
final class Square{
    private boolean hasBomb;
    private Status status = Status.UNTOUCHED;
    private int neighborBombs = 0;
    private final Object lock = new Object();

    /*
    Rep-invariant:
        A square can't have a bomb and be in DUG state at the same time
        When a bomb is diffused, change square to DUG state, hasBomb = false
    Rep-exposure:
        All fields are primitives, get methods don't affect field value
    Thread-safety argument:
        This class use synchronization to guarantee atomic read & modify operations
        The lock is private and not exposed to outside
     */
    public Square(boolean hasBomb) {
        this.hasBomb = hasBomb;
        checkRep();
    }

    private void checkRep(){
        synchronized (lock) {
            Objects.requireNonNull(hasBomb);
            Objects.requireNonNull(status);

            if (status == Status.DUG && hasBomb)
                throw new IllegalArgumentException("Square can't have a bomb and be in DUG state at the same time");
        }
    }

    public String toString(){
        synchronized (lock) {
            if (status == Status.FLAGGED)
                return "F";
            else if (status == Status.UNTOUCHED)
                return "-";
            else {
                if (neighborBombs == 0)
                    return " ";
                else
                    return String.valueOf(neighborBombs);
            }
        }
    }

    public boolean isHasBomb() {
        synchronized (lock) {
            return hasBomb;
        }
    }

    public Status getStatus() {
        synchronized (lock) {
            return status;
        }
    }

    public  void setStatus(Status status) {
        synchronized (lock) {
            this.status = status;

            if (status == Status.DUG)
                hasBomb = false;
            checkRep();
        }
    }

    public int getNeighborBombs() {
        synchronized (lock) {
            return neighborBombs;
        }
    }

    public void setNeighborBombs(int neighborBombs) {
        synchronized (lock) {
            this.neighborBombs = neighborBombs;
        }
    }
}
