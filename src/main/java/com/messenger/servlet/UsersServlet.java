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
import java.util.stream.Stream;

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

            request.setAttribute("users", users);
            request.getRequestDispatcher("/users.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
