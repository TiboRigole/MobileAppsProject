package com.example.tibo.myrides.General;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.tibo.myrides.R;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class)
public class RegistreerActvityTest {

    // we maken hier variabelen lokaal aan, zodat we ze kunnen makkelijker testen
    TextWatcher paswoordCounter;
    Button registreerButton;
    EditText paswoordText;
    EditText bevestigPaswoordText;
    TextView paswoordLengteView;
    RegistreerActvity registreerActvity = new RegistreerActvity();


    @Before
    public void setUp() throws Exception {


        //lokaal maken we deze aan zodat we hierop kunnen testen
        paswoordLengteView = registreerActvity.findViewById(R.id.counterpaslength);
        registreerButton = registreerActvity.findViewById(R.id.registreerButton);
        paswoordText = registreerActvity.findViewById(R.id.paswoordEditView);
        bevestigPaswoordText = registreerActvity.findViewById(R.id.confirmPaswoordEditView);
        paswoordLengteView = registreerActvity.findViewById(R.id.counterpaslength);


        //dit is een blok blok code uit registreeractivity
        paswoordCounter= new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //paswoordLengteView.setText(String.valueOf(s.length()));
                if (s.length() >= 6) {
                    paswoordLengteView.setTextColor(Color.parseColor("#dac438"));
                    if (bevestigPaswoordText.getText().toString().equals(paswoordText.getText().toString())) {
                        registreerButton.setEnabled(true);
                    }
                } else if (s.length() == 0) {
                    paswoordLengteView.setTextColor(Color.parseColor("#20FFFFFF"));
                    registreerButton.setEnabled(false);
                } else {
                    paswoordLengteView.setTextColor(Color.RED);
                    registreerButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };




    }

    @Test
    public void doTest() throws Exception {
        paswoordText.setText("veiligPaswoord123");
        bevestigPaswoordText.setText("veiligPaswoord123");

        Assert.assertTrue(registreerButton.isEnabled());
    }


    @After
    public void tearDown() throws Exception {
    }
}