package com.example.tibo.myrides.UserActivities;

import android.app.ActivityManager;
import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.tibo.myrides.General.MainActivity;
import com.example.tibo.myrides.HelperPackage.MyService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

//hierin gaan we testen als mp3 spelende service niet actief is in de ServiceInHomeActivityTest

@RunWith(AndroidJUnit4.class)
public class ServiceInHomeActivityTest {

    @Rule
    public final ActivityTestRule<HomeActivity> myActivityTestRule =
            new ActivityTestRule<>(HomeActivity.class );

    private Context context;
    private ActivityManager manager;

    @Before
    public void setUp() throws Exception {

        //get some values
        context = myActivityTestRule.getActivity();
        manager = (ActivityManager) myActivityTestRule.getActivity().getSystemService(context.ACTIVITY_SERVICE);


    }

    @Test
    public void serviceActiveInIngelogdeActivity(){

        //checks if the service is running in this activity

        boolean result = false;

        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if (MyService.class.getName().equals((service.service.getClassName()))){
                result =  true;
            }
        }

        Assert.assertFalse(result);

    }
}