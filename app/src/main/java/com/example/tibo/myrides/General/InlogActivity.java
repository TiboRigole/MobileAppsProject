package com.example.tibo.myrides.General;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tibo.myrides.Entities.CurrentUser;
import com.example.tibo.myrides.R;
import com.example.tibo.myrides.UserActivities.HomeActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonObject;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class InlogActivity extends AppCompatActivity {



    //manager voor facebook login
    private CallbackManager callbackManager;
    private LoginButton fbloginButton;

    //email en paswoord textviews
    private TextView emailTextView;
    private TextView paswoordTextView;

    //login button
    private Button logInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inlog);


        //GUI
        //init buttons
        fbloginButton = findViewById(R.id.fb_login_button);
        logInButton = (Button) findViewById(R.id.logInButton);

        //init textviews
        emailTextView = findViewById(R.id.editTextEmail);
        paswoordTextView = findViewById(R.id.editTextPaswoord);

        //logica buttons

        //logica inlogbutton met email adres en paswoord
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailTextView.getText().toString();
                String password = paswoordTextView.getText().toString();

                if (!email.equals("") && !password.equals("")) {
                    loginWithDisplayName(email, password);

                }
            }
        });



        //FACEBOOK
        //init manager to handle facebook manager
        callbackManager = CallbackManager.Factory.create();

        // facebook logica
        fbloginButton.setReadPermissions("email", "public_profile");
        Log.d("FBLOGIN","readpermissions set");

        // inlog callback na button press
        fbloginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("FBLOGIN", "facebook:onSuccess: " + loginResult);

                // EIGEN SERVER

                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                // Application code
                                try {
                                    String email = object.getString("email");
                                    String displayName = object.getString("name"); // 01/31/1980 format

                                    // registreer user met facebook
                                    registerAndLoginFacebookUser(displayName, email);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();



                // VOOR FIREBASE INLOGMETHODE
                // handleFacebookAccessToken(loginResult.getAccessToken());
                // go to next page?
            }

            @Override
            public void onCancel() {
                Log.d("FBLOGIN", "facebook:onCancel");
                Toast.makeText(InlogActivity.this,"facebook login gecanceld!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("FBLOGIN", "facebook:onError", error);
                Toast.makeText(InlogActivity.this,"facebook login error!", Toast.LENGTH_SHORT).show();

                //updateUI(null);

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        if(CurrentUser.getInstance()!=null){
            // @TODO: doorverwijzen naar home van user
            updateUIAfterLogin(CurrentUser.getInstance());
        }
    }

    /**
     * login aan de hand van een displayname
     * @param displayName displayname
     * @param password paswoord
     */
    private void loginWithDisplayName(String displayName, String password){

        OkHttpClient client = new OkHttpClient();

        try {

            // aanmaken body van request
            JSONObject jsonString = new JSONObject()
                    .put("displayName", displayName)
                    .put("password", password);


            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonString.toString());
            Request request = new Request.Builder()
                    .url("https://distributedsystemsprojec-bd15e.firebaseapp.com/login")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("Postman-Token", "a2ac0538-d987-4f5c-8795-5a199e0ef4fd")
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    System.out.println("response code "+ response.code());
                    if(response.code()==200) {
                        // login ok, set current user local
                        CurrentUser.getInstance().setEmail(response.body().string());
                        CurrentUser.getInstance().setDisplayName(displayName);
                        CurrentUser.getInstance().login();
                        updateUIAfterLogin(CurrentUser.getInstance());
                    }
                    else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Toast.makeText(InlogActivity.this, response.body().string(), Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    }
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * registreer de user die met facebook is ingelogd
     * @param displayName name van facebookuser
     * @param email email adres gekoppeld aan facebookuser
     */
    private void registerAndLoginFacebookUser(String displayName, String email){
        try {
            // set current user local
            CurrentUser.getInstance().setDisplayName(displayName);
            CurrentUser.getInstance().setEmail(email);
            CurrentUser.getInstance().login();


            // maak account aan op server voor user die inlogt met facebook
            JSONObject jsonString = new JSONObject()
                    .put("displayName", displayName)
                    .put("email", email)
                    .put("password", " ");

            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonString.toString());
            Request request = new Request.Builder()
                    .url("https://distributedsystemsprojec-bd15e.firebaseapp.com/registreer")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("Postman-Token", "69946e15-6cdd-46f5-9521-7acba52858bb")
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {

                }
            });


            updateUIAfterLogin(CurrentUser.getInstance());
        }
        catch (JSONException je){
            je.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("FBLOGIN", "onactivityResult binnengekomen!");
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * als er reeds een user is ingelogd dient de loginpagina niet weergegeven te worden
     * @param user
     */
    private void updateUIAfterLogin(CurrentUser user) {
        if (user.isLoggedIn()) {
            startActivity(new Intent(InlogActivity.this, HomeActivity.class));
        }
    }




}