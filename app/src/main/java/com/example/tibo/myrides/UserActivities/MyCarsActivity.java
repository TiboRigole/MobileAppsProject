package com.example.tibo.myrides.UserActivities;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.tibo.myrides.Entities.Car;
import com.example.tibo.myrides.Entities.CurrentUser;
import com.example.tibo.myrides.HelperPackage.CustomNavigationView;
import com.example.tibo.myrides.HelperPackage.NetworkChangeReceiver;
import com.example.tibo.myrides.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyCarsActivity extends AppCompatActivity {

    //init layout
    private Spinner mySpinner;
    private TextView carInfo;
    private DrawerLayout mDrawerLayout;
    private Button updateCarButton;
    private EditText newUserEditText;

    // INIT FIREBASE
    private FirebaseFirestore db;
    private CurrentUser currentUser;

    private Car selectedCar = null;

    private BroadcastReceiver br;

    // OPSLAG
    private HashMap<String, Car> databaseCars;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cars2);

        // DEF FIREBASE
        currentUser = CurrentUser.getInstance();
        db = FirebaseFirestore.getInstance();

        // broadcastreceiver
        br= new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(br, filter);

        // DEF OPSLAG
        databaseCars = new HashMap<>();


        // DEF LAYOUT
        mySpinner= findViewById(R.id.MyCars_carsSpinner);
        carInfo=findViewById(R.id.carInfoTextView);
        updateCarButton = findViewById(R.id.addSharedUserButton);
        newUserEditText = findViewById(R.id.editTextAddEmailEditCar);


        // TOOLBAR EN ZIJKANTMENU SETUP
        mDrawerLayout = findViewById(R.id.drawer_layout_mycars);

        //toolbar toevoegen aan de layout (nodig om de menuknop te hebben, die het zijkantmenu opkomt)
        Toolbar toolbar = findViewById(R.id.toolbar_MyCarsActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Cars");

        //menuknop toevoegen aan de toolbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        //item tappen: zet het item op selected, sluit de zijbar
        CustomNavigationView navigationView = (CustomNavigationView) findViewById(R.id.navigationzijkant_view);
        navigationView.setCheckedItem(R.id.nav_my_cars);
        navigationView.initSelect(this, mDrawerLayout);


        // EINDE TOOLBAR EN ZIJKANTMENU SETUP

        //fetch alle autos die van current user zijn
        // voeg ze toe aan hashmap aan de hand van hun nummerplaat
        loadGegevensVanFirebase();


        // button logic
        updateCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCar == null){
                    Toast.makeText(getApplicationContext(), "geen auto gelesecteerd", Toast.LENGTH_LONG).show();
                }
                else if (!newUserEditText.getText().toString().contains("@")){
                    Toast.makeText(getApplicationContext(), "geen auto gelesecteerd", Toast.LENGTH_LONG).show();
                }

                if(selectedCar != null && newUserEditText.getText().toString().contains("@") ){

                    Task<QuerySnapshot> query = db.collection("autos").whereEqualTo("eigenaar", CurrentUser.getInstance().getEmail()).get();
                    query.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {

                                List<String> users = new ArrayList<>();
                                users.addAll(selectedCar.getSharedWithUsers());
                                users.add(newUserEditText.getText().toString());
                                documentSnapshot.getReference().update("sharedWithUsers", users);

                                Toast.makeText(getApplicationContext(), "toegevoegd", Toast.LENGTH_SHORT).show();

                                loadGegevensVanFirebase();
                                selectedCar = databaseCars.get(mySpinner.getSelectedItem().toString());
                                carInfo.setText(selectedCar.toString());

                            }
                        }
                    });

                }
            }
        });


    }

    private void visualiseerCars() {
        ArrayList<String> nummerplaks = new ArrayList<String>();

        for (Map.Entry<String, Car> entry : databaseCars.entrySet()) {
            String key = entry.getKey();
            nummerplaks.add(key);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, nummerplaks);
        //set the spinners adapter to the previously created one.
        mySpinner.setAdapter(adapter);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.d("tagged",mySpinner.getSelectedItem().toString());
                selectedCar = databaseCars.get(mySpinner.getSelectedItem().toString());
                carInfo.setText(selectedCar.toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }



    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(false){
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterReceiver(br);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void loadGegevensVanFirebase(){
        Task<QuerySnapshot> query = db.collection("autos").whereEqualTo("eigenaar", CurrentUser.getInstance().getEmail()).get();
        query.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {

                    String nummerPlaat = documentSnapshot.getString("kenteken");
                    Car auto = new Car(documentSnapshot.getData());
                    Log.d("found", auto.toString());
                    databaseCars.put(documentSnapshot.getString("kenteken"), auto);
                }
                visualiseerCars();
            }
        });
        query.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });

    }

}
