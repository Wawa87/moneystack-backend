package com.wawa87.moneystack.service.system.user.model;

import java.util.ArrayList;
import java.util.List;

public class UserRequest {
    private String username;
    private List<String> emails;
    private String firstName;
    private String lastName;
    private String password;
    private String phoneNumber;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public static User convertToUser(UserRequest userRequest) {
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmails((ArrayList<String>) userRequest.getEmails());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setPassword(userRequest.getPassword());
        return user;
    }

    public static UserRequest convertUsertoRequest(User user) {
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername(user.getUsername());
        userRequest.setPassword(user.getPassword());
        userRequest.setEmails(user.getEmails());
        userRequest.setFirstName(user.getFirstName());
        userRequest.setLastName(user.getLastName());
        userRequest.setPhoneNumber(user.getPhoneNumber());
        return userRequest;
    }
}
