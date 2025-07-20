package com.example.irr_project.model;

public class Message {
    private String content;
    private String senderId;
    private long timestamp;

    public Message() {}

    public Message(String content, String senderId, long timestamp) {
        this.content = content;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
} 