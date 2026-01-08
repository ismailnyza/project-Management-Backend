package com.projectManagementAPI.model.entity;

public class Task{
    public Long id;
    public String taskName;
    public String status;
    public String dueDate;
    public String assignee;
    public String project;

//    constructor
    public Task(String assignee, String dueDate, Long id, String project, String status, String taskName) {
        this.assignee = assignee;
        this.dueDate = dueDate;
        this.id = id;
        this.project = project;
        this.status = status;
        this.taskName = taskName;
    }

//    getters setters
    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
}