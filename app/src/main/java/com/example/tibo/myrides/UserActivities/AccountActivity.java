package com.example.tibo.myrides.UserActivities;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.tibo.myrides.Entities.CurrentUser;
import com.example.tibo.myrides.HelperPackage.CustomNavigationView;
import com.example.tibo.myrides.HelperPackage.NetworkChangeReceiver;
import com.example.tibo.myrides.R;

public class AccountActivity extends AppCompatActivity {

    TextView name;
    TextView email;
    TextView loggedIn;
    DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mDrawerLayout=findViewById(R.id.drawer_layout_account);
        //toolbar toevoegen aan de layout (nodig om de menuknop te hebben, die het zijkantmenu opkomt)
        Toolbar toolbar = findViewById(R.id.toolbar_AccountActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mijn Account");

        //menuknop toevoegen aan de toolbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        //item tappen : zet het item op selected,  sluit de zijbar
        CustomNavigationView navigationView = (CustomNavigationView) findViewById(R.id.navigationzijkant_view);
        navigationView.setCheckedItem(R.id.nav_info);
        navigationView.initSelect(this, mDrawerLayout);




        name= findViewById(R.id.name);
        email= findViewById(R.id.email);
        loggedIn=findViewById(R.id.loggedin);

        name.setText(CurrentUser.getInstance().getDisplayName());
        email.setText(CurrentUser.getInstance().getEmail());
        if(CurrentUser.getInstance().isLoggedIn()){
            loggedIn.setText("ingelogd");
        }
        else{
            loggedIn.setText("niet ingelogd");
        }
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

    @Override
    public void onBackPressed() {
       if(false){
           super.onBackPressed();
       }
    }


}
