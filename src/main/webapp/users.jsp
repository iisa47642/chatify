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
        <h2>Find Users</h2>
        <a href="logout" class="logout-btn">Logout</a>
    </div>
    <div class="search-form">
        <form action="users" method="get">
            <input type="text" name="search" placeholder="Enter username to search..."
                   value="<%= request.getParameter("search") != null ? request.getParameter("search") : "" %>"
                   minlength="2" maxlength="50" required>
            <button type="submit">Search</button>
        </form>
    </div>
    <div class="users-list">
        <%
            List<User> users = (List<User>) request.getAttribute("users");
            if (users != null && !users.isEmpty()) {
                for (User user : users) {
        %>
        <div class="user-item">
            <a href="chat?receiverId=<%= user.getId() %>">
                <%= user.getUsername() %>
            </a>
        </div>
        <%
            }
        } else if (request.getParameter("search") != null) {
        %>
        <p>No users found.</p>
        <%
            }
        %>
    </div>
</div>
</body>
</html>
