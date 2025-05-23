package com.messenger.model;

import java.io.File;
import java.time.LocalDateTime;

public class Message {
    private int id;
    private int senderId;
    private int receiverId;
    private String senderContent;
    private String receiverContent;
    private String fileUrl;
    private LocalDateTime timestamp;
    private String fileType;

    // Конструкторы, геттеры и сеттеры
    public Message() {}

    public Message(int senderId, int receiverId, String receiverContent ,String senderContent,String fileUrl, String fileType) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.receiverContent = receiverContent;
        this.senderContent = senderContent;
        this.timestamp = LocalDateTime.now();
        this.fileUrl = fileUrl;
        this.fileType = fileType;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }
    public int getReceiverId() { return receiverId; }
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public String getSenderContent() {return senderContent;}
    public void setSenderContent(String senderContent) {this.senderContent = senderContent;}
    public String getReceiverContent() {return receiverContent;}
    public void setReceiverContent(String receiverContent) {this.receiverContent = receiverContent;}
}
