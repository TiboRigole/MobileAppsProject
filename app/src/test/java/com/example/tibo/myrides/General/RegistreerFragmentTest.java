package com.example.tibo.myrides.General;

import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.EditText;

import com.example.tibo.myrides.R;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.FragmentTestUtil;

import static org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startVisibleFragment;

@RunWith(RobolectricTestRunner.class)
public class RegistreerFragmentTest {

    private RegistreerFragment fragment;

    EditText paswoordText;
    EditText bevestigPaswoordText;
    Button registreerButton;


    @Before
    public void setUp() throws Exception {

        fragment = new RegistreerFragment();

        startVisibleFragment(fragment);

        paswoordText = fragment.getActivity().findViewById(R.id.paswoordEditView);
        bevestigPaswoordText = fragment.getActivity().findViewById(R.id.confirmPaswoordEditView);
        registreerButton = fragment.getActivity().findViewById(R.id.registreerButton);

    }

    @Test
    public void exists() throws Exception{
        Assert.assertNotNull(fragment);
    }

    @Test
    public void editTextVindbaar() throws Exception{
        Assert.assertTrue(paswoordText.isEnabled());
    }

    @Test
    public void correctPaswoord() throws Exception{
        paswoordText.setText("VeiligPW1234");
        bevestigPaswoordText.setText("VeiligPW1234");

        //de passwoorden zijn gelijk, dus de knop mag moet enabled zijn
        Assert.assertTrue(registreerButton.isEnabled());
    }

    @Test
    public void foutPaswoord() throws Exception{
        paswoordText.setText("VeiligPW1234");
        bevestigPaswoordText.setText("VeiligPW14");

        //de passwoorden zijn niet gelijk, dus de knop mag niet enabled zijn
        Assert.assertTrue(!registreerButton.isEnabled());
    }

}