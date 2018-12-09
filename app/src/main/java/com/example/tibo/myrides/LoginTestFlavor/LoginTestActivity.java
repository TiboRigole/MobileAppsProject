package com.example.tibo.myrides.LoginTestFlavor;

import com.example.tibo.myrides.General.InlogActivity;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Response;

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
