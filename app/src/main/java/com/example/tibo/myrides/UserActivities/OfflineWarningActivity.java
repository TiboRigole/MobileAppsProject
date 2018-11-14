package com.example.tibo.myrides.UserActivities;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.tibo.myrides.R;

public class OfflineWarningActivity extends AppCompatActivity {


    Button goToSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_warning);

        goToSettings= findViewById(R.id.goToNetworkConnection);

        goToSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(settingsIntent);
            }
        });
    }
}
