package com.example.tibo.myrides.UserActivities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tibo.myrides.Entities.Rit;
import com.example.tibo.myrides.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


import org.json.JSONException;

import java.text.DecimalFormat;

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


    //firebase authentication handler
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    //firebase database handler
    FirebaseFirestore db;


   // protected static CustomSharedPreference mPref;
    DecimalFormat df = new DecimalFormat("#.##");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        //firebase database init
        db = FirebaseFirestore.getInstance();

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
                    saveRitInDB(start, einde, kilometer, prijsNafte, resultaat, heenenterug);
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


    private void saveRitInDB(String start, String einde, double kilometer, double prijsNafte, double resultaat, boolean heenenterug) throws JSONException {

        if(heenenterug){
            kilometer=kilometer*2;
            resultaat=resultaat*2;
        }
        Task<QuerySnapshot> query= db.collection("autos").whereEqualTo("kenteken", nummerPlaatTextView.getText().toString()).get();
        double finalResultaat = resultaat;
        double finalKilometer = kilometer;
        query.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {

                    String eigenaarAuto=documentSnapshot.get("eigenaar").toString();
                    Rit rit= new Rit(currentUser.getEmail(),eigenaarAuto, start, einde, nummerPlaatTextView.getText().toString(), finalKilometer, prijsNafte, finalResultaat, heenenterug);

                    db.collection("ritten")
                            .add(rit)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d("VALUES","DocumentSnapchat added with ID: "+ documentReference.getId());

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("VALUES", "Error adding document: "+ e);
                                }
                            });


                }
            }
        });
        query.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });




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