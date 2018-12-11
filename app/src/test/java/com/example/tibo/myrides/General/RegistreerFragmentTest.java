package com.example.tibo.myrides.General;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.tibo.myrides.BuildConfig;
import com.example.tibo.myrides.R;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static junit.framework.TestCase.assertTrue;
import static org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment;

@RunWith(RobolectricOverrider.class)
public class RegistreerFragmentTest {

    @Test
    public void isSafePaswoord(){

        RegistreerFragment registreerFragment = new RegistreerFragment();

        startFragment( registreerFragment);

        LayoutInflater inflater = registreerFragment.getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_registreer, null);

        EditText paswoordText = (EditText)  view.findViewById(R.id.paswoordEditView);
        EditText bevestigPaswoordText = (EditText) view.findViewById(R.id.confirmPaswoordEditView);
        Button registreerButton = (Button) view.findViewById(R.id.registreerButton);

        paswoordText.setText("VeiligPW1234");
        bevestigPaswoordText.setText("VeiligPW1234");

        // de paswoorden zijn gelijk, we moeten dus kunnen registreren
        assertTrue(registreerButton.isEnabled());
    }

    /*@Test
    public void isNotSamePaswoord(){
        RegistreerFragment activity = Robolectric.setupActivity(RegistreerFragment.class);

        EditText paswoordText = activity.findViewById(R.id.paswoordEditView);
        EditText bevestigPaswoordText = activity.findViewById(R.id.confirmPaswoordEditView);
        Button registreerButton = activity.findViewById(R.id.registreerButton);

        paswoordText.setText("VeiligPW1234");
        bevestigPaswoordText.setText("VeiligPW14");

        //de passwoorden zijn niet gelijk, dus de knop mag niet enabled zijn
        Assert.assertTrue(!registreerButton.isEnabled());
    }*/

}