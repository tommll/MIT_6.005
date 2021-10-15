import game.Board;
import game.ConcreteBoard;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    static Board board;
    static int size = 5;

    void newBoard() {
        board = Board.newBoard(size, size);
    }

    @org.junit.jupiter.api.Test
    void testNewBoard(){
        newBoard();

        boolean hasAtLeastOneNotUNTOUCHED = false;
        int bombs = 0;

        for (int i = 1; i <= size; i++){
            for (int j = 1; j <= size; j++){
                if (!board.getStatus(i, j).equals("UNTOUCHED"))
                    hasAtLeastOneNotUNTOUCHED = true;

                if (board.hasBomb(i, j))
                    bombs++;
            }
        }

        assertTrue (bombs <= size * size / 2);
        assertFalse(hasAtLeastOneNotUNTOUCHED);
    }


    /**
     * TEST STRATEGY for coordinate system of
     * flag(), dig(), unflag()
     *
     * @para int x: < 0, 0, 0 < x < width, x=width, x > width
     * @para int y: < 0, 0, 0 < y < height, y=height, y > height
     *
     * Boundary condition: 0, width, height
     * @output false: x|y <= 0, x >width, y > height
     *
     */

    /**
     * TEST STRATEGY for status in flag()
     *
     * @output true: status UNTOUCHED, FLAGGED
     *         false: status DUG
     *
     */
    @org.junit.jupiter.api.Test
    void flag() {
        newBoard();
        assertFalse(board.flag(-1, 2));
        assertFalse(board.flag(0, 2));
        assertTrue(board.flag(1, 2));
        assertTrue(board.flag(5, 2));
        assertFalse(board.flag(6, 2));

        assertFalse(board.flag(2, -1));
        assertFalse(board.flag(2, 0));
        assertTrue(board.flag(2, 1));
        assertTrue(board.flag(2, 5));
        assertFalse(board.flag(2, 6));

        assertFalse(board.flag(-1, -1));
        assertFalse(board.flag(0, 0));
        assertTrue(board.flag(2, 1));
        assertTrue(board.flag(5, 5));
        assertFalse(board.flag(6, 6));

        // test status
        newBoard();
        assertEquals(board.getStatus(1, 1), "UNTOUCHED");
        board.flag(1,1);
        assertEquals(board.getStatus(1, 1), "FLAGGED");
        board.flag(1, 1);
        assertEquals(board.getStatus(1, 1), "FLAGGED");

        // TODO:
        // test for the case when previous status is DUG
        board.dig(1, 1);
        assertEquals(board.flag(1, 1), false);
    }

    /**
     * TEST STRATEGY for status in dig()
     *
     * @output true: status = DUG, status != DUG and has no bomb
     *         false: status != DUG and has a bomb
     */
    @org.junit.jupiter.api.Test
    void dig() {
        // make sure all squares contain no bomb
        newBoard();
        assertFalse(board.dig(-1, 2));
        assertFalse(board.dig(0, 2));
        assertFalse(board.dig(6, 2));

        assertFalse(board.dig(2, -1));
        assertFalse(board.dig(2, 0));
        assertFalse(board.dig(2, 6));

        assertFalse(board.dig(-1, -1));
        assertFalse(board.dig(0, 0));
        assertFalse(board.dig(6, 6));

        newBoard();

        System.out.println(board.toString());

        // test status
        newBoard();

        for (int i = 1; i <= size; i++){
            for (int j = 1; j <= size; j++) {
                if (!board.getStatus(i, j).equals("DUG") && board.hasBomb(i, j)){
                    assertFalse(board.dig(i, j)); // status != DUG and has bomb
                    assertEquals(board.getStatus(i, j), "DUG");
                    assertFalse(board.hasBomb(i, j));
                    assertTrue(board.dig(i, j));  // status = DUG
                }
            }
        }

        for (int i = 1; i <= size; i++){
            for (int j = 1; j <= size; j++) {
                if (!board.hasBomb(i, j) && !board.getStatus(i, j).equals("DUG")){ // != DUG and has no bomb
                    assertTrue(board.dig(i, j));
                }
            }
        }


    }

    /**
     * TEST STRATEGY
     *
     * @userCommand: FLAG, DIG, UNFLAG
     * @squareRepresentation: ' ', '-', 'int', 'F'
     *
     * 1/ Command FLAG:
     *  Prev_rep => new_rep:
     *   + ' ' => ' '
     *   + 'int' => 'int'
     *   + '-' => 'F'
     *
     * 2/ Command DIG:
     *  Prev_rep => new_rep:
     *   + ' ' => ' '
     *   + 'int' => 'int'
     *   + '-' or 'F' => 'int' or ' ' (also reveal the bomb)
     *
     * 3/ Command UNFLAG:
     *  Prev_rep => new_rep:
     *   + ' ' => ' '
     *   + 'int' => 'int'
     *   + 'F' => '-'
     *
     */
    @org.junit.jupiter.api.Test
    void currentStateFLAGCommandTest() {
        newBoard();

        // '-' => 'F'
        assertEquals(board.currentState()[1][1].toString(), "-");
        board.flag(1, 1);
        assertEquals(board.getStatus(1, 1), "FLAGGED");
        assertEquals(board.currentState()[1][1].toString(), "F");



        for (int i = 1; i <= size; i++){
            for (int j = 1; j <= size; j++){
                System.out.println();
                board.dig(i, j);

                // ' ' => ' '
                if (board.currentState()[i][j].toString().equals(" ")){
                    board.flag(i , j);
                    assertEquals(board.currentState()[i][j].toString(), " ");
                }
                // 'int' => 'int'
                else {
                    int neighborBomb = Integer.parseInt(board.currentState()[i][j].toString());
                    board.flag(i , j);
                    assertEquals(board.currentState()[i][j].toString(), String.valueOf(neighborBomb));
                }
            }
        }

    }

    @org.junit.jupiter.api.Test
    void testCurrentStateDIGCommandTest(){
        newBoard();

        // '-' => ' ' or 'int'
        board.dig(1, 1);
        assertEquals(board.getStatus(1, 1), "DUG");

        if (!board.currentState()[1][1].toString().equals(" ")){
            assertTrue(Integer.parseInt(board.currentState()[1][1].toString()) > 0);
        }

        // 'F' => ' ' or 'int'
        board.flag(2, 2);
        board.dig(2, 2);

        if (!board.currentState()[2][2].toString().equals(" ")){
            assertTrue(Integer.parseInt(board.currentState()[2][2].toString()) > 0);
        }

        for (int i = 1; i <= size; i++){
            for (int j = 1; j <= size; j++){
                board.dig(i, j);

                // ' ' => ' '
                if (board.currentState()[i][j].toString().equals(" ")){
                    board.dig(i , j);
                    assertEquals(board.currentState()[i][j].toString(), " ");
                }
                // 'int' => 'int'
                else {
                    int neighborBomb = Integer.parseInt(board.currentState()[i][j].toString());
                    board.dig(i , j);
                    assertEquals(board.currentState()[i][j].toString(), String.valueOf(neighborBomb));
                }
            }
        }
    }

    @Test
    void testCurrentStateUNFLAGCommandTest(){
        newBoard();

        // 'F' => '-'
        board.flag(2, 2);
        board.unflag(2, 2);

        assertEquals(board.currentState()[2][2].toString(), "-");


        for (int i = 1; i <= size; i++){
            for (int j = 1; j <= size; j++){
                board.dig(i, j);

                // ' ' => ' '
                if (board.currentState()[i][j].toString().equals(" ")){
                    board.unflag(i , j);
                    assertEquals(board.currentState()[i][j].toString(), " ");
                }
                // 'int' => 'int'
                else {
                    int neighborBomb = Integer.parseInt(board.currentState()[i][j].toString());
                    board.unflag(i , j);
                    assertEquals(board.currentState()[i][j].toString(), String.valueOf(neighborBomb));
                }
            }
        }
    }


    /**
     * TEST STRATEGY for status in dig()
     *
     * @output true: status FLAGGED (effect: change to UNTOUCHED)
     *         false: status DUG, UNTOUCHED
     */
    @org.junit.jupiter.api.Test
    void unflag() {
        // make sure all squares contain no bomb
        newBoard();
        assertFalse(board.unflag(-1, 2));
        assertFalse(board.unflag(0, 2));
        assertFalse(board.unflag(6, 2));

        assertFalse(board.unflag(2, -1));
        assertFalse(board.unflag(2, 0));
        assertFalse(board.unflag(2, 6));

        assertFalse(board.unflag(-1, -1));
        assertFalse(board.unflag(0, 0));
        assertFalse(board.unflag(6, 6));

        board.flag(2, 1);
        board.flag(5, 5);
        assertTrue(board.unflag(2, 1));
        assertEquals(board.getStatus(2, 1), "UNTOUCHED");
        assertTrue(board.unflag(5, 5));
        assertEquals(board.getStatus(5, 5), "UNTOUCHED");

        assertFalse(board.unflag( 3, 2)); // state = UNTOUCHED
        board.dig( 3, 4);
        assertFalse(board.unflag(3, 4)); // state = DUG

    }
    //  MULTI-THREAD TESTING
    private static final ConcreteBoard concreteBoard = new ConcreteBoard(5,5);

    static int flagX1 = 2;
    static int flagY1 = 2;
    private static Thread flagClient1 = new Thread(new Runnable() {
        @Override
        public void run() {
            if (concreteBoard.flag(flagX1, flagY1))
                System.out.println("Client 1 flag successfully!");
            else
                System.out.println("Client 1 flag unsuccessfully!");

            System.out.println("Client 1 sees: \n" + concreteBoard.toString());
        }
    });
    static int flagX2 = 2;
    static int flagY2 = 2;
    private static Thread flagClient2 = new Thread(new Runnable() {
        @Override
        public void run() {
            if (concreteBoard.flag(flagX2,flagY2))
                System.out.println("Client 2 flag successfully!");
            else
                System.out.println("Client 2 flag unsuccessfully!");

            System.out.println("Client 2 sees: \n" + concreteBoard.toString());
        }
    });
    static int flagX3 = 2;
    static int flagY3 = 2;
    private static Thread flagClient3 = new Thread(new Runnable() {
        @Override
        public void run() {
            if (concreteBoard.flag(flagX3,flagY3))
                System.out.println("Client 3 flag successfully!");
            else
                System.out.println("Client 3 flag unsuccessfully!");

            System.out.println("Client 3 sees: \n" + concreteBoard.toString());
        }
    });
    static int flagX4 = 2;
    static int flagY4 = 2;
    private static Thread flagClient4 = new Thread(new Runnable() {
        @Override
        public void run() {
            if (concreteBoard.flag(flagX4,flagY4))
                System.out.println("Client 4 flag successfully!");
            else
                System.out.println("Client 4 flag unsuccessfully!");

            System.out.println("Client 4 sees: \n" + concreteBoard.toString());
        }
    });
    static int flagX5 = 2;
    static int flagY5 = 2;
    private static Thread flagClient5 = new Thread(new Runnable() {
        @Override
        public void run() {
            if (concreteBoard.flag(flagX5,flagY5))
                System.out.println("Client 5 flag successfully!");
            else
                System.out.println("Client 5 flag unsuccessfully!");

            System.out.println("Client 5 sees: \n" + concreteBoard.toString());
        }
    });

    static int digX1 = 2;
    static int digY1 = 2;
    private static Thread digClient1 = new Thread(new Runnable() {
        @Override
        public void run() {
            if (!concreteBoard.dig(digX1,digY1))
                System.out.println("Client 1 dig and got bombed!");
            else
                System.out.println("Client 1 dig and didn't got bombed!");

            System.out.println("Client 1 sees: \n" + concreteBoard.toString());
        }
    });
    static int digX2 = 2;
    static int digY2 = 2;
    private static Thread digClient2 = new Thread(new Runnable() {
        @Override
        public void run() {
            if (!concreteBoard.dig(digX2,digY2))
                System.out.println("Client 2 dig and got bombed!");
            else
                System.out.println("Client 2 dig and didn't got bombed!");

            System.out.println("Client 2 sees: \n" + concreteBoard.toString());
        }
    });
    static int digX3 = 2;
    static int digY3 = 2;
    private static Thread digClient3 = new Thread(new Runnable() {
        @Override
        public void run() {
            if (!concreteBoard.dig(digX3,digY3))
                System.out.println("Client 3 dig and got bombed!");
            else
                System.out.println("Client 3 dig and didn't got bombed!");

            System.out.println("Client 3 sees: \n" + concreteBoard.toString());
        }
    });
    static int digX4 = 2;
    static int digY4 = 2;
    private static Thread digClient4 = new Thread(new Runnable() {
        @Override
        public void run() {
            if (!concreteBoard.dig(digX4,digY4))
                System.out.println("Client 4 dig and got bombed!");
            else
                System.out.println("Client 4 dig and didn't got bombed!");

            System.out.println("Client 4 sees: \n" + concreteBoard.toString());
        }
    });

    static int unflagX1 = 2;
    static int unflagY1 = 2;
    private static Thread unflagClient1 = new Thread(new Runnable() {
        @Override
        public void run() {
            if (concreteBoard.unflag(unflagX1,unflagY1))
                System.out.println("Client 1 unflag successfully!");
            else
                System.out.println("Client 1 unflag unsuccessfully!");

            System.out.println("Client 1 sees: \n" + concreteBoard.toString());
        }
    });
    static int unflagX2 = 2;
    static int unflagY2 = 2;
    private static Thread unflagClient2 = new Thread(new Runnable() {
        @Override
        public void run() {
            if (concreteBoard.unflag(unflagX2,unflagY2))
                System.out.println("Client 2 unflag successfully!");
            else
                System.out.println("Client 2 unflag unsuccessfully!");

            System.out.println("Client 2 sees: \n" + concreteBoard.toString());
        }
    });
    static int unflagX3 = 2;
    static int unflagY3 = 2;
    private static Thread unflagClient3 = new Thread(new Runnable() {
        @Override
        public void run() {
            if (concreteBoard.unflag(unflagX3,unflagX3))
                System.out.println("Client 3 unflag successfully!");
            else
                System.out.println("Client 3 unflag unsuccessfully!");

            System.out.println("Client 3 sees: \n" + concreteBoard.toString());
        }
    });
    static int unflagX4 = 2;
    static int unflagY4 = 2;
    private static Thread unflagClient4 = new Thread(new Runnable() {
        @Override
        public void run() {
            if (concreteBoard.unflag(unflagX4,unflagY4))
                System.out.println("Client 4 unflag successfully!");
            else
                System.out.println("Client 4 unflag unsuccessfully!");

            System.out.println("Client 4 sees: \n" + concreteBoard.toString());
        }
    });



    @Test
    void testMultiThreadFLAG() throws Exception{
        List<Thread> clients = new ArrayList<>();
        System.out.println("End game: \n" + concreteBoard.endGame());

        /* flag -> flag on same square
            + If square is UNTOUCHED, both flag successfully
            + If square is DUG, both flag unsuccessfully
         */

        //concreteBoard.dig(2, 2); // optional
        flagClient1.start();
        flagClient2.start();

        flagClient1.join();
        flagClient2.join();

        /* flag -> dig on same square
            + If square is UNTOUCHED and has bomb, flag successfully and dig as normal
            + If square is UNTOUCHED and has no bomb, flag successfully and dig as normal
            + If square is DUG, flag unsuccessfully and dig as normal
         */

        flagX3 = 1;
        flagY3 = 4;
        flagClient3.start();
        digX1 = 1;
        digY1 = 4;
        digClient1.start();

        flagClient3.join();
        digClient1.join();

        concreteBoard.dig(3,3);
        flagX4 = 3;
        flagY4 = 3;
        flagClient4.start();
        digX2 = 3;
        digY2 = 3;
        digClient2.start();

        flagClient4.join();
        digClient2.join();

        /* flag -> unflag on same square
            + if square is UNTOUCHED, both flag and unflag successfully
            + if square id DUG, both flag and unflag unsuccessfully
         */
        concreteBoard.dig(1,1);
        flagX5 = 1;
        flagY5 = 1;
        flagClient5.start();
        unflagX1 = 1;
        unflagY1 = 1;
        unflagClient1.start();

        flagClient5.join();
        unflagClient1.join();
    }

    @Test
    void testMultiThreadDIG() throws Exception{
        System.out.println("End game: \n" + concreteBoard.endGame());

        /* dig -> dig on same square
            + If square is UNTOUCHED, first dig as normal, later dig does nothing
            + If square is DUG, both digs do nothing
            (try enough times to get a bomb at (1,1))
         */
        digX1 = 1;
        digY1 = 1;
        digClient1.start();
        digX2 = 1;
        digY2 = 1;
        digClient2.start();

        digClient1.join();
        digClient2.join();

        /* dig -> flag/unflag on same square
            + If square is UNTOUCHED, first dig as normal, has bomb or not flag/unflag unsuccessfully
            + If square is DUG, dig do nothing and flag/unflag unsuccessfully
         */

        digX3 = 2;
        digY3 = 2;
        digClient3.start();
        flagX1 = 2;
        flagY1 = 2;
        flagClient1.start();

        digClient3.join();
        flagClient1.join();

        concreteBoard.dig(3, 3);
        digX4 = 3;
        digY4 = 3;
        digClient4.start();
        unflagX1 = 3;
        unflagY1 = 3;
        unflagClient1.start();

        digClient4.join();
        unflagClient1.join();
    }

    @Test
    void testMultiThreadUNFLAG() throws Exception{
        System.out.println("End game: \n" + concreteBoard.endGame());

        /* unflag -> unflag on same square
            + If square is UNTOUCHED, both unflag unsuccessfully
            + If square is DUG, both unflag unsuccessfully
         */

        unflagX1 = 1;
        unflagY1 = 1;
        unflagClient1.start();
        unflagX2 = 1;
        unflagY2 = 1;
        unflagClient2.start();

        unflagClient1.join();
        unflagClient2.join();

        /* unflag -> flag on same square
            + If square is UNTOUCHED, unflag unsuccessfully and flag successfully
            + If square is DUG, both unflag and flag unsuccessfully
         */

        concreteBoard.dig(2,2);
        unflagX3 = 2;
        unflagY3 = 2;
        unflagClient3.start();
        flagX1 = 2;
        flagY1 = 2;
        flagClient1.start();

        unflagClient3.join();
        flagClient1.join();

        /* unflag -> dig on same square
            + If square is UNTOUCHED, unflag unsuccessfully and dig as normal
            + If square is DUG, unflag unsuccessfully and dig does nothing
         */

        concreteBoard.dig(3,3);
        unflagX4 = 3;
        unflagY4 = 3;
        unflagClient4.start();
        digX1 = 3;
        digY1 = 3;
        digClient1.start();

        unflagClient4.join();
        digClient1.join();
    }
}