package com.example.tibo.myrides.UserActivities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import com.example.tibo.myrides.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SummaryActivity extends AppCompatActivity implements OnMapReadyCallback {

    boolean heenenterug;
    boolean computed=false;

    //minimap
    private MapView mapView;
    private GoogleMap gmap;
    private static final String MAP_VIEW_BUNDLE_KEY = "AIzaSyCC6_SCEF4zQaG5fbR-WyWKEEImFycQWsI";
    LatLng center;
    LatLng sourceLatLng;
    LatLng destLatLng;

    int meter;
    double kilometer;
    double verbruik; // liter per km
    double prijsNafte; // €/l
    double resultaat;
    String einde;
    String start;


    TextView source;
    TextView destination;


    TextView nummerPlaatTextView;


   // protected static CustomSharedPreference mPref;
    DecimalFormat df = new DecimalFormat("#.##");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        Bundle bundle= getIntent().getExtras();
        start= bundle.getString("vertrek");
        einde= bundle.getString("aankomst");
        center= (LatLng) bundle.get("center");
        sourceLatLng=(LatLng) bundle.get("sourceLatLng");
        destLatLng=(LatLng)bundle.get("destLatLng");

        source= (TextView) findViewById(R.id.source);
        destination= (TextView) findViewById(R.id.destination);

        nummerPlaatTextView=findViewById(R.id.nummerPlaat);
        nummerPlaatTextView.setTextSize(20);
        nummerPlaatTextView.setText(bundle.getString("nummerplaat"));

        source.setText(start);
        destination.setText(einde);


        // nu worden de berekeningen gedaan
        try {
            parseResults();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        CheckBox retour=(CheckBox) findViewById(R.id.retour);




        //toolbar toevoegen aan de layout (nodig om de menuknop te hebben, die het zijkantmenu oppopt
        Toolbar toolbar = findViewById(R.id.toolbar_SummaryActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Overzicht rit");


        Button save= (Button)findViewById(R.id.save);
        save.setOnClickListener((v)->{
            if(computed) {
                try {

                    heenenterug=retour.isChecked();
                    saveStripedJsonObject(start, einde, kilometer, prijsNafte, resultaat, heenenterug);
                    Intent intent= new Intent(SummaryActivity.this, HomeActivity.class);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "Er werd nog niets berekend om op te slaan", Toast.LENGTH_LONG).show();
            }


        });



        // minimap
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.mapRoute);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

// Needs to call MapsInitializer before doing any CameraUpdateFactory calls

            MapsInitializer.initialize(mapView.getContext());

    }


    public void parseResults() throws JSONException {

        // berekenen van resultaten
        Bundle bundle= getIntent().getExtras();

        // kilometer uit vorige activity
        kilometer= Double.parseDouble(bundle.getString("distance")); // km

        // @TODO: hardcoded values nog handlen per auto
        verbruik=0.055; // liter per km
        prijsNafte= 1.45;
        resultaat= kilometer*verbruik*prijsNafte;


        // visualiseren
        visualiseerResultaten(kilometer, verbruik, prijsNafte, resultaat);



        computed=true;

    }

    private void visualiseerResultaten(double kilometer, double verbruik, double prijsNafte, double resultaat) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {


                TextView kilometerText= findViewById(R.id.kilometers);
                TextView verbruikText= (TextView)findViewById(R.id.verbruik);
                TextView prijsNafteText= (TextView)findViewById(R.id.prijsNafte);
                TextView resultaatText= (TextView)findViewById(R.id.prijs);

                kilometerText.setText(df.format(kilometer)+ " km");
                verbruikText.setText(Double.toString(verbruik)+ " l/km");
                prijsNafteText.setText(Double.toString(prijsNafte)+ " €/km");

                resultaatText.setText(df.format(resultaat) + " €");

            }
        });

    }


    private void saveStripedJsonObject(String start, String einde, double kilometer, double prijsNafte, double resultaat, boolean heenenterug) throws JSONException {
        // @TODO: opslaan bij huidige gebruiker in firebase

        JSONObject saveObject= new JSONObject();


        if(heenenterug){
            kilometer=kilometer*2;
            resultaat=resultaat*2;
        }

        saveObject.put("vertrekpunt", start);
        saveObject.put("bestemming", einde);
        saveObject.put("afstand", kilometer);
        saveObject.put("prijsnafte", prijsNafte);
        saveObject.put("resultaatInEuro", resultaat);
        saveObject.put("heenenterug", heenenterug);

        //mPref=((CustomApplication)getApplication()).getShared();


        //mPref.addRittenData(saveObject);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMinZoomPreference(9);
        gmap.addMarker(new MarkerOptions().position(sourceLatLng).title(start).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        gmap.addMarker(new MarkerOptions().position(destLatLng).title(einde).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        gmap.moveCamera(CameraUpdateFactory.newLatLng(center));



    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}