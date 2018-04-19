package com.bupt.run.runningapp.appserver.model;

/**
 * Created by Mojo on 2017/4/11.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @SerializedName("username")
    @Expose

    private String username;
    @SerializedName("password")
    @Expose
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {

        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
