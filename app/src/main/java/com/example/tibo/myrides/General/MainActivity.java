package com.example.tibo.myrides.General;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.tibo.myrides.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private ImageButton infoButton;
    private Button inlogButton;
    private Button registreerButton;


    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        // FIREBASE INIT
        mAuth=FirebaseAuth.getInstance();


        //infoButton init
        infoButton = (ImageButton) findViewById(R.id.infoImageButton);

        //inlogButton init
        inlogButton = (Button)findViewById(R.id.inlogButton);

        //registreerButton init
        registreerButton = (Button)findViewById(R.id.naarRegistreerPaginaButton);

        //infoButton logica
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
            }
        });

        //inlogButton logica
        inlogButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, InlogActivity.class));
            }
        });

        //registreerButton logica
        registreerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegistreerActvity.class));
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            // @TODO: doorverwijzen naar home van user
        }
    }




}
