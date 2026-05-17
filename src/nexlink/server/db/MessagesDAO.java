package nexlink.server.db;

import nexlink.server.models.Message;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MessagesDAO {

    // INSERT — save a new message to DB
    public void saveMessage(Message msg) {
        String sql = "INSERT INTO messages (sender, receiver, message) VALUES (?, ?, ?)";

        try {
            Connection conn = DatabaseManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, msg.getSender());
            ps.setString(2, msg.getReceiver());
            ps.setString(3, msg.getMessage());

            ps.executeUpdate();
            System.out.println("Message saved to DB!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // SELECT — get chat history between two users
    public ArrayList<Message> getHistory(String sender, String receiver) {
        String sql = "SELECT * FROM messages WHERE sender = ? AND receiver = ?";
        ArrayList<Message> messages = new ArrayList<>();

        try {
            Connection conn = DatabaseManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, sender);
            ps.setString(2, receiver);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Message msg = new Message(rs.getInt("id"),rs.getString("sender"),rs.getString("receiver"),rs.getString("message"));
                messages.add(msg);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }
}
