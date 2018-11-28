package com.example.tibo.myrides.UserActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.tibo.myrides.R;

public class OfflineWarningActivity extends AppCompatActivity {


    Button goToHomeAct;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_warning);

        goToHomeAct = findViewById(R.id.goToHome);

        goToHomeAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToHome= new Intent(OfflineWarningActivity.this, HomeActivity.class);
                startActivity(goToHome);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(false){
            super.onBackPressed();
        }
    }
}
