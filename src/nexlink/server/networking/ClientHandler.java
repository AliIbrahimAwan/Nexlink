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

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
            BufferedWriter bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream()));

            // Step 1 — get sender name
            senderName = bufferedReader.readLine();
            System.out.println(senderName + " connected!");

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

                // Step 6 — confirm to client
                bufferedWriter.write("Message delivered and saved!");
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}