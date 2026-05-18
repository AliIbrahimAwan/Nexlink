package nexlink.client.networking;

import java.net.Socket;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

/**
 *
 * @author ALI
 */
public class ClientListener implements Runnable {

    Socket socket = null;
    InputStreamReader inputStreamReader = null;
    BufferedReader bufferedReader = null;

    ClientListener(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                String incomingMessage = bufferedReader.readLine();
                
                if(socket ==null){
                    System.out.println("The server has been stopped.");
                    break;
                }
                System.out.println("\n" +incomingMessage);
            }
        }catch (IOException e) {
            System.out.println("Connection closed.");
        } finally {
            try {
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
