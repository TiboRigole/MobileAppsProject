package com.example.tibo.myrides.General;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.tibo.myrides.R;
import com.google.firebase.FirebaseApp;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;


import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class)
public class RegistreerActvityTest {

        /*lokaal maken we deze aan zodat we hierop kunnen testen
        paswoordLengteView = registreerActvity.findViewById(R.id.counterpaslength);
        registreerButton = registreerActvity.findViewById(R.id.registreerButton);
        paswoordText = registreerActvity.findViewById(R.id.paswoordEditView);
        bevestigPaswoordText = registreerActvity.findViewById(R.id.confirmPaswoordEditView);
        paswoordLengteView = registreerActvity.findViewById(R.id.counterpaslength);*/

    @Test
    public void isSafePaswoord(){

        RegistreerActvity activity = Robolectric.setupActivity(RegistreerActvity.class);

        EditText paswoordText = activity.findViewById(R.id.paswoordEditView);
        EditText bevestigPaswoordText = activity.findViewById(R.id.confirmPaswoordEditView);
        Button registreerButton = activity.findViewById(R.id.registreerButton);

        paswoordText.setText("VeiligPW1234");
        bevestigPaswoordText.setText("VeiligPW1234");

        // de paswoorden zijn gelijk, we moeten dus kunnen registreren
        Assert.assertTrue(registreerButton.isEnabled());
    }

    @Test
    public void isNotSamePaswoord(){
        RegistreerActvity activity = Robolectric.setupActivity(RegistreerActvity.class);

        EditText paswoordText = activity.findViewById(R.id.paswoordEditView);
        EditText bevestigPaswoordText = activity.findViewById(R.id.confirmPaswoordEditView);
        Button registreerButton = activity.findViewById(R.id.registreerButton);

        paswoordText.setText("VeiligPW1234");
        bevestigPaswoordText.setText("VeiligPW14");

        //de passwoorden zijn niet gelijk, dus de knop mag niet enabled zijn
        Assert.assertTrue(!registreerButton.isEnabled());
    }

}