package com.example.tibo.myrides;

import com.example.tibo.myrides.General.MainActivity;
import com.example.tibo.myrides.General.RegistreerFragment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;



/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class ExampleUnitTest {

    MainActivity mainActivity;
    RegistreerFragment registreerFragment;

    @Before
    public void setUp() {
        mainActivity = Robolectric.setupActivity(MainActivity.class);
        registreerFragment = new RegistreerFragment();
    }

    @Test
    public void testMainActivity(){
        Assert.assertNotNull(mainActivity);
    }
}