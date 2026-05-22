package nexlink.client.networking;

import java.io.BufferedReader;
import java.io.IOException;

/**
 *
 * @author ALI
 */
public class ClientListener implements Runnable {

    BufferedReader bufferedReader;
    // Change Socket to BufferedReader:
    ClientListener(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    @Override
    public void run() {
        try {
            
            while (true) {
                String incomingMessage = bufferedReader.readLine();
                if (incomingMessage == null) {
                    System.out.println("Server disconnected.");
                    break;
                }
                System.out.println("\n" + incomingMessage);
            }
        } catch (IOException e) {
            System.out.println("Connection closed.");
        }
    }
}
