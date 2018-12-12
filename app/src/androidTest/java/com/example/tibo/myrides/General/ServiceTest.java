package com.example.tibo.myrides.General;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.ServiceTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.example.tibo.myrides.HelperPackage.MyService;
import com.example.tibo.myrides.R;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeoutException;

import androidx.test.espresso.Espresso;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ServiceTest {

    @Rule
    public final ActivityTestRule<MainActivity> myActivityTestRule =
            new ActivityTestRule<>(MainActivity.class );


    @Before
    public void setUp() throws TimeoutException {

        //get the service from the activity
    }

    @Test
    public void serviceActive(){
        //checks if the service is running

        Context context = myActivityTestRule.getActivity();
        ActivityManager manager = (ActivityManager) myActivityTestRule.getActivity().getSystemService(context.ACTIVITY_SERVICE);

        boolean result = false;

        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if (MyService.class.getName().equals((service.service.getClassName()))){
                result =  true;
            }
        }

        Log.d("servicetest","result = "+result);

        Assert.assertTrue(result);

    }

    //om nu een fragment te openen :
    /*
        All you have to do is to tell the JUnit that you need to perform
        an operation first before running your tests.
        This is done by making a function, let’s call it “setUp()”,
        and annotating it with @Before annotations.

        To change the fragment, you need to get the fragment manager and in order to do that,
         you need to get the activity. All this can be done using the rule object we created:

     */

}