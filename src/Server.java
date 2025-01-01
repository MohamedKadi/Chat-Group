import java.io.*;
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
    }

    public void closeServer(){
        try{
            server.close();
            socket.close();

        }catch(IOException e){
            e.printStackTrace();
        }
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
            close();
        }
    }
    public void sendMsg(String msg){
        try{
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bw.write(msg);
            bw.flush();
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            close();
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
