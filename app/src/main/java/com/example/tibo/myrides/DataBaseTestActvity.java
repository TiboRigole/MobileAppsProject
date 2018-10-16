package com.example.tibo.myrides;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DataBaseTestActvity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_base_test_actvity);

        Button dbTest = (Button) findViewById(R.id.buttonDbTest);

        dbTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //LOGICA DIE IETS VERSTUURT NAAR DE DATABANK MOET HIER, MVG
            }
        });

    }
}
