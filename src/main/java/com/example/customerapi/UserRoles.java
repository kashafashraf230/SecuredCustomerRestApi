package com.example.customerapi;

public class UserRoles {

    private String userRole;

    private String username;

    private String password;

    public UserRoles(){}

    public UserRoles(String userRole, String username, String password) {
        this.userRole = userRole;
        this.username = username;
        this.password = password;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
