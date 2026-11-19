package com.example.jobfinder.model;

import java.io.Serializable;

public class Feedback implements Serializable {
    private String id;
    private String userId;
    private String userName;
    private String userEmail;
    private String content;
    private String status; // "Chưa xử lý" / "Đã xử lý"
    private String adminReply;
    private long createdAt;

    public Feedback() {
    }

    public Feedback(String userId, String userName, String userEmail, String content,
                    String status, String adminReply, long createdAt) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.content = content;
        this.status = status;
        this.adminReply = adminReply;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdminReply() {
        return adminReply;
    }

    public void setAdminReply(String adminReply) {
        this.adminReply = adminReply;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}