package com.messenger.model;

public class PrivateKey {
    private int userId;
    private String privateKey;

    public PrivateKey() {}

    public PrivateKey(int userId, String privateKey) {
        this.userId = userId;
        this.privateKey = privateKey;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
