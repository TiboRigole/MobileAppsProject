package com.example.tibo.myrides.General;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;

public class RobolectricOverrider extends RobolectricTestRunner {

    public RobolectricOverrider (Class<?> testClass) throws InitializationError {
        super( testClass);
    }

    public static void startFragment( Fragment fragment){
        FragmentManager fragmentManager = new FragmentActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add( fragment,null);
        fragmentTransaction.commit();
    }

}