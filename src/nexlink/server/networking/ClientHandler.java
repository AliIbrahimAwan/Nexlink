
package nexlink.server.networking;

import nexlink.server.db.MessagesDAO;
import nexlink.server.models.Message;
import java.net.Socket;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class ClientHandler implements Runnable {

    Socket socket;
    String senderName;
    String receiverName;
    
    // 1. Elevate this to an instance variable so other threads can use it to route text here
    BufferedWriter bufferedWriter; 

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    // 2. Add a helper method that allows external ClientHandlers to drop data down this stream
    public void receiveForwardedMessage(String messageText) {
        try {
            if (bufferedWriter != null) {
                bufferedWriter.write(messageText);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
            
            // Initialized here using our instance variable field
            this.bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream()));

            // Step 1 — get sender name
            senderName = bufferedReader.readLine();
            System.out.println(senderName + " connected!");
            
            // REGISTRY GATE: Log this live connection into the Server's map immediately
            Server.activeClients.put(senderName, this);

            // Step 2 — get receiver name
            receiverName = bufferedReader.readLine();
            System.out.println(senderName + " wants to chat with " + receiverName);

            // Step 3 — message loop
            while (true) {
                String messageText = bufferedReader.readLine();

                if (messageText == null || messageText.equalsIgnoreCase("bye")) {
                    System.out.println(senderName + " disconnected.");
                    break;
                }

                System.out.println(senderName + " → " + receiverName + ": " + messageText);

                // Step 4 — create Message object
                Message msg = new Message(senderName, receiverName, messageText);

                // Step 5 — save to DB
                MessagesDAO messageDAO = new MessagesDAO();
                messageDAO.saveMessage(msg);

                // Step 6 — TARGETED ROUTING LOGIC
                // Look up if the targeted receiver is currently online in the Server map
                ClientHandler targetHandler = Server.activeClients.get(receiverName);
                
                if (targetHandler != null) {
                    // Send the message directly to the recipient's thread terminal stream!
                    targetHandler.receiveForwardedMessage(senderName + ": " + messageText);
                } else {
                    // If they are not in the Map, they are offline. Notify the sender.
                    receiveForwardedMessage("System: " + receiverName + " is offline. Message saved to DB.");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (senderName != null) {
                Server.activeClients.remove(senderName);
            }
            try {
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}