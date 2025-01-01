import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    ServerSocket server;
    List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    Socket socket;

    public Server(int port){
        try{
            //creating a server
            server =new ServerSocket(port);
            System.out.println("Server is listening on port "+port);
            //listening to any clients that connects
            while(true){
                socket = server.accept();
                System.out.println("new client connected");

                ClientHandler client = new ClientHandler(socket, this);
                clients.add(client);
                new Thread(client).start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            closeServer();
        }
    }
    public void BroadcastMsg(String msg,ClientHandler client){
        for(ClientHandler c : clients){
            if(c != client){
                c.sendMsg(msg);
            }
        }
    }

    public void removeClient(ClientHandler c){
        clients.remove(c);
        System.out.println("a client disconnected");
    }

    public void closeServer(){
        try{
            server.close();
            socket.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server(5000);
    }
}

class ClientHandler implements Runnable{
    Server server;
    Socket socket;
    BufferedWriter bw;
    BufferedReader br;
    public ClientHandler(Socket socket, Server server){
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void run() {
        try{
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String msg;
            while((msg = br.readLine()) != null){
                System.out.println("message that the server got: "+msg);
                server.BroadcastMsg(msg,this);
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            this.close();
        }
    }
    public void sendMsg(String msg){
        try{
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bw.write(msg);
            bw.newLine();
            bw.flush();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void close() {
        try{
            if(socket != null) socket.close();
            if(br != null)  br.close();
            if(bw != null) bw.close();
            server.removeClient(this);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
