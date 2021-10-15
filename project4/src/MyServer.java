import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MyServer {
    /*

     Rep-invariant:
        serverSocket can't be null
     */
    private ServerSocket serverSocket;

    public MyServer(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    public void startServer(){
        try{
            while (!serverSocket.isClosed()){
                // blocking method until a client connect
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected");

                // pass the socket created for the new client to a ClientHandler - implement Runnable
                ClientHandler clientHandler = new ClientHandler(socket);

                // Pass the Runnable object to a new thread
                Thread thread = new Thread(clientHandler);
                thread.start();// start thread

            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     *  Close down current Server
     */
    public void closeServerSocket(){
        try{
            if (serverSocket != null){
                serverSocket.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        // the server will be listening on port 1234
        ServerSocket serverSocket = new ServerSocket(1234);
        MyServer server = new MyServer(serverSocket);
        server.startServer();

    }
}

