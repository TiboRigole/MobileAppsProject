package com.example.tibo.myrides.TestingClasses;

import com.example.tibo.myrides.General.InlogActivity;
import com.squareup.okhttp.Response;

//deze activity bestaat enkel voor het runnen van de JUnit test
public class LoginTestActivity extends InlogActivity {

    private Callback mCallback;

    public void setLoginCallback(Callback callback){
        mCallback = callback;
    }

    public interface Callback{
        void onHandleResponseCalled(Response response);
    }

    @Override
    public void handleLoginResponse(Response response){
        mCallback.onHandleResponseCalled(response);
    }



}
