package com.example.tibo.myrides.General;

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


import com.example.tibo.myrides.R;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class RegistreerActvity extends AppCompatActivity {

    // INIT LAYOUT
    private EditText emailText;
    private EditText paswoordText;
    private EditText bevestigPaswoordText;
    private EditText usernameText;
    private TextView paswoordLengteView;
    private Button registreerButton;
    private ImageView warningBevestiging;
    private ImageView correcteBevestiging;


    // INIT FIREBASE
    private FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registreer);



        // DEF FIREBASE
        mAuth=FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();



        // DEF LAYOUT
        emailText = (EditText) findViewById(R.id.emailEditView);
        paswoordText = (EditText) findViewById(R.id.paswoordEditView);
        bevestigPaswoordText= (EditText) findViewById(R.id.confirmPaswoordEditView);
        usernameText= findViewById(R.id.usernameEditView);
        paswoordLengteView=findViewById(R.id.counterpaslength);
        registreerButton = (Button) findViewById(R.id.registreerButton);




        //init images
        warningBevestiging= findViewById(R.id.warningPaswoordConfirmatie);
        correcteBevestiging= findViewById(R.id.correctPaswoordConfirmatie);



        // LOGIC BUTTONS AND WIDGETS
        registreerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username= usernameText.getText().toString();
                String email= emailText.getText().toString();
                String paswoord= paswoordText.getText().toString();

                createAccount(username, email, paswoord);



            }
        });

        // controle van paswoord bij editview
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

    /**
     * toevoegen van account aan firebase (als emailadres nog niet in gebruik is)
     * @param username gebruikersnaam
     * @param email emailadres
     * @param password paswoord
     */
    public void createAccount(String username, String email, String password){

        try {
            JSONObject newUser= new JSONObject()
                    .put("displayName", username)
                    .put("email", email)
                    .put("password", password);

            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, newUser.toString());
            Request request = new Request.Builder()
                    .url("https://distributedsystemsprojec-bd15e.firebaseapp.com/registreer")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("Postman-Token", "988a5df0-74cd-4e1c-b6af-f4781fc01bd0")
                    .build();


            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if(response.code()==200){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Toast.makeText(RegistreerActvity.this, response.body().string(), Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Toast.makeText(RegistreerActvity.this, response.body().string(), Toast.LENGTH_SHORT).show();
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

/*
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("FIREBASE", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .setPhotoUri(null)
                                    .build();
                            Task updateProfile= user.updateProfile(profileUpdates);
                            updateProfile.addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    Toast.makeText(RegistreerActvity.this, user.getDisplayName()+ "u bent geregistreerd", Toast.LENGTH_SHORT).show();


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


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("FIREBASE", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistreerActvity.this, "Emailadres reeds in gebruik",
                                    Toast.LENGTH_SHORT).show();
                            }
                    }
                });*/
    }
}
