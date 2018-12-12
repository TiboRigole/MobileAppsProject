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

@RunWith(AndroidJUnit4.class)
public class ServiceTestInMainActivity {

    @Rule
    public final ActivityTestRule<MainActivity> myActivityTestRule =
            new ActivityTestRule<>(MainActivity.class );

    private Context context;
    private ActivityManager manager;

    @Before
    public void setUp() throws Exception {

        //get some values
        context = myActivityTestRule.getActivity();
        manager = (ActivityManager) myActivityTestRule.getActivity().getSystemService(context.ACTIVITY_SERVICE);


    }

    @Test
    public void serviceActiveInMainActivity(){

        //checks if the service is running in this activity

        boolean result = false;

        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if (MyService.class.getName().equals((service.service.getClassName()))){
                result =  true;
            }
        }

        Assert.assertTrue(result);

    }

}