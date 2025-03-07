<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.messenger.model.Message" %>
<%@ page import="com.messenger.model.User" %>
<%@ page import="java.util.List" %>
<html>
<head>
    <title>Chat</title>
    <link rel="stylesheet" href="css/style.css">
    <script src="js/chat.js"></script>
</head>
<body>
<div class="container">
    <div class="header">
        <h2>Chat with <%= ((User)request.getAttribute("receiver")).getUsername() %></h2>
        <a href="users" class="back-btn">Back to Users</a>
        <a href="logout" class="logout-btn">Logout</a>
    </div>
    <div class="chat-container">
        <div class="messages" id="messages">
            <%
                List<Message> messages = (List<Message>) request.getAttribute("messages");
                User currentUser = (User) session.getAttribute("user");
                if (messages != null) {
                    for (Message message : messages) {
                        String messageClass = message.getSenderId() == currentUser.getId()
                                ? "message sent" : "message received";
            %>
            <div class="<%= messageClass %>">
                <p><%= message.getContent() %></p>
                <span class="timestamp"><%= message.getTimestamp() %></span>
            </div>
            <%
                    }
                }
            %>
        </div>
        <form action="chat" method="post" class="message-form">
            <input type="hidden" name="receiverId"
                   value="<%= ((User)request.getAttribute("receiver")).getId() %>">
            <input type="text" name="content" placeholder="Type a message..." required>
            <button type="submit">Send</button>
        </form>
    </div>
</div>
<script>
    // Автоматическая прокрутка к последнему сообщению
    const messagesDiv = document.getElementById('messages');
    messagesDiv.scrollTop = messagesDiv.scrollHeight;
</script>
</body>
</html>