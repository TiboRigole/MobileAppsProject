package com.example.tibo.myrides.UserActivities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.example.tibo.myrides.Entities.Car;
import com.example.tibo.myrides.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;
import java.util.List;

public class AddCarActivity extends AppCompatActivity {

    Button addCar;
    Button addSharedUser;

    LinearLayout sharedUsersList;

    EditText verbruikEditView;
    EditText merkEditView;
    EditText kentekenEditView;
    Spinner benzineTypeSpinner;

    // firebase database
    FirebaseFirestore db;

    // firebase authentication handler
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);

        // firebase database
        db = FirebaseFirestore.getInstance();

        //firebase authentication init
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        addCar=findViewById(R.id.addCarButton);
        addSharedUser=findViewById(R.id.addUserButton);

        sharedUsersList=findViewById(R.id.listSharedUsers);

        verbruikEditView=findViewById(R.id.verbruikEditView);
        merkEditView= findViewById(R.id.merkEditView);
        kentekenEditView= findViewById(R.id.kentekenEditView);
        benzineTypeSpinner= findViewById(R.id.benzineTypeDropdown);

        String[] benzinetypes= new String[]{"DIESEL", "EURO 95 / E10", "SUPER+98", "CNG", "AD BLUE"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, benzinetypes);
        //set the spinners adapter to the previously created one.
        benzineTypeSpinner.setAdapter(adapter);

        addCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!verbruikEditView.getText().equals("") && benzineTypeSpinner.getSelectedItem()!=null){

                    // overlopen van alle ingevoerde emailadressen
                    List<String> emailadressen= new ArrayList<>();
                    final int childCount = sharedUsersList.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        EditText userTextView = (EditText) sharedUsersList.getChildAt(i);
                        // Do something with v.
                        if(!userTextView.getText().toString().equals("")) {
                            emailadressen.add(userTextView.getText().toString());
                        }
                    }
                    Car newCar= new Car(currentUser.getEmail(), Double.parseDouble(verbruikEditView.getText().toString()),kentekenEditView.getText().toString(), merkEditView.getText().toString(), benzineTypeSpinner.getSelectedItem().toString(), emailadressen);
                    System.out.println(newCar.toString());

                    // @TODO: check of nummerplaat nog niet in gebruik is

                    //toevoegen aan databank
                    db.collection("autos")
                            .add(newCar)
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
            }
        });

        addSharedUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText newSharedUser= new EditText(getApplicationContext());
                newSharedUser.setTextColor(getColor(R.color.colorText));
                newSharedUser.setHintTextColor(getColor(R.color.colorTextHint));
                newSharedUser.setHint("zzz@domain.com");
                newSharedUser.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(s.length()==0){
                            sharedUsersList.removeView(newSharedUser);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                sharedUsersList.addView(newSharedUser);

            }
        });



    }
}
