package com.projectManagementAPI.model;

import java.time.LocalDateTime;

public class Comment {
    public Long id;
    public String comment;
    public Long userId;
    public LocalDateTime dateTime;

    public Comment(String comment, LocalDateTime dateTime, Long id, Long userId) {
        this.comment = comment;
        this.dateTime = dateTime;
        this.id = id;
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}