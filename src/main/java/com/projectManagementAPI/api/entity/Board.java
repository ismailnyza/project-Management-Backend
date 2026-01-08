package com.projectManagementAPI.api.entity;

import java.util.ArrayList;

public class Board {
    public Long id;
    public String name;
    public ArrayList<String> users;
    public ArrayList<String> statuses;
    public ArrayList<Task> tasks;

    public Board(Long id, String name , ArrayList<String> users , ArrayList<String> statuses , ArrayList<Task> tasks){
        this.id = id;
        this.name = name;
        this.users = users;
        this.statuses = statuses;
        this.tasks = tasks;
    }

    public Long getId(){
        return this.id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getStatuses() {
        return statuses;
    }

    public void setStatuses(ArrayList<String> statuses) {
        this.statuses = statuses;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }
}