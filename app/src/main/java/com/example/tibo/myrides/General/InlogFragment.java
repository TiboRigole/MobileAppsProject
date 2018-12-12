package com.example.tibo.myrides.General;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tibo.myrides.Entities.CurrentUser;
import com.example.tibo.myrides.R;
import com.example.tibo.myrides.RoomPackage.AnApplication;
import com.example.tibo.myrides.RoomPackage.AppDatabase;
import com.example.tibo.myrides.RoomPackage.RitLocal;
import com.example.tibo.myrides.UserActivities.HomeActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InlogFragment extends Fragment {





    FirebaseFirestore db;

    // ROOM
    AnApplication application;
    static AppDatabase mDatabase;


    //manager voor facebook login
    private CallbackManager callbackManager;
    private LoginButton fbloginButton;

    //email en paswoord textviews
    private TextView usernameTextView;
    private TextView paswoordTextView;

    //login button
    private Button logInButton;

    private Button goBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        Log.i("activitylifecycle","onCreate triggered");





        return inflater.inflate(R.layout.activity_inlog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        db=FirebaseFirestore.getInstance();

        // ROOM
        application = (AnApplication) getActivity().getApplication();
        mDatabase = application.getDatabase();

        //GUI
        //init buttons
        fbloginButton = getView().findViewById(R.id.fb_login_button);
        logInButton = (Button) getView().findViewById(R.id.logInButton);

        //init textviews
        usernameTextView = getView().findViewById(R.id.editTextDisplayName);
        paswoordTextView = getView().findViewById(R.id.editTextPaswoord);

        //logica buttons

        //logica inlogbutton met email adres en paswoord
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = usernameTextView.getText().toString();
                String password = paswoordTextView.getText().toString();

                if (!email.equals("") && !password.equals("")) {

                    hideKeyBoard();
                    loginWithDisplayName(email, password);

                }
            }
        });



        //FACEBOOK
        //init manager to handle facebook manager
        callbackManager = CallbackManager.Factory.create();

        // facebook logica
        fbloginButton.setReadPermissions("email", "public_profile");
        Log.d("FBLOGIN","readpermissions set");

        // inlog callback na button press
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("FBLOGIN", "facebook:onSuccess: " + loginResult);

                // EIGEN SERVER

                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                // Application code
                                try {
                                    String email = object.getString("email");
                                    String displayName = object.getString("name"); // 01/31/1980 format

                                    // registreer user met facebook
                                    registerAndLoginFacebookUser(displayName, email);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();



                // VOOR FIREBASE INLOGMETHODE
                // handleFacebookAccessToken(loginResult.getAccessToken());
                // go to next page?
            }

            @Override
            public void onCancel() {
                Log.d("FBLOGIN", "facebook:onCancel");
                Toast.makeText(getActivity(),"facebook login gecanceld!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("FBLOGIN", "facebook:onError", error);
                Toast.makeText(getActivity(),"facebook login error!", Toast.LENGTH_SHORT).show();

                //updateUI(null);

            }
        });

        //vanuit de registreerpagina komt een intent
        Bundle bundle = getActivity().getIntent().getExtras();
        if(bundle.get("username")!= null){
            usernameTextView.setText(bundle.get("username").toString());
        }

        SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String userString = myPref.getString("username","");
        if(!userString.equals("")){usernameTextView.setText(userString);}



        super.onViewCreated(view, savedInstanceState);
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onStart() {
        super.onStart();
        AccessToken accessToken= AccessToken.getCurrentAccessToken();
        if(accessToken!=null && !accessToken.isExpired()){
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            Log.v("LoginActivity", response.toString());

                            // Application code
                            try {
                                //object zal null zijn als er geen connectie met internet is
                                if(object!=null) {
                                    String email = object.getString("email");
                                    String displayName = object.getString("name"); // 01/31/1980 format


                                    CurrentUser.getInstance().setDisplayName(displayName);
                                    CurrentUser.getInstance().setEmail(email);
                                    CurrentUser.getInstance().setLoggedIn(true);
                                    updateUIAfterLogin(CurrentUser.getInstance());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender,birthday");
            request.setParameters(parameters);
            request.executeAsync();

        }
        else{
            CurrentUser.getInstance().disconnectFromFacebook();
            updateUIAfterLogin(CurrentUser.getInstance());
        }


    }



    /**
     * login aan de hand van een displayname
     * @param displayName displayname
     * @param password paswoord
     */
    public void loginWithDisplayName(String displayName, String password){

        OkHttpClient client = new OkHttpClient();

        try {

            // aanmaken body van request
            JSONObject jsonString = new JSONObject()
                    .put("displayName", displayName)
                    .put("password", password);



            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonString.toString());
            Request request = new Request.Builder()
                    .url("https://distributedsystemsprojec-bd15e.firebaseapp.com/login")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("Postman-Token", "a2ac0538-d987-4f5c-8795-5a199e0ef4fd")
                    .build();
            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Request request, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Response response) throws IOException {

                    System.out.println("response code "+ response.code());
                    if(response.code()==200) {
                        // login ok, set current user local
                        CurrentUser.getInstance().setEmail(response.body().string());
                        CurrentUser.getInstance().setDisplayName(displayName);
                        CurrentUser.getInstance().login();
                        saveOfflineUser();




                        updateUIAfterLogin(CurrentUser.getInstance());
                    }
                    else{
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Toast.makeText(getActivity(), response.body().string(), Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    }
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * registreer de user die met facebook is ingelogd
     * @param displayName name van facebookuser
     * @param email email adres gekoppeld aan facebookuser
     */
    private void registerAndLoginFacebookUser(String displayName, String email){
        try {
            // set current user local
            CurrentUser.getInstance().setDisplayName(displayName);
            CurrentUser.getInstance().setEmail(email);
            CurrentUser.getInstance().login();
            saveOfflineUser();

            // maak account aan op server voor user die inlogt met facebook
            JSONObject jsonString = new JSONObject()
                    .put("displayName", displayName)
                    .put("email", email)
                    .put("password", " ");

            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonString.toString());
            Request request = new Request.Builder()
                    .url("https://distributedsystemsprojec-bd15e.firebaseapp.com/registreer")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("Postman-Token", "69946e15-6cdd-46f5-9521-7acba52858bb")
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {

                }
            });


            updateUIAfterLogin(CurrentUser.getInstance());
        }
        catch (JSONException je){
            je.printStackTrace();
        }

    }


    private void saveOfflineUser(){
        SharedPreferences myPref= PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences.Editor myEditor=myPref.edit();
        JSONObject jsonUserPref= null;
        try {
            jsonUserPref = new JSONObject()
                    .put("displayName", CurrentUser.getInstance().getDisplayName())
                    .put("email", CurrentUser.getInstance().getEmail());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        myEditor.putString("user", jsonUserPref.toString());
        myEditor.putString("username", CurrentUser.getInstance().getDisplayName());
        myEditor.apply();

        ArrayList<RitLocal> ritten= new ArrayList<RitLocal>();
        Task<QuerySnapshot> queryRitten= db.collection("ritten").whereEqualTo("eigenaarAuto", CurrentUser.getInstance().getEmail()).get();
        queryRitten.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                    ritten.add(new RitLocal(queryDocumentSnapshot.getData()));
                }

                // TODO sla alle ritten in arraylist op in ROOM database,  verwijder vorige
                new ritUpdate().execute(ritten);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("FBLOGIN", "onactivityResult binnengekomen in fragment!");
        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private static class ritUpdate extends AsyncTask<List<RitLocal>, Void, Void>
    {
        @Override
        protected Void doInBackground(List<RitLocal>... lists) {
            mDatabase.ritDao().deleteAllRitten();
            mDatabase.ritDao().insertRitten(lists[0]);
            return null;
        }
    }


    /**
     * als er reeds een user is ingelogd dient de loginpagina niet weergegeven te worden
     * @param user
     */
    private void updateUIAfterLogin(CurrentUser user) {
        if (user.isLoggedIn()) {
            startActivity(new Intent(getActivity(), HomeActivity.class));
        }

    }


    //volgende 2 methoden hebben als doel het onthouden van het email adres dat de gebruiker had
    // invoked when the activity may be temporarily destroyed, save the instance state here
    @Override
    public void onSaveInstanceState(Bundle outState) {

        Log.i("activitylifecycle","onSaveInstance triggered");
        outState.putString("username", usernameTextView.getText().toString());
        Log.i("activitylifecycle","username added");

        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }




}