package com.example.tibo.myrides.UserActivities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tibo.myrides.Entities.Rit;
import com.example.tibo.myrides.Models.PassPolyline;
import com.example.tibo.myrides.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MyDrivesActivity extends AppCompatActivity implements OnMapReadyCallback {


    //minimap
    private MapView mapView;
    private GoogleMap gmap;
    private static final String MAP_VIEW_BUNDLE_KEY = "AIzaSyCC6_SCEF4zQaG5fbR-WyWKEEImFycQWsI";


    HashMap<Polyline, Rit> polylineRitHashMap;
    HashMap<String, List<Rit>> dateBasedRitten;

    //firebase authentication handler
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    //firebase database handler
    FirebaseFirestore db;

    Spinner mySpinner;

    TextView routeInfo;

    private static LatLng source;
    private static LatLng dest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_drive);

        //firebase authentication init
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        //firebase database init
        db = FirebaseFirestore.getInstance();

        dateBasedRitten= new HashMap<>();
        polylineRitHashMap=new HashMap<>();

        mySpinner= findViewById(R.id.datumSpinnerMyDrives);

        routeInfo=findViewById(R.id.routeInfo);

        //fetch alle ritten die uitgevoerd zijn door current user
        Task<QuerySnapshot> query= db.collection("ritten").whereEqualTo("uitvoerder", currentUser.getEmail()).get();
        query.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    if(dateBasedRitten.get(documentSnapshot.getString("date"))==null){
                        dateBasedRitten.put(documentSnapshot.getString("date"),  new ArrayList<>());
                    }

                    dateBasedRitten.get(documentSnapshot.getString("date")).add(new Rit(documentSnapshot.getData()));

                    visualiseerRitten();
                }
            }
        });
        query.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });


        // minimap
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mapView = findViewById(R.id.mapMyRoutes);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(mapView.getContext());



    }

    public void visualiseerRitten(){

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



                    // init coordinates source
                    StringBuilder sourceURL = new StringBuilder();
                    sourceURL.append("https://api.mapbox.com/geocoding/v5/mapbox.places/");
                    sourceURL.append(rit.getVertrekpunt());
                    sourceURL.append(".json?access_token=pk.eyJ1IjoiYWFyb25oYWxsYWVydCIsImEiOiJjam4zbW00OHMyMDFlM3dwbHNmeTZubHU2In0.p4W7257HvvNNPLZukiBTJg&limit=1");
                    Request request = new Request.Builder()
                            .url(sourceURL.toString())
                            .get()
                            .addHeader("cache-control", "no-cache")
                            .addHeader("Postman-Token", "b3c2f59e-1217-4fff-9ce3-d28030cc397f")
                            .build();
                    OkHttpClient client = new OkHttpClient();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                double latSource = (double) jsonObject.getJSONArray("features").getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates").get(1);
                                double lngSource = (double) jsonObject.getJSONArray("features").getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates").get(0);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        source=new LatLng(latSource, lngSource);
                                        BitmapDescriptor icon= BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);

                                        gmap.addMarker(new MarkerOptions().position(source).title(rit.getVertrekpunt()).icon(icon));
                                        if(dest!=null){
                                            drawPolyline(source, dest, color, rit);
                                            source=null;
                                            dest=null;
                                        }
                                    }
                                });
                                } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    });


                    // init coordinates destination
                    StringBuilder destURL = new StringBuilder();
                    destURL.append("https://api.mapbox.com/geocoding/v5/mapbox.places/");
                    destURL.append(rit.getBestemming());
                    destURL.append(".json?access_token=pk.eyJ1IjoiYWFyb25oYWxsYWVydCIsImEiOiJjam4zbW00OHMyMDFlM3dwbHNmeTZubHU2In0.p4W7257HvvNNPLZukiBTJg&limit=1");
                    Request destRequest = new Request.Builder()
                            .url(destURL.toString())
                            .get()
                            .addHeader("cache-control", "no-cache")
                            .addHeader("Postman-Token", "b3c2f59e-1217-4fff-9ce3-d28030cc397f")
                            .build();
                    client.newCall(destRequest).enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                double latDest = (double) jsonObject.getJSONArray("features").getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates").get(1);
                                double lngDest = (double) jsonObject.getJSONArray("features").getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates").get(0);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dest= new LatLng(latDest, lngDest);
                                        gmap.addMarker(new MarkerOptions().position(dest).title(rit.getBestemming()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                        if(source!=null){
                                            drawPolyline(source, dest, color, rit);
                                            source=null;
                                            dest=null;
                                        }
                                    }
                                });
                                } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    });



                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private synchronized void drawPolyline(LatLng source, LatLng dest, int color, Rit rit) {
        ArrayList<LatLng> points = new ArrayList<LatLng>();
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
}
