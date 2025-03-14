package com.messenger.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@ServerEndpoint("/websocket/chat/{userId}")
public class ChatWebSocketEndpoint {
    private static final Logger logger = Logger.getLogger(ChatWebSocketEndpoint.class.getName());
    private static final Map<Integer, Session> sessions = new ConcurrentHashMap<>();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userIdStr) {
        try {
            int userId = Integer.parseInt(userIdStr);
            sessions.put(userId, session);
            logger.info("WebSocket connection opened for user: " + userId);
        } catch (NumberFormatException e) {
            logger.severe("Invalid user ID: " + userIdStr);
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("userId") String userIdStr) {
        try {
            int userId = Integer.parseInt(userIdStr);
            sessions.remove(userId);
            logger.info("WebSocket connection closed for user: " + userId);
        } catch (NumberFormatException e) {
            logger.severe("Invalid user ID: " + userIdStr);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.severe("WebSocket error: " + throwable.getMessage());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // Обработка входящих сообщений от клиента (если нужно)
        logger.info("Received message: " + message);
    }

    // Метод для отправки сообщения конкретному пользователю
    public static void sendMessageToUser(int userId, Object message) {
        Session session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(objectMapper.writeValueAsString(message));
                logger.info("Message sent to user: " + userId);
            } catch (IOException e) {
                logger.severe("Failed to send message: " + e.getMessage());
            }
        } else {
            logger.info("User " + userId + " is not connected");
        }
    }
}
