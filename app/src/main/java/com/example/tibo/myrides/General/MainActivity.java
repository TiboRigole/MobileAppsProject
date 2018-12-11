package com.example.tibo.myrides.General;


import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.tibo.myrides.Entities.CurrentUser;
import com.example.tibo.myrides.HelperPackage.MyService;
import com.example.tibo.myrides.HelperPackage.NetworkChangeReceiver;
import com.example.tibo.myrides.R;
import com.example.tibo.myrides.UserActivities.HomeActivity;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private ImageButton infoButton;
    private Button inlogButton;
    private Button registreerButton;
    private BroadcastReceiver br;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager= getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        InlogFragment inlogFragment= new InlogFragment();
        fragmentTransaction.add(R.id.fragmentContainer, inlogFragment, "inlog");
        fragmentTransaction.commit();


        startService(new Intent(this, MyService.class));
        // broadcastreceiver
        br= new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(br, filter);

        //infoButton init
        infoButton = (ImageButton) findViewById(R.id.infoImageButton);

        //inlogButton init
        inlogButton = (Button)findViewById(R.id.inlogButton);

        //registreerButton init
        registreerButton = (Button)findViewById(R.id.naarRegistreerPaginaButton);

        //infoButton logica
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
            }
        });

        //inlogButton logica
        inlogButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager= getSupportFragmentManager();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                InlogFragment inlogFragment= new InlogFragment();
                fragmentTransaction.replace(R.id.fragmentContainer, inlogFragment, "inlog");
                fragmentTransaction.commit();
                //startActivity(new Intent(MainActivity.this, InlogFragment.class));
            }
        });

        //registreerButton logica
        registreerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("registreer frame");
                FragmentManager fragmentManager= getSupportFragmentManager();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                RegistreerFragment registreerFragment= new RegistreerFragment();
                fragmentTransaction.replace(R.id.fragmentContainer, registreerFragment, "registreer");
                fragmentTransaction.commit();

                //startActivity(new Intent(MainActivity.this, RegistreerFrame.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        AccessToken accessToken= AccessToken.getCurrentAccessToken();
        if(accessToken!=null && !accessToken.isExpired()){
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            Log.v("LoginActivity", response.toString());

                            // Application code
                            try {
                                //object zal null zijn als er geen connectie met internet is
                                if(object!=null) {
                                    String email = object.getString("email");
                                    String displayName = object.getString("name"); // 01/31/1980 format


                                    CurrentUser.getInstance().setDisplayName(displayName);
                                    CurrentUser.getInstance().setEmail(email);
                                    CurrentUser.getInstance().setLoggedIn(true);
                                    updateUIAfterLogin(CurrentUser.getInstance());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender,birthday");
            request.setParameters(parameters);
            request.executeAsync();

        }
        else{
            CurrentUser.getInstance().disconnectFromFacebook();
            updateUIAfterLogin(CurrentUser.getInstance());
        }

    }

    @Override
    protected void onPause() {
        Log.i("activitylifecycle","onStop triggered");
        stopService(new Intent(this, MyService.class));
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i("activitylifecycle","onStop triggered");
        stopService(new Intent(this, MyService.class));
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i("activitylifecycle","onDestroy triggered");
        stopService(new Intent(this, MyService.class));
        super.onDestroy();

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        Log.i("activitylifecycle","onSaveInstanceState triggered");
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onBackPressed() {
        if(false){
            super.onBackPressed();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("FBLOGIN", "onactivityResult binnengekomen!");
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("inlog");
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    private void updateUIAfterLogin(CurrentUser user) {
        if (user.isLoggedIn()) {
            startActivity(new Intent(MainActivity.this,  HomeActivity.class));
        }

    }
}
