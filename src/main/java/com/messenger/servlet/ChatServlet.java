package com.messenger.servlet;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.messenger.dao.MessageDAO;
import com.messenger.dao.UserDAO;
import com.messenger.model.Message;
import com.messenger.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;


@WebServlet("/api/chat")
public class ChatServlet extends HttpServlet {
    private MessageDAO messageDAO;
    private UserDAO userDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        messageDAO = new MessageDAO();
        userDAO = new UserDAO();
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
        String receiverIdStr = request.getParameter("receiverId");

        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            Map<String, Object> responseData = new HashMap<>();
            if (receiverIdStr.equals(String.valueOf(currentUser.getId()))) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                Map<String, String> error = new HashMap<>();
                error.put("error", "Current user can't be receiver");
                objectMapper.writeValue(response.getWriter(), error);
                return;
            }

            if (receiverIdStr != null) {
                int receiverId = Integer.parseInt(receiverIdStr);
                User receiver = userDAO.getUserById(receiverId); // Предполагается, что у вас есть такой метод

                if (receiver == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Receiver not found");
                    objectMapper.writeValue(response.getWriter(), error);
                    return;
                }

                List<Message> messages = messageDAO.getMessagesBetweenUsers(
                        currentUser.getId(), receiverId
                );

                responseData.put("currentUser", currentUser);
                responseData.put("receiver", receiver);
                responseData.put("messages", messages);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                Map<String, String> error = new HashMap<>();
                error.put("error", "Receiver not found");
                objectMapper.writeValue(response.getWriter(), error);
                return;
            }

            objectMapper.writeValue(response.getWriter(), responseData);

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Database error");
            error.put("message", e.getMessage());
            objectMapper.writeValue(response.getWriter(), error);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid receiver ID format");
            objectMapper.writeValue(response.getWriter(), error);
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true"); // Добавьте эту строку!
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
        Map<String, String> requestData = objectMapper.readValue(request.getInputStream(), Map.class);
        int receiverId = Integer.parseInt(requestData.get("receiverId"));
        String content = requestData.get("content");

        Message message = new Message(currentUser.getId(), receiverId, content);
        try {
            messageDAO.saveMessage(message);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true"); // Добавьте эту строку!
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
        Map<String, String> requestData = objectMapper.readValue(request.getInputStream(), Map.class);
        String receiverIdStr = requestData.get("receiverId");

        if (receiverIdStr != null) {
            try {
                int receiverId = Integer.parseInt(receiverIdStr);
                messageDAO.deleteChatMessages(currentUser.getId(), receiverId);
                response.setStatus(HttpServletResponse.SC_OK);
            } catch (SQLException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                throw new ServletException(e);
            }
        } else response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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


