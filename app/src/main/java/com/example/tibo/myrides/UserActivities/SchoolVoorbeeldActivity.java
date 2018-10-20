package com.example.tibo.myrides.UserActivities;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;

import com.example.tibo.myrides.MainActivity;
import com.example.tibo.myrides.R;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SchoolVoorbeeldActivity extends AppCompatActivity {

    //variabelen reserveren
    private DrawerLayout mDrawerLayout;

    private Button logoutButton;

    //firebase (nodig voor logout)
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_voorbeeld);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        //firebase authentication init
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        //toolbar toevoegen aan de layout
        Toolbar toolbar = findViewById(R.id.toolbar_schoolvoorbeeld);
        setSupportActionBar(toolbar);

        //menuknopje toevoegen aan de toolbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);


        //item tappen : zet het item op selected,  sluit de zijbar
        NavigationView navigationView = findViewById(R.id.nav_view);
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
                            startActivity(new Intent(SchoolVoorbeeldActivity.this, MainActivity.class));

                        }

                        // Add code here to update the UI based on the item selected
                        // hier komt de logica wat er moet gebeuren eenmaal je op een
                        // knop in de zijkantmenu duwt
                        // For example, swap UI fragments here

                        return true;
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
