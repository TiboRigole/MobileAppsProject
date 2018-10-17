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

    //firebase authentication handler
    private FirebaseAuth mAuth;

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
        setContentView(R.layout.activity_inloggen_keuze);

        //GUI
        //init buttons
        fbloginButton = findViewById(R.id.fb_login_button);
        registreerButton = (Button) findViewById(R.id.RegistreerButton);
        logInButton = (Button) findViewById(R.id.logInButton);

        //init textviews
        emailTextView = findViewById(R.id.editTextEmail);
        paswoordTextView = findViewById(R.id.editTextPaswoord);

        //logica buttons
        registreerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(InloggenKeuzeActivity.this, RegistreerActvity.class);
                startActivity(intent);
            }
        });

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailTextView.getText().toString();
                String password = paswoordTextView.getText().toString();

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(InloggenKeuzeActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("firebaseloginattempt", "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUIAfterLogin(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("firebaseloginattempt", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(InloggenKeuzeActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


        //FIREBASE
        //init mAuth
        mAuth = FirebaseAuth.getInstance();

        //FACEBOOK
        //init manager to handle facebook manager
        callbackManager = CallbackManager.Factory.create();

        // logica facebook
        // opvragen email en foto
        fbloginButton.setReadPermissions("email", "public_profile");
        // inlog callback na button press
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


    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // Als er al iemand ingelogd is, doorverwijzen naar volgende pagina
        updateUIAfterLogin(currentUser);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * facebook auth with token
     * @param token
     */
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("FACEBOOKLOGIN", "handleFacebookAccessToken:" + token);

        // genereer credentials op basis van token
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        // sign in aan de hand van firebase handler met credential (dus in back-end van firebase)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("FACEBOOKLOGIN", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUIAfterLogin(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("FACEBOOKLOGIN", "signInWithCredential:failure", task.getException());
                            Toast.makeText(InloggenKeuzeActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }


    public void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        Toast.makeText(InloggenKeuzeActivity.this, "Je bent uitgelogd",
                Toast.LENGTH_SHORT).show();
    }

    private void updateUIAfterLogin(FirebaseUser user) {

        if (user != null) {
            // @TODO: update UI op basis van data horende bij user, waarschijnlijk doorverwijzing naar volgende pagina
        } else {
            // @TODO: update UI wannneer er geen account ingelogd is
        }
    }



}