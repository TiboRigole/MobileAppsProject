package com.example.tibo.myrides.General;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.tibo.myrides.HelperPackage.MyService;
import com.example.tibo.myrides.HelperPackage.NetworkChangeReceiver;
import com.example.tibo.myrides.R;


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
                startActivity(new Intent(MainActivity.this, InlogActivity.class));
            }
        });

        //registreerButton logica
        registreerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegistreerActvity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("stop service");
        stopService(new Intent(this, MyService.class));
    }


    @Override
    public void onBackPressed() {
        if(false){
            super.onBackPressed();
        }
    }


}
