package com.messenger.servlet;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.messenger.dao.MessageDAO;
import com.messenger.dao.UserDAO;
import com.messenger.model.Message;
import com.messenger.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.logging.Logger;

@WebServlet("/api/chat")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 10, // 10 MB
        maxFileSize = 100 * 1024 * 1024,  // 100 MB
        maxRequestSize = 15 * 1024 * 1024 * 10 // 150 MB
)
public class ChatServlet extends HttpServlet {
    private MessageDAO messageDAO;
    private UserDAO userDAO;
    private ObjectMapper objectMapper;
    private static final Logger logger = Logger.getLogger(ChatServlet.class.getName());

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
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            Map<String, String> error = new HashMap<>();
            error.put("error", "User not authenticated");
            objectMapper.writeValue(response.getWriter(), error);
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        int receiverId = Integer.valueOf(request.getParameter("receiverId"));
        String content = request.getParameter("content");
        String type = request.getParameter("type"); // "photo" или "voice"
        Part filePart = request.getPart("file");
        String Url = null;
        if (filePart != null) {
            String fileName = System.currentTimeMillis() + "_" + getSubmittedFileName(filePart);
            // Путь для сохранения
            String uploadPath = getServletContext().getRealPath("/uploads/" + type);
            logger.info("Upload path: " + uploadPath); // Логирование пути
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                logger.info("Directory created: " + created); // Логирование результата создания директории
            }
            try {
                filePart.write(uploadPath + File.separator + fileName);
            } catch (IOException e) {
                System.out.println("Ошибка записи файла: " + e.getMessage());
            }
            // Возвращаем URL для доступа к файлу
            Url = "http://localhost:8080" + request.getContextPath() + "/uploads/" + type + "/" + fileName;
        }




        // Возвращаем URL для доступа к файлу
        Map<String, String> responseData = new HashMap<>();

        try {
            Message message = new Message(currentUser.getId(), receiverId, content, Url, type);
            messageDAO.saveMessage(message);
            //код для отправки через WebSocket
            ChatWebSocketEndpoint.sendMessageToUser(receiverId, message);

            responseData.put("url", Url);
            responseData.put("type", type);
            objectMapper.writeValue(response.getWriter(), responseData);
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
            System.out.println("session is null");
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

        System.out.println(receiverIdStr);
        if (receiverIdStr != null) {
            try {
                int receiverId = Integer.parseInt(receiverIdStr);
                messageDAO.deleteChatMessages(currentUser.getId(), receiverId);
                response.setStatus(HttpServletResponse.SC_OK);
                ChatWebSocketEndpoint.SessionClose(receiverId);
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

    private String getSubmittedFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String item : items) {
            if (item.trim().startsWith("filename")) {
                return item.substring(item.indexOf("=") + 2, item.length() - 1).replace(" ", "");
            }
        }
        return "";
    }
}


