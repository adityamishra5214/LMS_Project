package com.bbau.library.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a community reply to a book discussion or query.
 */
public class DiscussionReply implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int discussionId;
    private String userId;
    private String userName;
    private UserRole userRole;
    private String replyText;
    private LocalDateTime createdAt;

    public DiscussionReply() {
        this.createdAt = LocalDateTime.now();
    }

    public DiscussionReply(int id, int discussionId, String userId, String userName, UserRole userRole, String replyText) {
        this.id = id;
        this.discussionId = discussionId;
        this.userId = userId;
        this.userName = userName;
        this.userRole = userRole;
        this.replyText = replyText;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDiscussionId() { return discussionId; }
    public void setDiscussionId(int discussionId) { this.discussionId = discussionId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public UserRole getUserRole() { return userRole; }
    public void setUserRole(UserRole userRole) { this.userRole = userRole; }

    public String getReplyText() { return replyText; }
    public void setReplyText(String replyText) { this.replyText = replyText; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

