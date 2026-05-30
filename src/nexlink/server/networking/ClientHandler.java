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
import java.util.List;

public class ClientHandler implements Runnable {

    Socket socket;
    String senderName;
    BufferedWriter bufferedWriter;
    User user = null;
    UserDAO userDAO = null;

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

            userDAO = new UserDAO();
            int currentOption = 1;

            // AUTH LOOP
            while (true) {

                if (currentOption == 1) {
                    // LOGIN
                    while (true) {
                        String input = bufferedReader.readLine();
                        if (input == null) {
                            return;
                        }

                        if (input.equals("SWITCH_TO_REGISTER")) {
                            currentOption = 2;
                            break;
                        }

                        // login attempt
                        senderName = input;

                        String senderPassword = bufferedReader.readLine();

                        user = userDAO.loginUser(senderName, senderPassword);

                        if (user != null) {
                            bufferedWriter.write("11");
                            bufferedWriter.newLine();
                            bufferedWriter.flush();

                            Server.activeClients.put(senderName, this);

                            System.out.println(senderName + " has officially logged in.");
                            this.sendLiveUserList();
                            Server.broadcastUserListUpdate();

                            break;
                        } else {
                            bufferedWriter.write("incorrect password");
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                        }
                    }

                    if (user != null) {
                        break; // login successful — exit auth loop
                    }
                } // FIXED: Using else if so option switching resets the master loop step cleanly
                else if (currentOption == 2) {
                    // REGISTER
                    while (true) {
                        String regStatus = bufferedReader.readLine();
                        if (regStatus == null) {
                            return;
                        }

                        if (regStatus.equals("START_REGISTRATION")) {
                            senderName = bufferedReader.readLine();
                            String senderPassword = bufferedReader.readLine();

                            boolean success = userDAO.registerUser(senderName, senderPassword);

                            if (success) {
                                bufferedWriter.write("1");
                                bufferedWriter.newLine();
                                bufferedWriter.flush();
                                currentOption = 1; // go back to login
                                break;
                            } else {
                                bufferedWriter.write("username taken");
                                bufferedWriter.newLine();
                                bufferedWriter.flush();
                            }
                        } // Catches menu synchronization reset tokens
                        else if (regStatus.equals("1")) {
                            currentOption = 1;
                            break;
                        }
                    }
                }
            }

            // CHAT PHASE
            while (true) {
                MessagesDAO messagesDAO = new MessagesDAO();

                // 💡 Read from the socket ONLY ONCE at the start of the loop
                String incomingRequest = bufferedReader.readLine();

                // If the client cleanly disconnected or dropped, exit the loop
                if (incomingRequest == null) {
                    break;
                }

                // ---------------- PROTOCOL 1: User List Request ----------------
                if (incomingRequest.equals("REQ_USER_LIST")) {
                    sendLiveUserList();
                    continue;
                }

                // ---------------- PROTOCOL 2: Chat History Request ----------------
                if (incomingRequest.startsWith("REQ_HISTORY|")) {
                    String[] tokens = incomingRequest.split("\\|", 2);

                    if (tokens.length == 2) {
                        String targetReceiver = tokens[1];
                        System.out.println("⏳ Fetching history between [" + this.senderName + "] and [" + targetReceiver + "]");

                        List<String> chatHistory = messagesDAO.getChatHistory(this.senderName, targetReceiver);

                        if (chatHistory == null || chatHistory.isEmpty()) {
                            bufferedWriter.write("LOAD_HISTORY|EMPTY");
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                            System.out.println("ℹ️ No previous history found. Sent EMPTY signal.");
                        } else {
                            StringBuilder historyPacket = new StringBuilder("LOAD_HISTORY|");
                            for (int i = 0; i < chatHistory.size(); i++) {
                                historyPacket.append(chatHistory.get(i));
                                if (i < chatHistory.size() - 1) {
                                    historyPacket.append("[MSG_SEP]");
                                }
                            }
                            bufferedWriter.write(historyPacket.toString());
                            bufferedWriter.newLine();
                            bufferedWriter.flush();

                            System.out.println("✅ Sent " + chatHistory.size() + " historical messages to [" + this.senderName + "]");
                        }
                    }
                    continue; // Pass smoothly to the next loop cycle
                }

                // ---------------- PROTOCOL 3: Targeted Chat Messaging ----------------
                if (incomingRequest.startsWith("MSG|")) {
                    System.out.println("entered if 2");

                    String[] tokens = incomingRequest.split("\\|", 3);

                    if (tokens.length == 3) {
                        String targetReceiver = tokens[1];
                        String textMessage = tokens[2];

                        System.out.println(textMessage + " to " + targetReceiver);

                        ClientHandler recipientHandler = Server.activeClients.get(targetReceiver);
                        Message message = new Message(senderName, targetReceiver, textMessage);
                        messagesDAO.saveMessage(message);

                        if (recipientHandler != null) {
                            recipientHandler.receiveForwardedMessage("INCOMING_MSG|" + senderName + "|" + textMessage);
                        } else {
                            this.receiveForwardedMessage("INCOMING_MSG|System|" + targetReceiver + " is currently offline.");
                        }
                    }
                    continue;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (senderName != null) {
                Server.activeClients.remove(senderName);
            }
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendLiveUserList() {
        try {
            // 1. Get all usernames from the database once
            List<String> users = userDAO.getAllUsernames();
            StringBuilder sb = new StringBuilder("USER_LIST|");

            for (String name : users) {
                // Don't show the user themselves in their own sidebar list
                if (!name.equals(this.senderName)) {
                    // 2. Check the memory HashMap instead of the database for live status!
                    String status = Server.activeClients.containsKey(name) ? "(online)" : "(offline)";

                    // 3. Format: USER_LIST|Wardan (online),Zain (offline),
                    sb.append(name).append(" ").append(status).append(",");
                }
            }

            // Send the compiled string back over the stream
            receiveForwardedMessage(sb.toString());

        } catch (Exception e) {
            System.out.println("Error compiling user list: " + e.getMessage());
        }
    }
}
