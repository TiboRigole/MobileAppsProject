package com.example.tibo.myrides.General;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.tibo.myrides.HelperPackage.MyService;
import com.example.tibo.myrides.R;


import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class RegistreerFragment extends Fragment {

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
    FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FirebaseApp.initializeApp(getActivity());

        return inflater.inflate(R.layout.activity_registreer, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


        // DEF FIREBASE
        db = FirebaseFirestore.getInstance();



        // DEF LAYOUT
        emailText = (EditText) getView().findViewById(R.id.emailEditView);
        paswoordText = (EditText) getView().findViewById(R.id.paswoordEditView);
        bevestigPaswoordText= (EditText) getView().findViewById(R.id.confirmPaswoordEditView);
        usernameText= getView().findViewById(R.id.usernameEditView);
        paswoordLengteView=getView().findViewById(R.id.counterpaslength);
        registreerButton = (Button) getView().findViewById(R.id.registreerButton);



        //init images
        warningBevestiging= getView().findViewById(R.id.warningPaswoordConfirmatie);
        correcteBevestiging= getView().findViewById(R.id.correctPaswoordConfirmatie);



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
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    Toast.makeText(getActivity(), response.body().string(), Toast.LENGTH_SHORT).show();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        });
                    }
                    else{
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    Toast.makeText(getActivity(), response.body().string(), Toast.LENGTH_SHORT).show();
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





}
