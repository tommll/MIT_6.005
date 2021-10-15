package game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConcreteArrayBoardTest {

    private static ConcreteBoard board;
    private final int size = 5;
    void newBoard(){
        board = new ConcreteBoard(size, size);
    }

    @Test
    void expand() {
        newBoard();
        System.out.println(board.endGame());

        board.flag(1,1);
        System.out.println(board.getNumFlagged());
        System.out.println(board.getNumDug());
        //System.out.println(board.toString());


        for (int i = 1; i <= size; i++){
            for (int j = 1; j <= size; j++){
                if (!board.hasBomb(i, j)){
                    try {
                        board.expand(i, j);
                    }catch(Exception e) {   e.printStackTrace();  }


                }
            }
            System.out.println(board.toString());
        }



    }
}