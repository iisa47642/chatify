//package com.messenger.servlet;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import jakarta.websocket.*;
//import jakarta.websocket.server.PathParam;
//import jakarta.websocket.server.ServerEndpoint;
//
//import java.io.IOException;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.logging.Logger;
//
//@ServerEndpoint("/websocket/chat/{userId}")
//public class UsersWebSocketEndpoint {
//    private static final Logger logger = Logger.getLogger(ChatWebSocketEndpoint.class.getName());
//    private static final Map<Integer, Session> sessions = new ConcurrentHashMap<>();
//    private static final ObjectMapper objectMapper = new ObjectMapper();
//
//    static {
//        objectMapper.registerModule(new JavaTimeModule());
//    }
//    private static final Map<Integer, java.util.Queue<String>> pendingMessages = new ConcurrentHashMap<>();
//    @OnOpen
//    public void onOpen(Session session, @PathParam("userId") String userIdStr) {
//        try {
//            int userId = Integer.parseInt(userIdStr);
//            if (session.isOpen()) {
//                sessions.put(userId, session);
//                logger.info("WebSocket connection opened for user: " + userId);
//
//                // Отправка отложенных сообщений
//                java.util.Queue<String> messages = pendingMessages.get(userId);
//                if (messages != null && !messages.isEmpty()) {
//                    while (!messages.isEmpty()) {
//                        String message = messages.poll();
//                        session.getBasicRemote().sendText(message);
//                        logger.info("Sent pending message to user: " + userId);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            logger.severe("Error in onOpen: " + e.getMessage());
//        }
//    }
//
//    @OnClose
//    public void onClose(Session session, @PathParam("userId") String userIdStr) {
//        try {
//            int userId = Integer.parseInt(userIdStr);
//            sessions.remove(userId);
//            logger.info("WebSocket connection closed for user: " + userId);
//        } catch (NumberFormatException e) {
//            logger.severe("Invalid user ID: " + userIdStr);
//        }
//    }
//
//    @OnError
//    public void onError(Session session, Throwable throwable) {
//        logger.severe("WebSocket error: " + throwable.getMessage());
//        sessions.values().removeIf(s -> s.equals(session));
//        try {
//            session.close();
//        } catch (IOException e) {
//            logger.severe("Error closing session: " + e.getMessage());
//        }
//    }
//
//    @OnMessage
//    public void onMessage(String message, Session session) {
//        logger.info("Received message from client: " + message);
//    }
//
//    public static void sendMessageToUser(int userId, Object message) {
//        try {
//            String jsonMessage = objectMapper.writeValueAsString(message);
//            Session session = sessions.get(userId);
//
//            if (session != null && session.isOpen()) {
//                session.getBasicRemote().sendText(jsonMessage);
//                logger.info("Message sent to user: " + userId);
//            } else {
//                // Сохраняем сообщение для будущей отправки
//                pendingMessages.computeIfAbsent(userId, k -> new java.util.concurrent.ConcurrentLinkedQueue<>())
//                        .add(jsonMessage);
//                logger.info("User " + userId + " is not connected, message queued");
//            }
//        } catch (IOException e) {
//            logger.severe("Failed to send message: " + e.getMessage());
//        }
//    }
//
//
//    public static void SessionClose(int userId) {
//        Session session = sessions.get(userId);
//        if (session != null) {
//            sessions.values().removeIf(s -> s.equals(session));
//            try {
//                session.close();
//            } catch (IOException e) {
//                logger.severe("Closing session: " + e.getMessage());
//            }
//        }
//    }
//
//    public static boolean isUserConnected(int userId) {
//        Session session = sessions.get(userId);
//        return session != null && session.isOpen();
//    }
//}
