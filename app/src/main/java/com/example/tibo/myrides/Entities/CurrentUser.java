package com.example.tibo.myrides.Entities;

public class CurrentUser {
    private static CurrentUser instance;
    private String displayName;
    private String email;
    private boolean loggedIn;

    public CurrentUser(){
        this.displayName="";
        this.email="";
    }

    public static synchronized CurrentUser getInstance(){
        if(instance==null){
            instance= new CurrentUser();
        }
        return instance;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public void logout(){
       loggedIn=false;
    }

    public void login(){
        loggedIn=true;
    }
}
