package com.example.tibo.myrides.UserActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.tibo.myrides.InlogActivity;
import com.example.tibo.myrides.MainActivity;
import com.example.tibo.myrides.R;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class HomeActivity extends AppCompatActivity {

    //variable reservation

    //buttons
    private Button logOutButton;

    //firebase authentication handler
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //firebase init
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        Log.d("FIREBASE", currentUser.toString());

        //buttons init
        logOutButton =(Button) findViewById(R.id.logOutButtonOnHomePage);

        //logout button logica
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //log de user uit
                logout(currentUser);

                //log uit van facebook
                LoginManager.getInstance().logOut();

                //ga terug naar de mainActivity
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
            }
        });

    }

    //extra hulpmethodes
    private void logout(FirebaseUser currentUser) {
        mAuth.signOut();
    }
}
