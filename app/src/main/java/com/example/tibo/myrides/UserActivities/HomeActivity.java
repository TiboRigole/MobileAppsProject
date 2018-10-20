package com.example.tibo.myrides.UserActivities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.tibo.myrides.MainActivity;
import com.example.tibo.myrides.R;
import com.example.tibo.myrides.RegistreerActvity;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class HomeActivity extends AppCompatActivity {

    //variable reservation

    //buttons
    private Button logOutButton;
    private Button zendButton;
    private Button naarSchoolVbButton;

    //editTexts
    private EditText aantalKmEditText;
    private EditText betaaldBoolEditText;

    //firebase authentication handler
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    //firebase database handler
    FirebaseFirestore db;
    //https://firebase.google.com/docs/firestore/quickstart

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //firebase authentication init
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        Log.d("FIREBASE", currentUser.toString());

        //firebase database init
        db = FirebaseFirestore.getInstance();
        //https://firebase.google.com/docs/firestore/quickstart


        //buttons init
        logOutButton =(Button) findViewById(R.id.logOutButtonOnHomePage);
        zendButton = (Button) findViewById(R.id.zendDBButton);
        naarSchoolVbButton = (Button) findViewById(R.id.buttonschoolvoorbeeld);

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

                //creeer een nieuwe User met een first en last name
                Map<String, Object> user = new HashMap<>();
                user.put("first","Ada");
                user.put("last","Lovelace");
                user.put("born",1815);

                //voeg document toe met een gegenereerde ID
                db.collection("users")
                        .add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("VALUES","DocumentSnapchat added with ID: "+ documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("VALUES", "Error adding document: "+ e);
                            }
                        });

            }
        });

        //schoolvoorbeeld Button logica
        naarSchoolVbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, SchoolVoorbeeldActivity.class));
            }
        });


    }

    //extra hulpmethodes
    private void logout(FirebaseUser currentUser) {
        mAuth.signOut();
    }
}
