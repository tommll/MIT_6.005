import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String userName;

    /**
     * Establish communication between client and server
     *
     * @param socket
     * @param userName
     */
    public Client(Socket socket, String userName){
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.userName = userName;
        }catch(IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    /**
     * Listen for user input from console and send it to the server
     */
    public void sendMessage(){
        try{
            // user log in first by enter a user name
            bufferedWriter.write(userName);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner sc = new Scanner(System.in);

            // keep listening, read message from console and send "userName" + "message" to server
            while (socket.isConnected()){
                String messageToSend = sc.nextLine();
                bufferedWriter.write(messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }catch(IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    /** Listen for new messages from server
     * We do this by creating a separated thread for
     * the purpose of listening for server messages
     */
    public void listenForMessage(){
        new Thread((new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;

                while (socket.isConnected()){
                    try{
                        msgFromGroupChat = bufferedReader.readLine();
                        System.out.println(msgFromGroupChat);
                    }catch(IOException e){
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        })).start();
    }

    /** Terminate connection
     *
     * @param socket current socket in connection
     * @param bufferedReader
     * @param bufferedWriter
     */
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
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

    public static void main(String[] args) throws IOException {
        // on client machine, ask user to type in user name
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your user name for the game: ");
        String userName = sc.nextLine();

        // create a socket to connect to port 1234 of server machine- which is localhost
        Socket socket = new Socket("localhost", 1234);

        // Create new client object
        Client client = new Client(socket, userName);
        // Needs 2 threads:
        //   1. For sending messages - main thread
        //   2. For receiving new messages - 2nd thread
        client.listenForMessage();
        client.sendMessage();
    }
}
