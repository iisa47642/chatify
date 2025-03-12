package com.messenger.dao;

import com.messenger.model.Message;
import com.messenger.model.User;
import com.messenger.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public void registerUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.executeUpdate();
        }
    }
    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                return user;
            }
            return null;
        }
    }

    public User getUserById(int uId) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        String Id = Integer.toString(uId);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, Id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                return user;
            }
            return null;
        }
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                users.add(user);
            }
        }
        return users;
    }

    public List<User> getUsersByChat(int user1Id) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT DISTINCT u.id, u.username\n" +
                "FROM users u\n" +
                "INNER JOIN messages m \n" +
                "ON (u.id = m.sender_id OR u.id = m.receiver_id)\n" +
                "WHERE (m.sender_id = ? OR m.receiver_id = ?)\n" +
                "AND u.id != ?\n" +
                "ORDER BY u.username;";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, user1Id);
            stmt.setInt(2, user1Id);
            stmt.setInt(3, user1Id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                users.add(user);
            }
        }
        return users;
    }
}
