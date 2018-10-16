package com.example.tibo.myrides;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //button voor naar de about page
        Button naarAbout = (Button)findViewById(R.id.buttonAbout);

        naarAbout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                //begin van de app
            }
        });



        //button voor naar de inlog keuze page
        Button naarKeuzeInloggen = (Button)findViewById(R.id.buttonInloggen);

        naarKeuzeInloggen.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, InloggenKeuzeActivity.class));
            }
        });
    }


}
