package nexlink.client.networking;

import java.io.BufferedReader;
import java.io.IOException;
import nexlink.client.gui.ChatWindow;

/**
 *
 * @author ALI
 */
public class ClientListener implements Runnable {

    BufferedReader bufferedReader;
    private ChatWindow chatWindow;

    public ClientListener(BufferedReader bufferedReader, ChatWindow window) {
        this.bufferedReader = bufferedReader;
        this.chatWindow = window;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String response = bufferedReader.readLine();
                if (response == null) {
                    break; // Stream ended gracefully
                }

                // ---------------- PROTOCOL 1: Live User List Sync ----------------
                if (response.startsWith("USER_LIST|")) {
                    String data = response.substring(10); // Strip out "USER_LIST|"
                    String[] displayStrings = data.split(",");

                    java.awt.EventQueue.invokeLater(() -> {
                        javax.swing.DefaultListModel<String> model = new javax.swing.DefaultListModel<>();
                        for (String item : displayStrings) {
                            if (!item.trim().isEmpty()) {
                                model.addElement(item.trim());
                            }
                        }
                        chatWindow.getUserList().setModel(model);
                    });
                    continue;
                }

                // ---------------- PROTOCOL 2: Load Chat History ----------------
                if (response.startsWith("LOAD_HISTORY|")) {
                    String[] parts = response.split("\\|", 2);
                    
                    if (parts.length == 2) {
                        String historyData = parts[1].trim();

                        java.awt.EventQueue.invokeLater(() -> {
                            javax.swing.JTextPane txtArea = chatWindow.getMessagesTextArea();
                            if (txtArea != null) {
                                if (historyData.equals("EMPTY")) {
                                    System.out.println("ℹ️ ClientListener: No history found, clearing view.");
                                    txtArea.setText(""); // Wipe old conversation tracks clean
                                } else {
                                    // Split back out using our escape safe string array token
                                    String[] individualMessages = historyData.split(java.util.regex.Pattern.quote("[MSG_SEP]"));
                                    
                                    StringBuilder sb = new StringBuilder();
                                    for (String msg : individualMessages) {
                                        sb.append(msg).append("\n");
                                    }
                                    
                                    txtArea.setText(sb.toString().trim()); // Render the database history block
                                    txtArea.setCaretPosition(txtArea.getDocument().getLength());
                                    System.out.println("🎯 ClientListener: Rendered " + individualMessages.length + " database messages.");
                                }
                            }
                        });
                    }
                    continue;
                }

                // ---------------- PROTOCOL 3: Live Incoming Messages ----------------
                if (response.startsWith("INCOMING_MSG|")) {
                    String[] tokens = response.split("\\|", 3);

                    if (tokens.length == 3) {
                        String sender = tokens[1];
                        String text = tokens[2];

                        java.awt.EventQueue.invokeLater(() -> {
                            javax.swing.JTextPane txtArea = chatWindow.getMessagesTextArea();
                            if (txtArea != null) {
                                String currentText = txtArea.getText().trim();
                                if (currentText.isEmpty()) {
                                    txtArea.setText(sender + ": " + text);
                                } else {
                                    txtArea.setText(currentText + "\n" + sender + ": " + text);
                                }
                                txtArea.setCaretPosition(txtArea.getDocument().getLength());
                            }
                        });
                    }
                    continue;
                }
            }
        } catch (IOException e) {
            System.out.println("Connection closed.");
        }
    }
}