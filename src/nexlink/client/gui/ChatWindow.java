/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package nexlink.client.gui;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.awt.color.*;

/**
 *
 * @author ALI
 */
public class ChatWindow extends javax.swing.JFrame {

    private void requestUserList() {
        try {
            // Send a custom string token to tell the server we need the user directory
            bufferedWriter.write("REQ_USER_LIST");
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (java.io.IOException e) {
            System.out.println("Error requesting user list: " + e.getMessage());
        }
    }

    private String currentLoggedInUser;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String selectedReceiver = null;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ChatWindow.class.getName());
    private java.io.File selectedFile = null;
    private java.net.Socket clientSocket;
    /**
     * Creates new form ChatWindow
     */
    public ChatWindow(String Username, java.net.Socket socket,BufferedWriter writer, BufferedReader reader) {

        this.currentLoggedInUser = Username;
        this.clientSocket = socket;
        this.bufferedWriter = writer;
        this.bufferedReader = reader;

        // 1. Let NetBeans build the initial layout structures first
        initComponents();

        // 2. Clear out NetBeans' broken card layout naming scheme
        msgPanel.removeAll();

        // 3. Re-add the panels with distinct, clean layout strings
msgPanel.add(chatCardPanel, "chat");
    msgPanel.add(filesCardPanel, "files");
    msgPanel.add(settingsCardPanel, "settings");
        // 4. Force the panel to update its internal rendering tree
        msgPanel.revalidate();
        msgPanel.repaint();

        // 5. Default starting view
java.awt.CardLayout cl = (java.awt.CardLayout) msgPanel.getLayout();
    cl.show(msgPanel, "chat");

        // ==========================================
        // 💎 PERFECT TRANSPARENCY SETTINGS
        // ==========================================
        // Main layouts background removal
        mainPanel.setOpaque(false);
        msgPanel.setOpaque(false);
        chatCardPanel.setOpaque(false);
        filesCardPanel.setOpaque(false);

        // CHAT VIEW: Strip JScrollPane, Viewport, and Text View layers
        rightPane.setOpaque(false);
        rightPane.getViewport().setOpaque(false);
        messagesTextArea.setOpaque(false);
        messagesTextArea.setForeground(Color.WHITE);
        messagesTextArea.setEditable(false);

        // FILES VIEW: Strip JScrollPane, Viewport, and Text View layers
        rightPaneFiles.setOpaque(false);
        rightPaneFiles.getViewport().setOpaque(false); // 🫧 Crucial missing step
        filesTextArea.setOpaque(false);
        filesTextArea.setForeground(Color.WHITE);

        // User list initialization and backend triggers
        userList.setForeground(Color.WHITE);
        userList.setCellRenderer(new StatusListRenderer());
        requestUserList();

        // Hook up the list selection listener
        userList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            @Override
            public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String selected = userList.getSelectedValue();
                    if (selected != null) {
                        String cleanReceiver = selected.split(" ")[0].trim();
                        selectedReceiver = cleanReceiver;

                        try {
                            messagesTextArea.setText("");
                            bufferedWriter.write("REQ_HISTORY|" + selectedReceiver);
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                        } catch (Exception ex) {
                            System.out.println("❌ Client error requesting chat history: " + ex.getMessage());
                            
                        }
                    }
                }
            }
        });
    }

    private String getFileChecksum(java.io.File file) {
    try {
        java.security.MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
        java.io.FileInputStream fis = new java.io.FileInputStream(file);
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;
        
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }
        fis.close();
        
        byte[] bytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    } catch (Exception e) {
        return "Checksum Error: " + e.getMessage();
    }
}
    
    public javax.swing.JList<String> getUserList() {
        return userList;
    }

    public javax.swing.JTextPane getMessagesTextArea() {
        return messagesTextArea;
    }

    public String getSelectedReceiver() {
        return selectedReceiver;
    }

   public void appendSystemNotification(String message) {
    java.awt.EventQueue.invokeLater(() -> {
        // 1. Log to the primary Chat View window
        if (messagesTextArea != null) {
            String current = messagesTextArea.getText().trim();
            messagesTextArea.setText(current.isEmpty() ? message : current + "\n" + message);
            messagesTextArea.setCaretPosition(messagesTextArea.getDocument().getLength());
        }
        
        // 2. Log to the Files Management tab view window 
        if (filesTextArea != null) {
            String current = filesTextArea.getText().trim();
            filesTextArea.setText(current.isEmpty() ? message : current + "\n" + message);
            filesTextArea.setCaretPosition(filesTextArea.getDocument().getLength());
        }
    });
} 
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        nexLinkLabel = new javax.swing.JLabel();
        mainPanel = new javax.swing.JPanel();
        leftPane = new javax.swing.JScrollPane();
        userList = new javax.swing.JList<>();
        msgPanel = new javax.swing.JPanel();
        chatCardPanel = new javax.swing.JPanel();
        sendButton = new javax.swing.JButton();
        msgToSndTextFeild = new java.awt.TextField();
        rightPane = new javax.swing.JScrollPane();
        messagesTextArea = new javax.swing.JTextPane();
        filesCardPanel = new javax.swing.JPanel();
        sendButton1 = new javax.swing.JButton();
        rightPaneFiles = new javax.swing.JScrollPane();
        filesTextArea = new javax.swing.JTextPane();
        chooseButton = new javax.swing.JButton();
        settingsCardPanel = new javax.swing.JPanel();
        mainPanel1 = new javax.swing.JPanel();
        btnShowChat = new java.awt.Button();
        btnShowFiles = new java.awt.Button();
        btnSettings = new java.awt.Button();
        background = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        jLabel2.setBackground(new java.awt.Color(102, 0, 255));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 204, 204));
        jLabel2.setText(currentLoggedInUser);

        nexLinkLabel.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        nexLinkLabel.setForeground(new java.awt.Color(0, 204, 204));
        nexLinkLabel.setText("NexLink");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        mainPanel.setBackground(new java.awt.Color(51, 51, 51));

        userList.setBackground(new java.awt.Color(51, 51, 51));
        userList.setFont(new java.awt.Font("Segoe UI Light", 0, 14)); // NOI18N
        userList.setForeground(new java.awt.Color(255, 255, 255));
        userList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        userList.setSelectionBackground(new java.awt.Color(102, 102, 102));
        userList.setSelectionForeground(new java.awt.Color(255, 255, 255));
        userList.addListSelectionListener(this::userListValueChanged);
        leftPane.setViewportView(userList);

        msgPanel.setBackground(new java.awt.Color(51, 51, 51));
        msgPanel.setLayout(new java.awt.CardLayout());

        chatCardPanel.setBackground(new java.awt.Color(51, 51, 51));
        chatCardPanel.setOpaque(false);

        sendButton.setBackground(new java.awt.Color(0, 153, 153));
        sendButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        sendButton.setForeground(new java.awt.Color(255, 255, 255));
        sendButton.setLabel("SEND");
        sendButton.addActionListener(this::sendButtonActionPerformed);

        msgToSndTextFeild.setBackground(new java.awt.Color(51, 51, 51));
        msgToSndTextFeild.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        msgToSndTextFeild.setForeground(new java.awt.Color(255, 255, 255));
        msgToSndTextFeild.addActionListener(this::msgToSndTextFeildActionPerformed);

        messagesTextArea.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        rightPane.setViewportView(messagesTextArea);

        javax.swing.GroupLayout chatCardPanelLayout = new javax.swing.GroupLayout(chatCardPanel);
        chatCardPanel.setLayout(chatCardPanelLayout);
        chatCardPanelLayout.setHorizontalGroup(
            chatCardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chatCardPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(chatCardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rightPane, javax.swing.GroupLayout.PREFERRED_SIZE, 543, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(chatCardPanelLayout.createSequentialGroup()
                        .addComponent(msgToSndTextFeild, javax.swing.GroupLayout.PREFERRED_SIZE, 421, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24)
                        .addComponent(sendButton, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(1108, 1108, 1108))
        );
        chatCardPanelLayout.setVerticalGroup(
            chatCardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, chatCardPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rightPane, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(chatCardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(msgToSndTextFeild, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sendButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22))
        );

        msgPanel.add(chatCardPanel, "card2");

        filesCardPanel.setBackground(new java.awt.Color(51, 51, 51));
        filesCardPanel.setOpaque(false);

        sendButton1.setBackground(new java.awt.Color(0, 153, 153));
        sendButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        sendButton1.setForeground(new java.awt.Color(255, 255, 255));
        sendButton1.setText("SEND FILE");
        sendButton1.addActionListener(this::sendButton1ActionPerformed);

        rightPaneFiles.setOpaque(false);

        filesTextArea.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        filesTextArea.setOpaque(false);
        rightPaneFiles.setViewportView(filesTextArea);

        chooseButton.setBackground(new java.awt.Color(0, 153, 153));
        chooseButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        chooseButton.setForeground(new java.awt.Color(255, 255, 255));
        chooseButton.setText("CHOOSE FILE");
        chooseButton.addActionListener(this::chooseButtonActionPerformed);

        javax.swing.GroupLayout filesCardPanelLayout = new javax.swing.GroupLayout(filesCardPanel);
        filesCardPanel.setLayout(filesCardPanelLayout);
        filesCardPanelLayout.setHorizontalGroup(
            filesCardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(filesCardPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(filesCardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rightPaneFiles, javax.swing.GroupLayout.PREFERRED_SIZE, 552, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(filesCardPanelLayout.createSequentialGroup()
                        .addComponent(chooseButton)
                        .addGap(18, 18, 18)
                        .addComponent(sendButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(1108, 1108, 1108))
        );
        filesCardPanelLayout.setVerticalGroup(
            filesCardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, filesCardPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rightPaneFiles, javax.swing.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(filesCardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(chooseButton, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                    .addComponent(sendButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(21, 21, 21))
        );

        msgPanel.add(filesCardPanel, "card2");

        settingsCardPanel.setBackground(new java.awt.Color(51, 51, 51));
        settingsCardPanel.setOpaque(false);

        javax.swing.GroupLayout settingsCardPanelLayout = new javax.swing.GroupLayout(settingsCardPanel);
        settingsCardPanel.setLayout(settingsCardPanelLayout);
        settingsCardPanelLayout.setHorizontalGroup(
            settingsCardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 592, Short.MAX_VALUE)
        );
        settingsCardPanelLayout.setVerticalGroup(
            settingsCardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 448, Short.MAX_VALUE)
        );

        msgPanel.add(settingsCardPanel, "card2");

        mainPanel1.setBackground(new java.awt.Color(51, 51, 51));
        mainPanel1.setOpaque(false);

        btnShowChat.setActionCommand("CHAT");
        btnShowChat.setLabel("CHAT");
        btnShowChat.addActionListener(this::btnShowChatActionPerformed);

        btnShowFiles.setActionCommand("FILE");
        btnShowFiles.setLabel("FILES");
        btnShowFiles.addActionListener(this::btnShowFilesActionPerformed);

        btnSettings.setLabel("SETTINGS");
        btnSettings.addActionListener(this::btnSettingsActionPerformed);

        javax.swing.GroupLayout mainPanel1Layout = new javax.swing.GroupLayout(mainPanel1);
        mainPanel1.setLayout(mainPanel1Layout);
        mainPanel1Layout.setHorizontalGroup(
            mainPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnShowChat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(90, 90, 90)
                .addComponent(btnShowFiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(77, 77, 77)
                .addComponent(btnSettings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(207, Short.MAX_VALUE))
        );
        mainPanel1Layout.setVerticalGroup(
            mainPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanel1Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(mainPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnShowChat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnShowFiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSettings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        background.setBackground(new java.awt.Color(51, 51, 51));
        background.setLayout(new java.awt.CardLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nexlink/client/auth/images/background.JPG"))); // NOI18N
        jLabel1.setText("jLabel1");
        background.add(jLabel1, "card2");

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(leftPane, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addComponent(mainPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35))
            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                    .addContainerGap(203, Short.MAX_VALUE)
                    .addComponent(msgPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 592, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(background, javax.swing.GroupLayout.PREFERRED_SIZE, 813, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(mainPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(61, 61, 61)
                        .addComponent(leftPane, javax.swing.GroupLayout.PREFERRED_SIZE, 446, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(79, Short.MAX_VALUE))
            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(mainPanelLayout.createSequentialGroup()
                    .addGap(58, 58, 58)
                    .addComponent(msgPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 448, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(8, Short.MAX_VALUE)))
            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(mainPanelLayout.createSequentialGroup()
                    .addComponent(background, javax.swing.GroupLayout.PREFERRED_SIZE, 586, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void userListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_userListValueChanged
        // TODO add your handling code here:

        String selected = userList.getSelectedValue();

        if (selected != null) {
            //  Clean up the display string to isolate just the pure username
            if (selected.contains(" (online)")) {
                selectedReceiver = selected.replace(" (online)", "");
            } else if (selected.contains(" (offline)")) {
                selectedReceiver = selected.replace(" (offline)", "");
            } else {
                selectedReceiver = selected;
            }

        }
    }//GEN-LAST:event_userListValueChanged

    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendButtonActionPerformed
        // TODO add your handling code here
        String messageText = msgToSndTextFeild.getText().trim();

        // 1. Validation checks
        if (messageText.isEmpty()) {
            return;
        }

        if (selectedReceiver == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Please select a user from the list to chat with.");
            return;
        }

        try {
            // 2. Package the data using our protocol structure: MSG|Receiver|Text
            bufferedWriter.write("MSG|" + selectedReceiver + "|" + messageText);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            // 3. Clear out the text input field for the next message
            msgToSndTextFeild.setText("");

            // Optional: Append your own sent message directly to your messagesTextArea right away
            messagesTextArea.setText(messagesTextArea.getText() + "\nYou: " + messageText);

        } catch (java.io.IOException e) {
            System.out.println("Error sending message: " + e.getMessage());
        }
    }//GEN-LAST:event_sendButtonActionPerformed

    private void sendButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendButton1ActionPerformed
if (selectedFile == null || !selectedFile.exists()) {
        javax.swing.JOptionPane.showMessageDialog(this, "Please select a valid file first!", "No File Selected", javax.swing.JOptionPane.WARNING_MESSAGE);
        return;
    }
    if (selectedReceiver == null) {
        javax.swing.JOptionPane.showMessageDialog(this, "Please select a user to send this file to.", "No Receiver Selected", javax.swing.JOptionPane.WARNING_MESSAGE);
        return;
    }

    new Thread(() -> {
        try {
            long fileSize = selectedFile.length();
            String fileName = selectedFile.getName();
            filesTextArea.setText(filesTextArea.getText() + "\n\n🔄 Uploading '" + fileName + "'...");

            // Step 1: Send text metadata over your standard text writer
            bufferedWriter.write("FILE_START|" + selectedReceiver + "|" + fileName + "|" + fileSize);
            bufferedWriter.newLine();
            bufferedWriter.flush(); // Ensure the line leaves the client completely!

            // Step 2: Stream raw bytes using the raw Socket OutputStream directly
            java.io.OutputStream socketOut = clientSocket.getOutputStream();
            java.io.FileInputStream fileInput = new java.io.FileInputStream(selectedFile);

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = fileInput.read(buffer)) != -1) {
                socketOut.write(buffer, 0, bytesRead);
            }
            socketOut.flush(); // Force all bytes onto the network pipeline
            fileInput.close();

            filesTextArea.setText(filesTextArea.getText() + "\n✅ File transmitted successfully!");
            selectedFile = null;

        } catch (java.io.IOException e) {
            filesTextArea.setText(filesTextArea.getText() + "\n❌ Transfer failed: " + e.getMessage());
            System.out.println("Error sending file over stream: " + e.getMessage());
        }
    }).start();
 
    }//GEN-LAST:event_sendButton1ActionPerformed

    private void btnShowChatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowChatActionPerformed
        // TODO add your handling code here:
        try {
            java.awt.CardLayout cl = (java.awt.CardLayout) msgPanel.getLayout();
            cl.show(msgPanel, "chat");
        } catch (Exception e) {
            System.out.println(" CardLayout Error: " + e.getMessage());
        }
    }//GEN-LAST:event_btnShowChatActionPerformed

    private void btnShowFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowFilesActionPerformed
        // TODO add your handling code here:
        try {
            java.awt.CardLayout cl = (java.awt.CardLayout) msgPanel.getLayout();
            cl.show(msgPanel, "files");
        } catch (Exception e) {
            System.out.println("CardLayout Error: " + e.getMessage());
        }

    }//GEN-LAST:event_btnShowFilesActionPerformed

    private void msgToSndTextFeildActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_msgToSndTextFeildActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_msgToSndTextFeildActionPerformed

    private void chooseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooseButtonActionPerformed
        // TODO add your handling code here:
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setDialogTitle("Select a File to Send via NexLink");

        int result = fileChooser.showOpenDialog(this);
        if (result == javax.swing.JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();

            // Display the selected file name in your filesTextArea or a label so the user knows it worked!
            filesTextArea.setText("📂 Selected File: " + selectedFile.getName()
                    + "\nSize: " + (selectedFile.length() / 1024) + " KB"
                    + "\n\nClick 'SEND FILE' to transmit.");

        }
       
    }//GEN-LAST:event_chooseButtonActionPerformed

    private void btnSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSettingsActionPerformed
        // TODO add your handling code here:
System.out.println("⚙️ Settings Button Clicked!");
    try {
        java.awt.CardLayout cl = (java.awt.CardLayout) msgPanel.getLayout();
        cl.show(msgPanel, "settings"); // 🧼 Swaps flawlessly to your settings view
    } catch (Exception e) {
        System.out.println("❌ CardLayout Error: " + e.getMessage());
    }
    }//GEN-LAST:event_btnSettingsActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel background;
    private java.awt.Button btnSettings;
    private java.awt.Button btnShowChat;
    private java.awt.Button btnShowFiles;
    private javax.swing.JPanel chatCardPanel;
    private javax.swing.JButton chooseButton;
    private javax.swing.JPanel filesCardPanel;
    private javax.swing.JTextPane filesTextArea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane leftPane;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel mainPanel1;
    private javax.swing.JTextPane messagesTextArea;
    private javax.swing.JPanel msgPanel;
    private java.awt.TextField msgToSndTextFeild;
    private javax.swing.JLabel nexLinkLabel;
    private javax.swing.JScrollPane rightPane;
    private javax.swing.JScrollPane rightPaneFiles;
    private javax.swing.JButton sendButton;
    private javax.swing.JButton sendButton1;
    private javax.swing.JPanel settingsCardPanel;
    private javax.swing.JList<String> userList;
    // End of variables declaration//GEN-END:variables
}
