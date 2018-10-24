package com.example.tibo.myrides.UserActivities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tibo.myrides.MainActivity;
import com.example.tibo.myrides.R;
import com.example.tibo.myrides.RegistreerActvity;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class HomeActivity extends AppCompatActivity {

    // lists met auto's
    LinearLayout myCarsLayout;
    LinearLayout sharedCarsLayout;



    //firebase authentication handler
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    //firebase database handler
    FirebaseFirestore db;
    //https://firebase.google.com/docs/firestore/quickstart

    //zijkantLayout
    private DrawerLayout mDrawerLayout;

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


        myCarsLayout=findViewById(R.id.myCarsList);
        sharedCarsLayout=findViewById(R.id.sharedCarsList);


        //zijkantmenu init
        mDrawerLayout = findViewById(R.id.drawer_layout_home);

        //toolbar toevoegen aan de layout (nodig om de menuknop te hebben, die het zijkantmenu oppopt)
        Toolbar toolbar = findViewById(R.id.toolbar_HomeActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Hoofdmenu");

        //menuknopje toevoegen aan de toolbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        //item tappen : zet het item op selected,  sluit de zijbar
        NavigationView navigationView = findViewById(R.id.navigationzijkant_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        if(menuItem.getItemId() == R.id.nav_logout){
                            Log.d("checketupt","tis de justen");

                            //log de user uit
                            mAuth.signOut();

                            //log uit van facebook
                            LoginManager.getInstance().logOut();

                            //ga terug naar de mainActivity
                            startActivity(new Intent(HomeActivity.this, MainActivity.class));

                        }

                        if(menuItem.getItemId()==R.id.nav_add_drive){
                            startActivity(new Intent(HomeActivity.this, AddDriveActivity.class));
                        }

                        if(menuItem.getItemId()==R.id.nav_add_car){
                            startActivity(new Intent(HomeActivity.this, AddCarActivity.class));
                        }

                        // Add code here to update the UI based on the item selected
                        // hier komt de logica wat er moet gebeuren eenmaal je op een
                        // knop in de zijkantmenu duwt
                        // For example, swap UI fragments here

                        return true;
                    }
                });


        // vul lijst met mijn auto's en gedeelde auto's
        Task<QuerySnapshot> query= db.collection("autos").whereEqualTo("eigenaar", currentUser.getEmail()).get();

        query.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                findViewById(R.id.loader_myCar).setVisibility(View.GONE);
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    TextView kentekenTextView= new TextView(getApplicationContext());
                    kentekenTextView.setTextColor(getColor(R.color.colorText));
                    kentekenTextView.setText(documentSnapshot.get("kenteken").toString());
                    myCarsLayout.addView(kentekenTextView);
                }
            }
        });
        query.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });


        // @TODO: vul lijst met shared auto's aan



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
