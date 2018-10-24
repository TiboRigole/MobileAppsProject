package com.example.tibo.myrides.UserActivities;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.tibo.myrides.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONObject;
import com.example.tibo.myrides.PlaceAutocompleteAdapter;

public class AddDriveActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));


    AutoCompleteTextView source;
    AutoCompleteTextView destination;

    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drive);

        source = (AutoCompleteTextView) findViewById(R.id.source);
        destination = (AutoCompleteTextView) findViewById(R.id.destination);


        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, LAT_LNG_BOUNDS, null);
        source.setAdapter(mPlaceAutocompleteAdapter);
        destination.setAdapter(mPlaceAutocompleteAdapter);


        //toolbar toevoegen aan de layout (nodig om de menuknop te hebben, die het zijkantmenu oppopt
        Toolbar toolbar = findViewById(R.id.toolbar_AddDriveActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Rit toevoegen");

        CheckBox retour = (CheckBox) findViewById(R.id.retour);

        Button routes = (Button) findViewById(R.id.routes);
        routes.setOnClickListener((v) -> {
            System.out.println("source tekst" + source.getText().toString());
            if (!source.getText().toString().equals("") || !destination.getText().toString().equals("")) {
                Intent intent = new Intent(AddDriveActivity.this, MapsRouteActivity.class);
                intent.putExtra("vertrek", source.getText().toString());
                intent.putExtra("aankomst", destination.getText().toString());
                startActivity(intent);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "vul adressen in", Toast.LENGTH_LONG);
                toast.show();
            }
        });


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}