package nexlink.server.networking;
/**
 *
 * @author ALI
 */
import java.net.Socket;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;


 public class ClientHandler implements Runnable {
     
     Socket socket;
     
     public ClientHandler(Socket socket){
     this.socket= socket;
     }
     
     @Override
     public void run(){
        try{
        BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter bufferedWriter= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        
        while(true){
        String messageFromClient= bufferedReader.readLine();
        
        if(messageFromClient.equalsIgnoreCase("bye")){
            break;
        }
            System.out.println(messageFromClient);
            
            bufferedWriter.write("Sever: Message recived");
            bufferedWriter.newLine();
            bufferedWriter.flush();
            
        
        }
        
        }catch(IOException e){
        e.printStackTrace();        
        }

         
     }
     
    
     
}
