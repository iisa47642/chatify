package com.messenger.servlet;

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

@WebServlet("/chat")
public class ChatServlet extends HttpServlet {
    private MessageDAO messageDAO;
    private UserDAO userDAO;

    @Override
    public void init() {
        messageDAO = new MessageDAO();
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

        User currentUser = (User) session.getAttribute("user");
        String receiverIdStr = request.getParameter("receiverId");

        if (receiverIdStr != null) {
            try {
                int receiverId = Integer.parseInt(receiverIdStr);
                User receiver = userDAO.getUserByUsername(
                        userDAO.getAllUsers().stream()
                                .filter(u -> u.getId() == receiverId)
                                .findFirst().get().getUsername()
                );
                List<Message> messages = messageDAO.getMessagesBetweenUsers(
                        currentUser.getId(), receiverId
                );
                request.setAttribute("receiver", receiver);
                request.setAttribute("messages", messages);
            } catch (SQLException e) {
                throw new ServletException(e);
            }
        }
        request.getRequestDispatcher("/chat.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        int receiverId = Integer.parseInt(request.getParameter("receiverId"));
        String content = request.getParameter("content");

        Message message = new Message(currentUser.getId(), receiverId, content);
        try {
            messageDAO.saveMessage(message);
            response.sendRedirect("chat?receiverId=" + receiverId);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
