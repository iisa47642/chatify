package com.messenger.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.messenger.dao.UserDAO;
import com.messenger.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {
    private UserDAO userDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        userDAO = new UserDAO();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Регистрация модуля для поддержки Java 8 Date/Time
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Добавляем заголовки CORS
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true"); // Добавьте эту строку!
        try {
            // Читаем JSON из тела запроса
            Map<String, String> loginData = objectMapper.readValue(request.getInputStream(), Map.class);
            String username = loginData.get("username");
            String password = loginData.get("password");

            // Проверяем, что username и password не null
            if (username == null || password == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                Map<String, String> error = new HashMap<>();
                error.put("error", "Username and password are required");
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                objectMapper.writeValue(response.getWriter(), error);
                return;
            }

            User user = userDAO.getUserByUsername(username);

            if (user != null && user.getPassword().equals(password)) {
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                response.setStatus(HttpServletResponse.SC_OK);
                Map<String, String> success = new HashMap<>();
                success.put("message", "Login successful");
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                objectMapper.writeValue(response.getWriter(), success);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid username or password");
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                objectMapper.writeValue(response.getWriter(), error);
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Database error");
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), error);
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true"); // Добавьте эту строку!
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
