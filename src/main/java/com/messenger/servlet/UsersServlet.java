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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@WebServlet("/api/users")
public class UsersServlet extends HttpServlet {
    private UserDAO userDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        userDAO = new UserDAO();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Регистрация модуля для поддержки Java 8 Date/Time
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
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

        try {

            // Получаем параметр поиска из запроса
            // Для гет запроса
            String searchQuery = request.getParameter("search");

            List<User> users;
            Map<String, Object> responseData = new HashMap<>();
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                // Если есть поисковый запрос, ищем пользователей по имени
                User currentUser = (User) session.getAttribute("user");
                users = Stream.concat(
                                userDAO.getAllUsers().stream()
                                        .filter(user -> user.getUsername().equalsIgnoreCase(searchQuery)
                                                && user.getId() != currentUser.getId()),
                                userDAO.getUsersByChat(currentUser.getId()).stream()
                        )
                        .distinct() // чтобы убрать дубликаты
                        .collect(Collectors.toList());
            } else {
                // Если поискового запроса нет, передаем пустой список
                User currentUser = (User) session.getAttribute("user");
                users = userDAO.getUsersByChat(currentUser.getId());
            }

            responseData.put("users", users);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), responseData);
        } catch (SQLException e) {
            throw new ServletException(e);
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
