package com.messenger.servlet;

import com.messenger.dao.UserDAO;
import com.messenger.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/users")
public class UsersServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }

        try {
            // Получаем параметр поиска из запроса
            String searchQuery = request.getParameter("search");
            List<User> users;

            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                // Если есть поисковый запрос, ищем пользователей по имени
                User currentUser = (User) session.getAttribute("user");
                users = userDAO.getAllUsers().stream()
                        .filter(user -> user.getUsername().equalsIgnoreCase(searchQuery)
                                && user.getId() != currentUser.getId())
                        .collect(Collectors.toList());
            } else {
                // Если поискового запроса нет, передаем пустой список
                users = List.of();
            }

            request.setAttribute("users", users);
            request.getRequestDispatcher("/users.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
