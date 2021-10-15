package game;

/**
 * A mutable MineSweeper board with individual 'square'.
 * Squares have pre-defined bomb status label of a mutable type.
 * Squares can be in 3 states:
 *      flagged (represent as 'F')
 *      dug: if 0 neighbors that have a bomb (represent as ' ')
 *           if N neighbors that have a bomb (represent as INTEGER 'N')
 *      untouched (represent as '-')
 *
 * A square can either contain a bomb or not.
 * The board coordinate system starts from the top left
 *      and in the range of 1 to {Max width/Max height}
 */
public interface Board {

    /**
     * Create a new board with all squares have status UNTOUCHED
     * and each square has the probability of containing
     * a bomb of 0.25
     *
     * @param width width of the board
     * @param height width of the board
     * @return
     */
    public static Board newBoard(int width, int height){
        return new ConcreteBoard(width, height);
    }

    /**
     * Flag a square.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @return false if the square at (x,y) status is DUG,
     *      true if status is FLAGGED or UNTOUCHED (if status is UNTOUCHED change to FLAGGED).
     */
    public boolean flag(int x, int y);

    /**
     * Dig a square
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @return true if the square at (x,y) status is {DUG and has no bomb} or status {!= DUG and has no bomb}.
     *       false if status {!= DUG and has a bomb} (change to contain no bomb and status to DUG)
     *
     *
     */
    public boolean dig(int x, int y);

    /**
     * Return the current state of the board.
     *
     * A square can be display as:
     * 1. flagged (represent as 'F')
     * 2. dug: if 0 neighbors that have a bomb (represent as ' ')
     *         if N neighbors that have a bomb (represent as INTEGER 'N')
     * 3. untouched (represent as '-')
     *
     */
    public String[][] currentState();

    /**
     * Un-flag a square
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @return false if the square at (x,y) status is DUG or UNTOUCHED.
     *         true if square status is FLAGGED (effect: change to UNTOUCHED).
     */
    public boolean unflag(int x, int y);

    /**
     * Get the status of a square
     *
     * @param x
     * @param y
     * @return one of 3 String {"UNTOUCHED", "FLAGGED", "DUG"}
     */
    public String getStatus(int x, int y);

    /**
     * Check if a square contains a bomb
     *
     * @param x
     * @param y
     * @return true if contains a bomb, false otherwise
     */
    public boolean hasBomb(int x, int y);

}
