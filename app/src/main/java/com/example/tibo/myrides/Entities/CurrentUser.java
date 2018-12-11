package com.example.tibo.myrides.Entities;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;

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

    public void goOffline(){
        loggedIn=false;
    }
    public void logout(){
        displayName="";
        email="";
       loggedIn=false;
    }

    public void login(){
        loggedIn=true;
    }

    public void disconnectFromFacebook() {

        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {

                LoginManager.getInstance().logOut();

            }
        }).executeAsync();
    }
}
