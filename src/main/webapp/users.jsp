<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.messenger.model.User" %>
<%@ page import="java.util.List" %>
<html>
<head>
    <title>Users</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="container">
    <div class="header">
        <h2>Users</h2>
        <a href="logout" class="logout-btn">Logout</a>
    </div>
    <div class="users-list">
        <%
            List<User> users = (List<User>) request.getAttribute("users");
            User currentUser = (User) session.getAttribute("user");
            if (users != null) {
                for (User user : users) {
                    if (user.getId() != currentUser.getId()) {
        %>
        <div class="user-item">
            <a href="chat?receiverId=<%= user.getId() %>">
                <%= user.getUsername() %>
            </a>
        </div>
        <%
                    }
                }
            }
        %>
    </div>
</div>
</body>
</html>
