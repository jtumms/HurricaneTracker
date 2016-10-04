package com.company;

/**
 * Created by john.tumminelli on 10/4/16.
 */
public class User {
    String name;
    String password;
    User user;

    public User(User user) {
        this.user = user;
    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;

    }
}
