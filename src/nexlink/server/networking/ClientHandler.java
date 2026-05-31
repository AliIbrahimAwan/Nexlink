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
import nexlink.server.db.FileDAO;
import nexlink.server.gui.ServerConsole;
import nexlink.server.models.FileModel;

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

                            
                            ServerConsole.log("AUTH", "User '" + senderName + "' authenticated. Network tunnel open.");
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
                ServerConsole.log("DISCONNECT", "Client [" + this.senderName + "] signed out cleanly.");
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
                    ServerConsole.log("DATABASE", "Fetching history between [" + this.senderName + "] and [" + targetReceiver + "]");

                    List<String> chatHistory = messagesDAO.getChatHistory(this.senderName, targetReceiver);

                    if (chatHistory == null || chatHistory.isEmpty()) {
                        bufferedWriter.write("LOAD_HISTORY|EMPTY");
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                        ServerConsole.log("DATABASE", "No previous history found. Sent EMPTY signal.");
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

                        ServerConsole.log("SUCCESS", "Sent " + chatHistory.size() + " historical messages to [" + this.senderName + "]");
                    }
                }
                continue; // Pass smoothly to the next loop cycle
            }

            // ---------------- PROTOCOL 3: Targeted Chat Messaging ----------------
            if (incomingRequest.startsWith("MSG|")) {
                String[] tokens = incomingRequest.split("\\|", 3);

                if (tokens.length == 3) {
                    String targetReceiver = tokens[1];
                    String textMessage = tokens[2];

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

            // ---------------- PROTOCOL 4: File Streaming ----------------
            if (incomingRequest.startsWith("FILE_START|")) {
                String[] tokens = incomingRequest.split("\\|", 4);

                if (tokens.length == 4) {
                    String targetReceiver = tokens[1];
                    String fileName = tokens[2];
                    long fileSize = Long.parseLong(tokens[3]);

                    ServerConsole.log("ROUTING", "File payload: [" + this.senderName + "] ➔ [" + targetReceiver + "] (" + fileName + " | " + fileSize + " bytes)");
                    ClientHandler recipientHandler = Server.activeClients.get(targetReceiver);

                    if (recipientHandler != null) {
                        forwardFileStream(recipientHandler, fileName, fileSize);
                        FileModel newFile = new FileModel(fileName, senderName, targetReceiver);
                        new FileDAO().logFileTransfer(newFile);
                    } else {
                        ServerConsole.log("WARN", "Target user offline. Draining data stream safely.");
                        drainStream(fileSize);
                        this.receiveForwardedMessage("INCOMING_MSG|System|" + targetReceiver + " is offline. File transfer cancelled.");
                    }
                }
                continue;
            }
        }
    } catch (java.net.SocketException e) {
        // 🛑 THIS CATCHES THE ERROR: Client closed window or killed program abruptly
        ServerConsole.log("OFFLINE", "Client [" + (this.senderName != null ? this.senderName : "Unknown") + "] disconnected abruptly.");
    } catch (java.io.IOException e) {
        // Catches regular stream dropouts
        ServerConsole.log("ERROR", "Network stream error on [" + this.senderName + "]: " + e.getMessage());
    } finally {
        // 🧹 ALWAYS executes to clean up memory and lists
        cleanUpSessionResources();
    }
}

// Helper method added to handle the cleanup seamlessly
private void cleanUpSessionResources() {
    try {
        // 1. Remove from active server routing maps so messages aren't sent to a dead socket
        if (this.senderName != null) {
            Server.activeClients.remove(this.senderName);
            ServerConsole.log("DISCONNECT", "Removed [" + this.senderName + "] session token from Active memory registry.");
            
            // Optional: broadcast updated user list to everyone remaining online
            // sendUpdatedListToAllOnlineUsers();
        }
        

    } catch (Exception ex) {
        // Silent block for terminal close anomalies
    }
         
         finally {
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

    private void forwardFileStream(ClientHandler recipient, String fileName, long fileSize) {
        try {
            // Step 1: Notify the recipient over their normal text stream
            recipient.bufferedWriter.write("FILE_INCOMING|" + this.senderName + "|" + fileName + "|" + fileSize);
            recipient.bufferedWriter.newLine();
            recipient.bufferedWriter.flush();

            // Step 2: Access raw streams to route binary chunks directly
            java.io.InputStream rawInput = this.socket.getInputStream();
            java.io.OutputStream rawOutput = recipient.socket.getOutputStream();

            byte[] buffer = new byte[4096];
            long totalBytesRemaining = fileSize;
            int bytesRead;

            while (totalBytesRemaining > 0
                    && (bytesRead = rawInput.read(buffer, 0, (int) Math.min(buffer.length, totalBytesRemaining))) != -1) {

                rawOutput.write(buffer, 0, bytesRead);
                totalBytesRemaining -= bytesRead;
            }

            rawOutput.flush();
            System.out.println("✅ Server successfully forwarded: " + fileName);

        } catch (java.io.IOException e) {
            System.out.println("❌ Server routing error: " + e.getMessage());
        }
    }

    private void drainStream(long bytesToDiscard) {
        try {
            java.io.InputStream rawInput = this.socket.getInputStream();
            byte[] buffer = new byte[4096];
            long remaining = bytesToDiscard;
            int read;

            while (remaining > 0 && (read = rawInput.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {
                remaining -= read;
            }
        } catch (java.io.IOException e) {
            System.out.println("Error discarding broken pipeline bytes: " + e.getMessage());
        }
    }

}
