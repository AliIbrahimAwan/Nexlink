package nexlink.server.networking;

import nexlink.server.db.MessagesDAO;
import nexlink.server.models.Message;
import nexlink.server.models.User;
import nexlink.server.db.UserDAO;

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
    BufferedWriter bufferedWriter;
    User user=null;
    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

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

            this.bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()));

            UserDAO userDAO = new UserDAO();
            while(true){
            // Step 1 — get option from client
            String option = bufferedReader.readLine();
            int optionInt = Integer.parseInt(option);

            if (optionInt == 1) {
                // LOGIN LOOP
                while (true) {
                    senderName = bufferedReader.readLine();
                    String senderPassword = bufferedReader.readLine();

                    user = userDAO.loginUser(senderName, senderPassword);

                    if (user != null) {
                        bufferedWriter.write("11");
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                        break;
                    } else {
                        bufferedWriter.write("incorrect password");
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }
                }
                if(user != null){
                break;
                }
            } else if (optionInt == 2) {
                // REGISTER
                senderName = bufferedReader.readLine();
                String senderPassword = bufferedReader.readLine();

                boolean success = userDAO.registerUser(senderName, senderPassword);

                if (success) {
                    String sucessString= "1";
                    bufferedWriter.write(sucessString);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                } else {
                    bufferedWriter.write("username taken");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    socket.close();
                    return;
                }
                }
            if(optionInt==2){
            continue;
            }
            }

            // Step 2 — register client in active map
            System.out.println(senderName + " connected!");
            Server.activeClients.put(senderName, this);

            // Step 3 — get receiver name
            receiverName = bufferedReader.readLine();
            System.out.println(senderName + " wants to chat with " + receiverName);

            // Step 4 — check receiver exists
            if (!userDAO.userExists(receiverName)) {
                receiveForwardedMessage("System: User " + receiverName + " does not exist.");
                socket.close();
                return;
            }

            // Step 5 — message loop
            MessagesDAO messageDAO = new MessagesDAO();
            while (true) {
                String messageText = bufferedReader.readLine();

                if (messageText == null || messageText.equalsIgnoreCase("bye")) {
                    System.out.println(senderName + " disconnected.");
                    break;
                }

                System.out.println(senderName + " → " + receiverName + ": " + messageText);

                // save to DB
                Message msg = new Message(senderName, receiverName, messageText);
                messageDAO.saveMessage(msg);

                // route to receiver
                ClientHandler targetHandler = Server.activeClients.get(receiverName);
                if (targetHandler != null) {
                    targetHandler.receiveForwardedMessage(senderName + ": " + messageText);
                } else {
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