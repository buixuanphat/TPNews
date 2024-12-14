package com.example.tpnews_ungdungdocbao;

import java.io.Serializable;

public class UserTPNew implements Serializable {
    private String username;
    private String password;
    private int active;

    public UserTPNew() {}

    public UserTPNew(String username, String password, int active) {
        this.username = username;
        this.password = password;
        this.active = active;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

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
