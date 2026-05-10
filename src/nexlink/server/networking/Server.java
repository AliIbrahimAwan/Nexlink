package nexlink.server.networking;
/**
 *
 * @author ALI
 */
import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;


public class Server {
    public static void main(String[] args){
        
        ServerSocket serverSocket= null;
        
        try{
            serverSocket= new ServerSocket(1919);
            }catch(IOException e){
                e.printStackTrace();
                return;
            }
        
        
        while(true){
            try{
                Socket socket= serverSocket.accept();
                
                System.out.println("The client has connected");
                
                ClientHandler clientHandler= new ClientHandler(socket);
                Thread thread= new Thread(clientHandler);
                thread.start();
                
                
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        
        
        
    }
            
}
