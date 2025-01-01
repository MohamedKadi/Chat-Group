import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    ServerSocket server;
    List<ClientHandler> clients = new CopyOnWriteArrayList<ClientHandler>();
    Socket socket;

    public Server(int port){
        try{
            //creating a server
            server =new ServerSocket();
            //listening to any clients that connects
            socket = server.accept();
            System.out.println("new client connected");


        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

class ClientHandler{

}
