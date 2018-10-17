package com.example.tibo.myrides;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

//https://github.com/firebase/quickstart-android/blob/8ab219cb636ff5f7e8f8fdf5f8f6f77b3094e0f8/auth/app/src/main/java/com/google/firebase/quickstart/auth/java/FacebookLoginActivity.java#L117-L123
public class InloggenKeuzeActivity extends AppCompatActivity {

    private Button registreerButton;

    //firebase stuff
    private FirebaseAuth mAuth;

    //facebook login button
    private CallbackManager callbackManager;
    private LoginButton fbloginButton;

    //textViews
    private TextView emailTextView;
    private TextView paswoordTextView;

    //gewone login button
    private Button logInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inloggen_keuze);

        //registreerButton
        registreerButton = (Button) findViewById(R.id.RegistreerButton);

        //registreerButton logica
        registreerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(InloggenKeuzeActivity.this, RegistreerActvity.class);
                startActivity(intent);
            }
        });

        //logInButton (niet facebook)
        logInButton = (Button) findViewById(R.id.logInButton);

        //logInButton Logica
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailTextView.getText().toString();
                String password = paswoordTextView.getText().toString();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(InloggenKeuzeActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("firebaseloginattempt", "loginUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("firebaseloginattempt", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }

                                // ...
                            }
                        });



            }
        });

        //textViews
        emailTextView = findViewById(R.id.editTextEmail);
        paswoordTextView = findViewById(R.id.editTextPaswoord);

        //init mAuth
        mAuth = FirebaseAuth.getInstance();

        //facebook gerelateerde stuff
        callbackManager = CallbackManager.Factory.create();
        fbloginButton = findViewById(R.id.fb_login_button);
        fbloginButton.setReadPermissions("email", "public_profile");
        fbloginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("FBLOGIN", "facebook:onSuccess: " + loginResult);

                handleFacebookAccessToken(loginResult.getAccessToken());
                //go to next page?
            }

            @Override
            public void onCancel() {
                Log.d("FBLOGIN", "facebook:onCancel");

            }

            @Override
            public void onError(FacebookException error) {
                Log.d("FBLOGIN", "facebook:onError", error);

                //updateUI(null);

            }
        });
    }

        // [START on_start_check_user]
        @Override
        public void onStart() {
            super.onStart();
            // Check if user is signed in (non-null) and update UI accordingly.
            FirebaseUser currentUser = mAuth.getCurrentUser();
            updateUI(currentUser);
        }
        // [END on_start_check_user]

        // [START on_activity_result]
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            // Pass the activity result back to the Facebook SDK
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        // [END on_activity_result]

        // [START auth_with_facebook]
        private void handleFacebookAccessToken(AccessToken token) {
            Log.d("FACEBOOKLOGIN", "handleFacebookAccessToken:" + token);
            // [START_EXCLUDE silent]
            //showProgressDialog();
            // [END_EXCLUDE]

            AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("FACEBOOKLOGIN", "signInWithCredential:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("FACEBOOKLOGIN", "signInWithCredential:failure", task.getException());
                                Toast.makeText(InloggenKeuzeActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }

                            // [START_EXCLUDE]
                            //hideProgressDialog();
                            // [END_EXCLUDE]
                        }
                    });
        }
        // [END auth_with_facebook]

    public void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        System.out.println("gaat in logout");
        updateUI(null);
    }

    private void updateUI(FirebaseUser user) {
        //hideProgressDialog();
        if (user != null) {
            //emailTextView.setText(getString("umoeder", user.getDisplayName()));
            //paswoordTextView.setText(getString("umoderde2e", user.getUid()));

            //findViewById(R.id.buttonFacebookLogin).setVisibility(View.GONE);
            //findViewById(R.id.buttonFacebookSignout).setVisibility(View.VISIBLE);
        } else {
            emailTextView.setText("je bent uitgelogd");
            paswoordTextView.setText(null);

            //findViewById(R.id.buttonFacebookLogin).setVisibility(View.VISIBLE);
            //findViewById(R.id.buttonFacebookSignout).setVisibility(View.GONE);
        }
    }



}