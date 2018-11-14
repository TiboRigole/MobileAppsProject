package com.example.tibo.myrides.UserActivities;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tibo.myrides.Entities.Car;
import com.example.tibo.myrides.Entities.CurrentUser;
import com.example.tibo.myrides.General.MainActivity;
import com.example.tibo.myrides.HelperPackage.CustomNavigationView;
import com.example.tibo.myrides.R;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.List;

/**
 * In deze activity kan men een auto aan z'n account toevoegen
 */
public class AddCarActivity extends AppCompatActivity {

    // INIT LAYOUT

    private Button addCar;
    // toevoegen van een editText field aan de lijst sharedUsers
    private Button addSharedUser;
    // In deze lijst kan men gebruikers toevoegen adhv emailadres om de auto te delen
    private LinearLayout sharedUsersList;
    private EditText verbruikEditView;
    private EditText merkEditView;
    private EditText kentekenEditView;
    // nodig om prijs van benzine te bepalen
    private Spinner benzineTypeSpinner;
    // zijkantview
    private DrawerLayout mDrawerLayout;



    // init firebase database
    private FirebaseFirestore db;


    // init currentUser
    private CurrentUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);

        //FIREBASE
        // def firebase database
        db = FirebaseFirestore.getInstance();

        currentUser = CurrentUser.getInstance();


        // DEF LAYOUT
        addCar=findViewById(R.id.addCarButton);
        addSharedUser=findViewById(R.id.addUserButton);
        sharedUsersList=findViewById(R.id.listSharedUsers);
        verbruikEditView=findViewById(R.id.verbruikEditView);
        merkEditView= findViewById(R.id.merkEditView);
        kentekenEditView= findViewById(R.id.kentekenEditView);
        benzineTypeSpinner= findViewById(R.id.benzineTypeDropdown);
        mDrawerLayout = findViewById(R.id.drawer_layout_addcar);


        // toolbar toevoegen aan de layout (nodig om de menuknop te hebben, die het zijkantmenu opkomt)
        Toolbar toolbar = findViewById(R.id.toolbar_AddCarActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Car");

        //menuknop toevoegen aan de toolbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);



        //item tappen : zet het item op selected,  sluit de zijbar
        CustomNavigationView navigationView = findViewById(R.id.navigationzijkant_view);
        navigationView.setCheckedItem(R.id.nav_add_car);
        navigationView.initSelect(this, mDrawerLayout);


        // Waardes aan spinner toekennen
        String[] benzinetypes= new String[]{"DIESEL", "EURO 95 / E10", "SUPER+98", "CNG", "AD BLUE"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, benzinetypes);
        //set the spinners adapter to the previously created one.
        benzineTypeSpinner.setAdapter(adapter);

        // LOGIC BUTTONS AND WIDGETS
        // toevoegen van car aan database
        addCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // niet opslaan indien kenteken, verbruik en benzinetype niet is ingegeven
                if(!kentekenEditView.getText().equals("") && !verbruikEditView.getText().equals("") && benzineTypeSpinner.getSelectedItem()!=null){

                    // lijst aanmaken van alle ingevoerde emailadressen waarmee deze auto gedeeld
                    // dient te worden
                    List<String> emailadressen= new ArrayList<>();
                    final int childCount = sharedUsersList.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        EditText userTextView = (EditText) sharedUsersList.getChildAt(i);
                        // Do something with v.
                        if(!userTextView.getText().toString().equals("")) {
                            emailadressen.add(userTextView.getText().toString());
                        }
                    }

                    // Nieuw object Car aanmaken met ingevoerde gegevens
                    Car newCar= new Car(currentUser.getEmail(),
                            Double.parseDouble(verbruikEditView.getText().toString()),
                            kentekenEditView.getText().toString(), merkEditView.getText().toString(),
                            benzineTypeSpinner.getSelectedItem().toString(), emailadressen);


                    // @TODO: check of nummerplaat nog niet in gebruik is

                    // toevoegen van newCar aan databank
                    db.collection("autos")
                            .add(newCar)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    // auto succesvol toegevoegd, ga terug naar homeactivity
                                    startActivity(new Intent(AddCarActivity.this, HomeActivity.class));
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Er is een fout opgetreden, probeer opnieuw", Toast.LENGTH_SHORT);
                                }
                            });
                }
            }
        });

        //toevoegen van nieuwe EditView aan linearlayout zodat een nieuw emailadres kan worden toegevoegd
        addSharedUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText newSharedUser= new EditText(getApplicationContext());
                // layout van EditText
                newSharedUser.setTextColor(getColor(R.color.colorText));
                newSharedUser.setHintTextColor(getColor(R.color.colorTextHint));
                newSharedUser.setHint("zzz@domain.com");

                // textveldje verdwijnt als het emailadres verwijderd wordt
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

    //https://developer.android.com/training/implementing-navigation/nav-drawer#java
    //logica wanneer op menu knop geduwd wordt dat het sidebarmenu geopend wordt
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
