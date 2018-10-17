package com.example.tibo.myrides;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegistreerActvity extends AppCompatActivity {


    private EditText usernameText;
    private EditText paswoordText;
    private EditText bevestigPaswoordText;

    private TextView paswoordLengteView;

    private Button registreerButton;

    private ImageView warningBevestiging;
    private ImageView correcteBevestiging;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registreer);

        // FIREBASE INIT
        mAuth=FirebaseAuth.getInstance();

        //teksveldjes init
        usernameText = (EditText) findViewById(R.id.emailEditView);
        paswoordText = (EditText) findViewById(R.id.paswoordEditView);
        bevestigPaswoordText= (EditText) findViewById(R.id.confirmPaswoordEditView);

        paswoordLengteView=findViewById(R.id.counterpaslength);

        // init button
        registreerButton = (Button) findViewById(R.id.registreerButton);



        //init images
        warningBevestiging= findViewById(R.id.warningPaswoordConfirmatie);
        correcteBevestiging= findViewById(R.id.correctPaswoordConfirmatie);



        registreerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount(usernameText.getText().toString(), paswoordText.getText().toString());
            }
        });

        TextWatcher paswoordIdemControler=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //correct
                if(bevestigPaswoordText.getText().toString().equals(paswoordText.getText().toString())){
                    warningBevestiging.setVisibility(View.INVISIBLE);
                    correcteBevestiging.setVisibility(View.VISIBLE);
                    if(paswoordText.getText().toString().length()>=6) {
                        registreerButton.setEnabled(true);
                    }
                }
                //incorrect
                else{
                    warningBevestiging.setVisibility(View.VISIBLE);
                    correcteBevestiging.setVisibility(View.INVISIBLE);
                    registreerButton.setEnabled(false);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        TextWatcher paswoordCounter= new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                paswoordLengteView.setText(String.valueOf(s.length()));
                if(s.length()>=6){
                    paswoordLengteView.setTextColor(getResources().getColor(R.color.colorAccent));
                    if(bevestigPaswoordText.getText().toString().equals(paswoordText.getText().toString())) {
                        registreerButton.setEnabled(true);
                    }
                }
                else if(s.length()==0){
                    paswoordLengteView.setTextColor(Color.parseColor("#20FFFFFF"));
                    registreerButton.setEnabled(false);
                }
                else{
                    paswoordLengteView.setTextColor(Color.RED);
                    registreerButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        paswoordText.addTextChangedListener(paswoordIdemControler);
        paswoordText.addTextChangedListener(paswoordCounter);

        bevestigPaswoordText.addTextChangedListener(paswoordIdemControler);

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

    public void createAccount(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("FIREBASE", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(RegistreerActvity.this, user.toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("FIREBASE", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistreerActvity.this, "Emailadres reeds in gebruik",
                                    Toast.LENGTH_SHORT).show();
                            }

                        // ...
                    }
                });
    }
}
