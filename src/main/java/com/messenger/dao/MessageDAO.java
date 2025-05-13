package com.messenger.dao;

import com.messenger.model.Message;
import com.messenger.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {
    public void saveMessage(Message message) throws SQLException {
        String sql = "INSERT INTO messages (sender_id, receiver_id, file_url, file_type, timestamp, receiver_content, sender_content) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, message.getSenderId());
            stmt.setInt(2, message.getReceiverId());
            stmt.setString(6, message.getReceiverContent());
            stmt.setString(7, message.getSenderContent());
            stmt.setString(3, message.getFileUrl());
            stmt.setString(4, message.getFileType());
            stmt.setTimestamp(5, Timestamp.valueOf(message.getTimestamp()));
            stmt.executeUpdate();
        }
    }

    public List<Message> getMessagesBetweenUsers(int user1Id, int user2Id) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) ORDER BY timestamp";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, user1Id);
            stmt.setInt(2, user2Id);
            stmt.setInt(3, user2Id);
            stmt.setInt(4, user1Id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Message message = new Message();
                message.setId(rs.getInt("id"));
                message.setSenderId(rs.getInt("sender_id"));
                message.setReceiverId(rs.getInt("receiver_id"));
                message.setReceiverContent(rs.getString("receiver_content"));
                message.setSenderContent(rs.getString("sender_content"));
                message.setFileUrl(rs.getString("file_url"));
                message.setFileType(rs.getString("file_type"));
                message.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                messages.add(message);
            }
        }
        return messages;
    }

    public void deleteChatMessages(int user1Id, int user2Id) throws SQLException {
        String sql = "DELETE FROM messages WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?)";
        try (Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, user1Id);
            stmt.setInt(2, user2Id);
            stmt.setInt(3, user2Id);
            stmt.setInt(4, user1Id);
            stmt.executeUpdate();
        }
    }
}
