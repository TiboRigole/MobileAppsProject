package com.example.tibo.myrides.UserActivities;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tibo.myrides.Entities.CurrentUser;
import com.example.tibo.myrides.Entities.Rit;
import com.example.tibo.myrides.General.MainActivity;
import com.example.tibo.myrides.R;
import com.facebook.login.LoginManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MyDrivesActivity extends AppCompatActivity implements OnMapReadyCallback {

    // INIT LAYOUT
    private Spinner mySpinner;
    private TextView routeInfo;
    private DrawerLayout mDrawerLayout;
    // INIT MINIMAP
    private MapView mapView;
    private GoogleMap gmap;
    private static final String MAP_VIEW_BUNDLE_KEY = "AIzaSyCC6_SCEF4zQaG5fbR-WyWKEEImFycQWsI";

    // INIT FIREBASE
    private FirebaseFirestore db;
    private CurrentUser currentUser;

    // OPSLAG
    // link tussen Polyline en Rit object
    private HashMap<Polyline, Rit> polylineRitHashMap;

    // key = String van datum waarop rit is uitgevoerd
    // value= lijst van ritten die op datum zijn uitgevoerd
    private HashMap<String, List<Rit>> dateBasedRitten;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_drive);



        // DEF FIREBASE
        currentUser = CurrentUser.getInstance();
        db = FirebaseFirestore.getInstance();



        // DEF OPSLAG
        dateBasedRitten= new HashMap<>();
        polylineRitHashMap=new HashMap<>();



        // DEF LAYOUT
        mySpinner= findViewById(R.id.datumSpinnerMyDrives);
        routeInfo=findViewById(R.id.routeInfo);
        mDrawerLayout = findViewById(R.id.drawer_layout_adddrive);

        // toolbar toevoegen aan de layout (nodig om de menuknop te hebben, die het zijkantmenu opkomt)
        Toolbar toolbar = findViewById(R.id.toolbar_MyDriveActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Drives");

        //menuknop toevoegen aan de toolbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        //item tappen : zet het item op selected,  sluit de zijbar
        NavigationView navigationView = findViewById(R.id.navigationzijkant_view);
        navigationView.setCheckedItem(R.id.nav_my_drives);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();


                        // verschillende logica's / doorverwijzingen bij knopjes
                        if(menuItem.getItemId() == R.id.nav_logout){

                            //log de user uit
                            currentUser.logout();
                            //log uit van facebook
                            LoginManager.getInstance().logOut();
                            //ga terug naar de mainActivity
                            startActivity(new Intent(MyDrivesActivity.this, MainActivity.class));
                        }

                        if(menuItem.getItemId()==R.id.nav_add_drive){
                            startActivity(new Intent(MyDrivesActivity.this, AddDriveActivity.class));
                        }

                        if(menuItem.getItemId()==R.id.nav_add_car){
                            startActivity(new Intent(MyDrivesActivity.this, AddCarActivity.class));
                        }

                        if(menuItem.getItemId()==R.id.nav_other_drives){
                            startActivity(new Intent(MyDrivesActivity.this, OtherDrivesActivity.class ));
                        }

                        if(menuItem.getItemId()==R.id.nav_my_drives){
                            startActivity(new Intent(MyDrivesActivity.this, MyDrivesActivity.class));
                        }

                        return true;
                    }
                });



        // DEF MINIMAP
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mapView = findViewById(R.id.mapMyRoutes);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(mapView.getContext());



        //fetch alle ritten die uitgevoerd zijn door current user
        // voeg ze toe aan hashmap aan de hand van datum waarop ze zijn uitgevoerd
        Task<QuerySnapshot> query= db.collection("ritten").whereEqualTo("uitvoerder", CurrentUser.getInstance().getEmail()).get();
        query.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    if(dateBasedRitten.get(documentSnapshot.getString("date"))==null){
                        dateBasedRitten.put(documentSnapshot.getString("date"),  new ArrayList<>());
                    }

                    dateBasedRitten.get(documentSnapshot.getString("date")).add(new Rit(documentSnapshot.getData()));

                }
                // visualisatie van de ritten op de map
                visualiseerRitten();
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
     * lijn in vogelvlucht van verschillende sources and destinations om routes aan te tonen
     * als polyline geklikt wordt, verschijnt informatie in textview onder map
     */
    public void visualiseerRitten(){
        //@TODO synchronisatie probleem oplossen, wachten op 2 requests totdat lijn getrokken mag worden, groot probleem, moeilijk op te lossen !!

        ArrayList<String> datums= new ArrayList<String>();
        for(Map.Entry<String, List<Rit>> entry : dateBasedRitten.entrySet()) {
            String key = entry.getKey();
            datums.add(key);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, datums);
        //set the spinners adapter to the previously created one.
        mySpinner.setAdapter(adapter);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gmap.clear();
                for (Rit rit : dateBasedRitten.get(mySpinner.getSelectedItem().toString())) {

                    Random rnd = new Random();
                    int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));


                    BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                    gmap.addMarker(new MarkerOptions().position(rit.getVertrekCoord()).title(rit.getVertrekpunt()).icon(icon));
                    gmap.addMarker(new MarkerOptions().position(rit.getEindCoord()).title(rit.getBestemming()).icon(icon));

                    drawPolyline(rit.getVertrekCoord(), rit.getEindCoord(), color,rit);


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * tekenen van polyline
     * @param source vertrekpunt
     * @param dest destination
     * @param color kleur van polyline
     * @param rit Rit object om toe te voegen om link te leggen via rit en polyline in hashmap
     */
    private synchronized void drawPolyline(LatLng source, LatLng dest, int color, Rit rit) {
        ArrayList<LatLng> points = new ArrayList<>();
        PolylineOptions polyLineOptions = new PolylineOptions();
        points.add(source);
        points.add(dest);
        polyLineOptions.width(10);
        polyLineOptions.geodesic(true);
        polyLineOptions.color(color);
        polyLineOptions.clickable(true);
        polyLineOptions.addAll(points);
        Polyline polyline = gmap.addPolyline(polyLineOptions);
        polylineRitHashMap.put(polyline, rit);
        polyline.setGeodesic(true);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMinZoomPreference(5);
        LatLng center= new LatLng(50.85045, 4.34878);
        gmap.moveCamera(CameraUpdateFactory.newLatLng(center));


        gmap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                Rit r= polylineRitHashMap.get(polyline);
                routeInfo.setText(r.toString());
            }
        });




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


    //logica wanneer op menu knop geduwd wordt dat het sidebarmenu geopend wordt
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
