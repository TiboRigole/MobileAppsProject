package com.example.tibo.myrides.UserActivities;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tibo.myrides.Entities.CurrentUser;
import com.example.tibo.myrides.General.MainActivity;
import com.example.tibo.myrides.HelperPackage.CustomNavigationView;
import com.example.tibo.myrides.HelperPackage.NetworkChangeReceiver;
import com.example.tibo.myrides.R;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class HomeActivity extends AppCompatActivity {

    // INIT LAYOUT
    // lijsten met eigen en gedeelde auto's
    private LinearLayout myCarsLayout;
    private LinearLayout sharedCarsLayout;
    //zijkantLayout
    private DrawerLayout mDrawerLayout;

    // INIT FIREBASE

    private FirebaseFirestore db;
    private CurrentUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        // DEF FIREBASE
        currentUser = CurrentUser.getInstance();
        db = FirebaseFirestore.getInstance();

        // DEF LAYOUT
        myCarsLayout=findViewById(R.id.myCarsList);
        sharedCarsLayout=findViewById(R.id.sharedCarsList);
        mDrawerLayout = findViewById(R.id.drawer_layout_home);

        // toolbar toevoegen aan de layout (nodig om de menuknop te hebben, die het zijkantmenu opkomt)
        Toolbar toolbar = findViewById(R.id.toolbar_HomeActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Hoofdmenu");

        //menuknop toevoegen aan de toolbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        //item tappen : zet het item op selected,  sluit de zijbar
        CustomNavigationView navigationView = (CustomNavigationView) findViewById(R.id.navigationzijkant_view);
        navigationView.initSelect(this, mDrawerLayout);
        /*navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();


                        // verschillende logica's / doorverwijzingen bij knopjes
                        if(menuItem.getItemId() == R.id.nav_logout){
                            //log de user uit
                            currentUser.logout();
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

                        if(menuItem.getItemId()==R.id.nav_other_drives){
                            startActivity(new Intent(HomeActivity.this, OtherDrivesActivity.class ));
                        }

                        if(menuItem.getItemId()==R.id.nav_my_drives){
                            startActivity(new Intent(HomeActivity.this, MyDrivesActivity.class));
                        }

                        return true;
                    }
                });*/


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

        Task<QuerySnapshot> querySharedCars= db.collection("autos").whereArrayContains("sharedWithUsers", currentUser.getEmail()).get();
        querySharedCars.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                findViewById(R.id.loader_sharedCar).setVisibility(View.GONE);
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    TextView kentekenTextView= new TextView(getApplicationContext());
                    kentekenTextView.setTextColor(getColor(R.color.colorText));
                    kentekenTextView.setText(documentSnapshot.get("kenteken").toString());
                    sharedCarsLayout.addView(kentekenTextView);
                }
            }
        });
        querySharedCars.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
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
