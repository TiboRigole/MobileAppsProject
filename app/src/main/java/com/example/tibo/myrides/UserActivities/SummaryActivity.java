package com.example.tibo.myrides.UserActivities;


import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tibo.myrides.Entities.CurrentUser;
import com.example.tibo.myrides.Entities.Rit;
import com.example.tibo.myrides.HelperPackage.NetworkChangeReceiver;
import com.example.tibo.myrides.HelperPackage.PassPolyline;
import com.example.tibo.myrides.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class SummaryActivity extends AppCompatActivity implements OnMapReadyCallback {

    // STATUS
    private boolean computed=false;

    // INIT MINIMAP
    private MapView mapView;
    private GoogleMap gmap;
    private static final String MAP_VIEW_BUNDLE_KEY = "AIzaSyCC6_SCEF4zQaG5fbR-WyWKEEImFycQWsI";
    private LatLng center;
    private  LatLng sourceLatLng;
    private  LatLng destLatLng;


    // EIGENSCHAPPEN RIT
    private double kilometer;
    private double verbruik; // liter per km
    private double prijsNafte; // €/l
    private double resultaat;
    private String einde;
    private String start;
    private String nummerplaat;
    private boolean heenenterug;
    private Polyline polyline;


    // INIT LAYOUT
    private TextView source;
    private TextView destination;
    private TextView nummerPlaatTextView;
    private CheckBox retour;
    private Button save;


    // INIT FIREBASE
    private FirebaseFirestore db;

    private BroadcastReceiver br;

    private CurrentUser currentUser;

    // FORMAT DOUBLES
    private DecimalFormat df = new DecimalFormat("#.##");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        // broadcastreceiver
        br= new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(br, filter);

        // DEF FIREBASE
        currentUser = CurrentUser.getInstance();
        db = FirebaseFirestore.getInstance();

        // GET RIT EIGENSCHAPPEN
        Bundle bundle= getIntent().getExtras();
        start= bundle.getString("vertrek");
        einde= bundle.getString("aankomst");
        center= (LatLng) bundle.get("center");
        sourceLatLng=(LatLng) bundle.get("sourceLatLng");
        destLatLng=(LatLng)bundle.get("destLatLng");
        kilometer= Double.parseDouble(bundle.getString("distance")); // km


        // DEF LAYOUT
        source= (TextView) findViewById(R.id.source);
        destination= (TextView) findViewById(R.id.destination);
        nummerplaat=bundle.getString("nummerplaat");
        nummerPlaatTextView=findViewById(R.id.nummerPlaat);
        nummerPlaatTextView.setTextSize(20);
        nummerPlaatTextView.setText(nummerplaat);
        source.setText(start);
        destination.setText(einde);
        retour=(CheckBox) findViewById(R.id.retour);
        save= (Button)findViewById(R.id.save);



        // LOGIC BUTTONS AND WIDGETS
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



        // DEF MINIMAP
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mapView = findViewById(R.id.mapRoute);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(mapView.getContext());

        // COMPUTATIONS
        try {
            parseResults();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void parseResults() throws JSONException {

        // vraag auto op waar kenteken gelijk is aan nummerplaat => verkrijgen van verbruik en benzinetype
        Task<QuerySnapshot> query= db.collection("autos").whereEqualTo("kenteken", nummerplaat).get();
        query.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    verbruik=documentSnapshot.getDouble("verbruik");

                    //@TODO prijs nafte uit een api ofzo halen
                    String typeNafte=documentSnapshot.getString("benzineType");
                    prijsNafte= 1.45;

                    resultaat= kilometer*verbruik*prijsNafte;
                    // visualiseren
                    visualiseerResultaten(kilometer, verbruik, prijsNafte, resultaat);
                    computed=true;
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

    /**
     * visualiseren van de resultaten (meegegeven in de parameters van deze methode)
     * @param kilometer
     * @param verbruik
     * @param prijsNafte
     * @param resultaat
     */
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


    /**
     * Rit opslaan in de database
     * @param start vertrekpunt
     * @param einde destination
     * @param kilometer afstand in kilometers
     * @param prijsNafte prijs van de nafte
     * @param resultaat totale prijs
     * @param heenenterug retour reis boolean
     * @throws JSONException
     */
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
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                    Date date = new Date();
                    Rit rit= new Rit(dateFormat.format(date), currentUser.getEmail(),eigenaarAuto, start, einde, nummerPlaatTextView.getText().toString(), finalKilometer, prijsNafte, finalResultaat, heenenterug, sourceLatLng, destLatLng);

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

        List<PatternItem> pattern = Arrays.<PatternItem>asList(
                new Dot(), new Gap(20), new Dash(30), new Gap(20));
        Random rnd= new Random();

        // polyline opvragen vanuit helperclass en deze weergeven op de minimap
        polyline=PassPolyline.polyline;
        gmap.addPolyline(new PolylineOptions()
                .addAll(PassPolyline.polyline.getPoints())
                .color(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)))
                .width(15)
                .pattern(pattern)
        );

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
        this.unregisterReceiver(br);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onBackPressed() {
        if(false){
            super.onBackPressed();
        }
    }
}