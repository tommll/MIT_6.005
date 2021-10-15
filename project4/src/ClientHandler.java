import game.Board;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable{

    // a singleton list to hold all the clientHandler records
    private static List<ClientHandler> clientHandlers = new ArrayList<>();

    // socket, reader, writer to handle communication between client and server
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    // client name
    private String clientUserName;

    // Game data
    private static int width = 5;
    private static int height = 5;
    private static final Board board = Board.newBoard(width, height);
    private boolean isLost = false;

    /**
     * Establish communication between client and server
     *
     * @param socket the socket which is connected to client
     */
    public ClientHandler(Socket socket){
        try{
            // set up the server-side of communication with client
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // read the user name from when client log in
            this.clientUserName = bufferedReader.readLine();

            bufferedWriter.write("Welcome to MineSweeper Board: " + 10 + " columns by " + 10 + " rows.\n"
                    + (1 + clientHandlers.size()) + " players including you. Type 'help' for help.");
            bufferedWriter.newLine();
            bufferedWriter.flush();

            // now add the new client to global pool of clients
            clientHandlers.add(this);

            // broadcast the info "new client has join" to other clients
            broadCastMessage("SERVER: " + clientUserName + " has joined the game");

        }catch(IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    /**
     * The actions which this server thread will be
     * doing while the connection is still alive
     */
    @Override
    public void run() {

        String messageFromClient;

        while (socket.isConnected()){
            try {
                // 1. Server will be listening for new messages
                // coming from this client
                messageFromClient = bufferedReader.readLine();

                String output = handleRequest(messageFromClient);

                System.out.println("Client input: " + messageFromClient);

                if (output != null) {
                    sendMessage(output);

                    if (output.length() >= width * height)
                        broadCastMessage(output);
                }

            }catch(IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }


    }

    /**
     * Handler for client input, performing requested operations and returning an output message.
     *
     * @param input message from client
     * @return message to client, or null if none
     */
    private String handleRequest(String input) {
        String regex = "(look)|(help)|(bye)|"
                + "(dig -?\\d+ -?\\d+)|(flag -?\\d+ -?\\d+)|(deflag -?\\d+ -?\\d+)";
        if ( ! input.matches(regex)) {
            // invalid input
            // TODO Problem 5

            return "Invalid input";
        }
        String[] tokens = input.split(" ");
        if (tokens[0].equals("look")) {
            // 'look' request
            // TODO Problem 5

            return board.toString();
        } else if (tokens[0].equals("help")) {
            // 'help' request
            // TODO Problem 5

            return "This is a help message.";
        } else if (tokens[0].equals("bye")) {
            // 'bye' request
            // TODO Problem 5

            closeEverything(socket, bufferedReader, bufferedWriter);
            return "GOODBYE!";
        } else {
            if (isLost){
                return "[SPECTATOR MODE] Here's the current state of the game\n" + board.toString();
            }

            int x = Integer.parseInt(tokens[1]);
            int y = Integer.parseInt(tokens[2]);
            if (tokens[0].equals("dig")) {
                // 'dig x y' request
                // TODO Problem 5

                if (!board.dig(x, y)) {
                    isLost = true;
                    return "YOU LOST!";
                }
                return board.toString();
            } else if (tokens[0].equals("flag")) {
                // 'flag x y' request
                // TODO Problem 5

                board.flag(x, y);
                return board.toString();
            } else if (tokens[0].equals("deflag")) {
                // 'deflag x y' request
                // TODO Problem 5

                board.unflag(x, y);
                return board.toString();
            }
        }
        // TODO: Should never get here, make sure to return in each of the cases above
        throw new UnsupportedOperationException();
    }

    /**
     * Send the message to every other client currently active in the chat room
     *
     * @param messageToSend
     */
    public void broadCastMessage(String messageToSend){
        for (ClientHandler x : clientHandlers){
            try {
                if (!x.clientUserName.equals(clientUserName)){
                    // Write the message to current client's database
                    //   push message to client writer
                    x.bufferedWriter.write(messageToSend);
                    //   push newLine character to indicate end
                    x.bufferedWriter.newLine();
                    //   since the buffer might not be full, flush to send message immediately
                    x.bufferedWriter.flush();
                }
            }catch(IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    /**
     * Send the message to client
     *
     * @param messageToSend
     */
    public void sendMessage(String messageToSend){
        try {
            {
                // Write the message to current client's database
                //   push message to client writer
                bufferedWriter.write(messageToSend);
                //   push newLine character to indicate end
                bufferedWriter.newLine();
                //   since the buffer might not be full, flush to send message immediately
                bufferedWriter.flush();
            }
        }catch(IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    /**
     *  Remove clientHandler for the user who has left the game
     */
    public void removeClientHandler(){
        clientHandlers.remove(this);
        broadCastMessage("SERVER: " + clientUserName + " has left the game!");
    }

    /** Terminate connection
     *
     * @param socket current socket in connection
     * @param bufferedReader
     * @param bufferedWriter
     */
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        removeClientHandler();

        try{
            if (bufferedReader != null){
                bufferedReader.close();
            }

            if (bufferedWriter != null){
                bufferedWriter.close();
            }

            if (socket != null){
                socket.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

}

