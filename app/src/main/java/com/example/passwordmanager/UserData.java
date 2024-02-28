package com.example.passwordmanager;

public class UserData {

    String type;
    String user_name;
    String password;

    public UserData( String type, String user_name, String password) {
        this.type = type;
        this.user_name = user_name;
        this.password = password;
    }





    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
