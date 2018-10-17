package com.example.tibo.myrides;


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

import com.example.tibo.myrides.UserActivities.HomeActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
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

//https://github.com/firebase/quickstart-android/blob/8ab219cb636ff5f7e8f8fdf5f8f6f77b3094e0f8/auth/app/src/main/java/com/google/firebase/quickstart/auth/java/FacebookLoginActivity.java#L117-L123
public class InlogActivity extends AppCompatActivity {

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

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(InlogActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    Log.d("LOGINDEBUG","login successful, onComplete hier");

                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("LOGINDEBUG", "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Log.d("LOGINDEBUG",user.toString());
                                    Log.d("LOGINDEBUG","updateUIAfterLogin enter");

                                    Toast.makeText(InlogActivity.this, "Login succesvol!",Toast.LENGTH_SHORT).show();

                                    // @TODO: ga naar ingelogde pagina
                                    //wacht 3 seconden
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            //wat er moet gebeuren na 5 seconden
                                            Log.d("LOGINDEBUG","3 seconden gewacht!");

                                            //naar homeActivity
                                            updateUIAfterLogin(mAuth.getCurrentUser());

                                        }
                                    }, 1500);

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("firebaseloginattempt", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(InlogActivity.this, "Authentication failed.",
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
        Log.d("FBLOGIN","callbackmanager created");

        // logica facebook
        // opvragen email en foto
        fbloginButton.setReadPermissions("email", "public_profile");
        Log.d("FBLOGIN","readpermissions set");

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
        //als de ingelogd wordt geladen, checkt hij of er een user ingelogd is
        //zoja, gaat hij direct naar de homepagina ervan
        Log.d("FBLOGIN", "onstart methode binnengekomen");

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Als er al iemand ingelogd is, doorverwijzen naar volgende pagina
        updateUIAfterLogin(currentUser);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("FBLOGIN", "onactivityResult binnengekomen!");
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * facebook auth with token
     * gebeurt enkel als we met facebook inloggen!
     * @param token
     */
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("FBLOGIN", "handleFacebookAccessToken:" + token);

        // genereer credentials op basis van token
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        // sign in aan de hand van firebase handler met credential (dus in back-end van firebase)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("FBLOGIN", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUIAfterLogin(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("FBLOGIN", "signInWithCredential:failure", task.getException());
                            Toast.makeText(InlogActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }

    // @TODO: signout methode ergens implementeren!
    public void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        Toast.makeText(InlogActivity.this, "Je bent uitgelogd",
                Toast.LENGTH_SHORT).show();
    }

    private void updateUIAfterLogin(FirebaseUser user) {

        if (user != null) {

            // @TODO: update UI op basis van data horende bij user, waarschijnlijk doorverwijzing naar volgende pagina
            startActivity(new Intent(InlogActivity.this, HomeActivity.class));

        } else {
            // @TODO: update UI wannneer er geen account ingelogd is
        }
    }



}