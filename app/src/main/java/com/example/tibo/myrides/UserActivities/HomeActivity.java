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

import com.example.tibo.myrides.MainActivity;
import com.example.tibo.myrides.R;
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
        //https://firebase.google.com/docs/firestore/quickstart


        //buttons init
        logOutButton =(Button) findViewById(R.id.logOutButtonOnHomePage);
        zendButton = (Button) findViewById(R.id.zendDBButton);
        naarSchoolVbButton = (Button) findViewById(R.id.buttonschoolvoorbeeld);

        //edittexts init
        aantalKmEditText = (EditText) findViewById(R.id.editTextAantalKm);
        betaaldBoolEditText = (EditText) findViewById(R.id.editTextBoolean);

        //zijkantmenu init
        mDrawerLayout = findViewById(R.id.drawer_layout_home);

        //toolbar toevoegen aan de layout (nodig om de menuknop te hebben, die het zijkantmenu oppopt
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

                        // Add code here to update the UI based on the item selected
                        // hier komt de logica wat er moet gebeuren eenmaal je op een
                        // knop in de zijkantmenu duwt
                        // For example, swap UI fragments here

                        return true;
                    }
                });


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




    //extra hulpmethodes
    private void logout(FirebaseUser currentUser) {
        mAuth.signOut();
    }
}
