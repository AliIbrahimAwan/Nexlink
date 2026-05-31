/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nexlink.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import nexlink.server.models.FileModel;

public class FileDAO {

    public boolean logFileTransfer(FileModel file) {
        // Adjusted query strings to remove the path completely
        String query = "INSERT INTO files (file_name, sender, receiver) VALUES (?, ?, ?)";
        try {
            Connection conn = DatabaseManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, file.getFileName());
            ps.setString(2, file.getSender());
            ps.setString(3, file.getReceiver());
            
            int rowsInserted = ps.executeUpdate();
            ps.close();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.out.println("❌ Database Error logging file transfer: " + e.getMessage());
            return false;
        }
    }
}