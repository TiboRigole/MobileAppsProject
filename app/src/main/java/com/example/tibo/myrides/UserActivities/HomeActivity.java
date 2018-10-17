package com.example.tibo.myrides.UserActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.tibo.myrides.InlogActivity;
import com.example.tibo.myrides.MainActivity;
import com.example.tibo.myrides.R;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class HomeActivity extends AppCompatActivity {

    //variable reservation

    //buttons
    private Button logOutButton;
    private Button zendButton;

    //editTexts
    private EditText aantalKmEditText;
    private EditText betaaldBoolEditText;

    //firebase authentication handler
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    //firebase database handler
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //firebase authentication init
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        Log.d("FIREBASE", currentUser.toString());

        //firebase database init
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //buttons init
        logOutButton =(Button) findViewById(R.id.logOutButtonOnHomePage);
        zendButton = (Button) findViewById(R.id.zendDBButton);

        //edittexts init
        aantalKmEditText = (EditText) findViewById(R.id.editTextAantalKm);
        betaaldBoolEditText = (EditText) findViewById(R.id.editTextBoolean);

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

        //zend Button logica
        zendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int aantalKm = Integer.parseInt(aantalKmEditText.getText().toString());
                int betaaldInt = Integer.parseInt(betaaldBoolEditText.getText().toString());
                boolean betaald = false;
                if(betaaldInt == 1){betaald = true;}

                Log.d("VALUES","aantalKm:"+aantalKm);
                Log.d("VALUES","betaald:"+betaald);

                //verwerken en versturen?
                mDatabase.child("/ritten/7VSSVrqriZcBIJVuc7fs").child("aantalKm").setValue(aantalKm);
                mDatabase.child("/ritten/7VSSVrqriZcBIJVuc7fs").child("betaald").setValue(betaald);
            }
        });


    }

    //extra hulpmethodes
    private void logout(FirebaseUser currentUser) {
        mAuth.signOut();
    }
}
