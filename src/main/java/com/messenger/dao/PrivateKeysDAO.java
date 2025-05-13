package com.messenger.dao;
import com.messenger.model.Message;
import com.messenger.util.DBConnection;
import com.messenger.model.PrivateKey;

import java.sql.*;

public class PrivateKeysDAO {
    public void saveKey(PrivateKey privateKey) {
        String sql = "INSERT INTO user_keys VALUES(?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, privateKey.getUserId());
            stmt.setString(2, privateKey.getPrivateKey());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public PrivateKey getKey(int userId) {
        PrivateKey privateKey = new PrivateKey();
        String sql = "SELECT * FROM user_keys WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                privateKey.setUserId(rs.getInt("user_id"));
                privateKey.setPrivateKey(rs.getString("private_key"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return privateKey;
    }
}
