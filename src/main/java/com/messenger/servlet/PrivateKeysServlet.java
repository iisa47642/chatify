package com.messenger.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.messenger.dao.MessageDAO;
import com.messenger.dao.PrivateKeysDAO;
import com.messenger.dao.UserDAO;
import com.messenger.model.Message;
import com.messenger.model.PrivateKey;
import com.messenger.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@WebServlet("/api/private_keys")
public class PrivateKeysServlet extends HttpServlet {
    private UserDAO userDAO;
    private PrivateKeysDAO privateKeysDAO;
    private ObjectMapper objectMapper;
    private static final Logger logger = Logger.getLogger(PrivateKeysServlet.class.getName());

    @Override
    public void init() {
        userDAO = new UserDAO();
        privateKeysDAO = new PrivateKeysDAO();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Регистрация модуля для поддержки Java 8 Date/Time
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Установка заголовков для CORS (если необходимо)
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true"); // Добавьте эту строку!

        // Проверка авторизации
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            Map<String, String> error = new HashMap<>();
            error.put("error", "User not authenticated");
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), error);
            return;
        }
        User currentUser = (User) session.getAttribute("user");
        PrivateKey privateKey = privateKeysDAO.getKey(currentUser.getId());
        response.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(response.getWriter(), privateKey);

    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true"); // Добавьте эту строку!
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, String> requestData = objectMapper.readValue(request.getInputStream(), Map.class);
        String username = requestData.get("username");
        String newPrivateKey = requestData.get("privateKey");
        User currentUser;
        try { currentUser = userDAO.getUserByUsername(username);} catch (SQLException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
        PrivateKey privateKey = new PrivateKey(currentUser.getId(),newPrivateKey);
        privateKeysDAO.saveKey(privateKey);
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true"); // Добавьте эту строку!
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
