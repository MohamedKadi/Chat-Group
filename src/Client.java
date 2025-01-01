import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable{
    Socket socket;
    BufferedReader br;
    BufferedReader brserver;
    BufferedWriter bw;
    public Client(String url, int port){
        try{
            socket = new Socket(url,port);
            System.out.println("send a message to the group");
            br = new BufferedReader(new InputStreamReader(System.in));
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            new Thread(this).start();
            String msg="";
            while(true){
                while((msg = br.readLine()) != null){
                    if(msg.equals("quit")){
                        System.out.println("quit");
                        break;
                    }
                    bw.write(msg);
                    bw.newLine();
                    bw.flush();
                }
                break;
            }
        }catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            this.close();

        }
    }

    public void listenToServer(){
        try{
            brserver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String msg="";
            while(!socket.isClosed() && (msg=brserver.readLine())!=null){
                System.out.println("some client said: "+ msg);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            this.close();
        }
    }

    public void close(){
        try{
            if(br != null) br.close();
            if(bw != null) bw.close();
            if(brserver != null) brserver.close();
            if(socket != null) socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        listenToServer();
    }

    public static void main(String[] args) {
        Client c1 = new Client("127.0.0.1",5000);
    }
}
