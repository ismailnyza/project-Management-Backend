package com.projectManagementAPI.model;

public class User {
    public Long id;
    public String firstName;
    public String lastName;

    public User(Long id , String name){
        if (name.contains(" ")){
            String[] fullName = name.split(" ");
                this.firstName = fullName[0];
                this.lastName = fullName[1];
        }
        else {
            this.firstName = name;
            this.lastName = null;
        }
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}