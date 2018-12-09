package com.example.tibo.myrides;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.example.tibo.myrides.LoginTestFlavor.LoginTestActivity;
import com.squareup.okhttp.Response;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TestAsyncLoginFailure {

    private static final String TAG = TestAsyncLoginFailure.class.getSimpleName();

    @Rule
    public ActivityTestRule<LoginTestActivity> mActivityRule =
            new ActivityTestRule<>(LoginTestActivity.class);

    private LoginTestActivity testActivity = null;

    @Test
    public void testLoginSucces() throws Exception {

        testActivity = mActivityRule.getActivity();

        EditText dn = testActivity.findViewById(R.id.editTextDisplayName);
        EditText pw = testActivity.findViewById(R.id.editTextPaswoord);
        Button loginButton = testActivity.findViewById(R.id.logInButton);

        testActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dn.setText("testGebruiker");
                pw.setText("Test1234");
                loginButton.performClick();
            }
        });

        final Object syncObject = new Object();



        testActivity.setLoginCallback(new LoginTestActivity.Callback() {
            @Override
            public void onHandleResponseCalled(Response response) {
                Log.v("androidTest", "onHandleResponseCalled in thread " + Thread.currentThread().getId());
                Log.v("androidTest", "we zitten hier");

                assertTrue(response.code() != 200);
                synchronized (syncObject){
                    syncObject.notify();
                }
            }
        });


        synchronized (syncObject){
            syncObject.wait();
        }

    }



}
