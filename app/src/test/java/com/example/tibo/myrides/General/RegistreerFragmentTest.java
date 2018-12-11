package com.example.tibo.myrides.General;

import android.widget.Button;
import android.widget.EditText;

import com.example.tibo.myrides.R;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(RobolectricTestRunner.class)
public class RegistreerFragmentTest {

        /*lokaal maken we deze aan zodat we hierop kunnen testen
        paswoordLengteView = registreerActvity.findViewById(R.id.counterpaslength);
        registreerButton = registreerActvity.findViewById(R.id.registreerButton);
        paswoordText = registreerActvity.findViewById(R.id.paswoordEditView);
        bevestigPaswoordText = registreerActvity.findViewById(R.id.confirmPaswoordEditView);
        paswoordLengteView = registreerActvity.findViewById(R.id.counterpaslength);*/

    @Test
    public void isSafePaswoord(){

        RegistreerFragment activity = Robolectric.setupActivity(RegistreerFragment.class);

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
        RegistreerFragment activity = Robolectric.setupActivity(RegistreerFragment.class);

        EditText paswoordText = activity.findViewById(R.id.paswoordEditView);
        EditText bevestigPaswoordText = activity.findViewById(R.id.confirmPaswoordEditView);
        Button registreerButton = activity.findViewById(R.id.registreerButton);

        paswoordText.setText("VeiligPW1234");
        bevestigPaswoordText.setText("VeiligPW14");

        //de passwoorden zijn niet gelijk, dus de knop mag niet enabled zijn
        Assert.assertTrue(!registreerButton.isEnabled());
    }

}