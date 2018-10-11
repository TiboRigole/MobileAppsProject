package com.example.tibo.myrides;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;


public class InloggenKeuzeActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private TextView textViewEmail;
    private LoginButton loginButton;
    private ImageView imageAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inloggen_keuze);

        //textView email instellen
        textViewEmail = findViewById(R.id.txtEmail);

        //facebook foto kader instellen
        imageAvatar = (ImageView)findViewById(R.id.avatar);

        //facebook login methoden
        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton)  findViewById(R.id.login_buttonSituatie1);
        loginButton.setReadPermissions(Arrays.asList("public_profile","email"));

        //callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject me, GraphResponse response) {
                                if  (response.getError() != null){
                                    Log.d("inloggenskeer","geen error");
                                }
                                else{
                                    Log.d("inloggenskeer","wel error?");
                                    String email =  me.optString("email");
                                    String id = me.optString("id");
                                }
                            }
                        }).executeAsync();

            }

            @Override
            public void onCancel() {
                //app code
            }

            @Override
            public void onError(FacebookException error) {
                //app code
            }
        });

        //if already login
        if(AccessToken.getCurrentAccessToken() !=null)
        {
            //jsut set user id
            textViewEmail.setText(AccessToken.getCurrentAccessToken().getUserId());
        }

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //textview set text "successfully logged in"

                GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject me, GraphResponse response) {
                                if  (response.getError() != null){
                                    Log.d("inloggenskeer","geen error");
                                }
                                else{
                                    Log.d("inloggenskeer","wel error?");
                                    String email =  me.optString("email");
                                    String id = me.optString("id");

                                    //value setten?
                                    textViewEmail.setText(email);
                                }
                            }
                        }).executeAsync();
            }

            @Override
            public void onCancel() {
                //textview set text "login  canceled"
            }

            @Override
            public void onError(FacebookException error) {

            }
        });


    }

    private void getData(JSONObject object) {
        try{
            URL profile_picture = new URL ("https://graph.facebook.com/"+object.getString("id")+"/picture?width=250&height=250");

            Picasso.with(this).load(profile_picture.toString()).into(imageAvatar);

            textViewEmail.setText(object.getString("email"));



        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
