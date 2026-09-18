package com.example.jobfinder.admin;

public class AdminReport {

    private String id;
    private String userName;
    private String userEmail;
    private String userId;
    private String content;
    private String status;
    private String adminReply;
    private long createdAt;

    public AdminReport() {
    }

    public AdminReport(
            String id,
            String userName,
            String userEmail,
            String userId,
            String content,
            String status,
            String adminReply,
            long createdAt) {

        this.id = id;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userId = userId;
        this.content = content;
        this.status = status;
        this.adminReply = adminReply;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public String getStatus() {
        return status;
    }

    public String getAdminReply() {
        return adminReply;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}