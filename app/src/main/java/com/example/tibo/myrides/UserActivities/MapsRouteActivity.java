package com.example.tibo.myrides.UserActivities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.example.tibo.myrides.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MapsRouteActivity extends FragmentActivity implements OnMapReadyCallback {

    private double selectedDistance;

    private GoogleMap mMap;
    double sourceLatCoord=0;
    double sourceLngCoord=0;
    double destLatCoord=0;
    double destLngCoord=0;

    LatLng center=null;
    LatLng sourceLatLng=null;
    LatLng destLatLng=null;

    HashMap<String, Double> distances;
    List<Polyline> lines;
    String source;
    String destination;

    Handler mainHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_route);
        lines= new ArrayList<>();
        distances=new HashMap<String, Double>();
        mainHandler= new Handler(getApplicationContext().getMainLooper());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Bundle bundle= getIntent().getExtras();
        OkHttpClient client = new OkHttpClient();
        source= (String) bundle.get("vertrek");
        destination= (String) bundle.get("aankomst");
        source.replace(" ","+");
        destination.replace(" ","+");

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                for (Polyline line : lines) {
                    List<PatternItem> pattern = Arrays.<PatternItem>asList(
                            new Dot(), new Gap(20), new Dash(30), new Gap(20));
                    line.setPattern(pattern);
                }
                List<PatternItem> pattern = Arrays.<PatternItem>asList(
                        new Dash(30), new Gap(1), new Dash(30), new Gap(1));
                polyline.setPattern(pattern);

                if(selectedDistance==distances.get(polyline.getId())) {
                    Intent intent = new Intent(MapsRouteActivity.this, SummaryActivity.class);

                    intent.putExtra("distance", Double.toString(selectedDistance));
                    intent.putExtra("vertrek", source);
                    intent.putExtra("aankomst", destination);
                    intent.putExtra("center",center);
                    intent.putExtra("sourceLatLng", sourceLatLng);
                    intent.putExtra("destLatLng", destLatLng);

                    // @TODO: route meegeven naar volgende activity

                    startActivity(intent);
                }
                else {
                    selectedDistance = distances.get(polyline.getId());
                    Toast.makeText(MapsRouteActivity.this, "distance: " + distances.get(polyline.getId()).toString() + " km \n press again to confirm", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // init coordinates source
        StringBuilder sourceURL = new StringBuilder();
        sourceURL.append("https://api.mapbox.com/geocoding/v5/mapbox.places/");
        sourceURL.append(source);
        sourceURL.append(".json?access_token=pk.eyJ1IjoiYWFyb25oYWxsYWVydCIsImEiOiJjam4zbW00OHMyMDFlM3dwbHNmeTZubHU2In0.p4W7257HvvNNPLZukiBTJg&limit=1");

        Request request = new Request.Builder()
                .url(sourceURL.toString())
                .get()
                .addHeader("cache-control", "no-cache")
                .addHeader("Postman-Token", "b3c2f59e-1217-4fff-9ce3-d28030cc397f")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    JSONObject jsonObject=new JSONObject(response.body().string());
                    System.out.println(jsonObject.toString());
                    double latSource =(double)jsonObject.getJSONArray("features").getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates").get(1);
                    double lngSource=(double)jsonObject.getJSONArray("features").getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates").get(0);
                    System.out.println("source coord "+latSource+lngSource);
                    setSource(latSource, lngSource);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        // init coordinates destination

        StringBuilder destURL = new StringBuilder();
        destURL.append("https://api.mapbox.com/geocoding/v5/mapbox.places/");
        destURL.append(destination);
        destURL.append(".json?access_token=pk.eyJ1IjoiYWFyb25oYWxsYWVydCIsImEiOiJjam4zbW00OHMyMDFlM3dwbHNmeTZubHU2In0.p4W7257HvvNNPLZukiBTJg&limit=1");

        Request requestDest = new Request.Builder()
                .url(destURL.toString())
                .get()
                .addHeader("cache-control", "no-cache")
                .addHeader("Postman-Token", "b3c2f59e-1217-4fff-9ce3-d28030cc397f")
                .build();

        client.newCall(requestDest).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    JSONObject jsonObject=new JSONObject(response.body().string());

                    double latDest =(double)jsonObject.getJSONArray("features").getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates").get(1);
                    double lngDest=(double)jsonObject.getJSONArray("features").getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates").get(0);

                    setDest(latDest, lngDest);
                    int wait=0;
                    while(wait==0) {
                        if (sourceLatCoord != 0 && sourceLngCoord != 0) {
                            initMap();
                            wait=1;
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        });


    }

    public void setSource(double latSource, double lngSource){
        sourceLatCoord=latSource;
        sourceLngCoord= lngSource;
    }

    public void setDest(double latDest, double lngDest){
        destLatCoord=latDest;
        destLngCoord= lngDest;
    }


    public void initMap() throws InterruptedException {

            // init source and center map on source
            Runnable initSourceMap= new Runnable() {
                @Override
                public void run() {
                    sourceLatLng = new LatLng(sourceLatCoord, sourceLngCoord);
                    destLatLng= new LatLng(destLatCoord, destLngCoord);
                    List<LatLng> points= new ArrayList<>();
                    points.add(sourceLatLng);
                    points.add(destLatLng);

                    center=computeCentroid(points);


                    mMap.addMarker(new MarkerOptions().position(sourceLatLng).title(source).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    mMap.addMarker(new MarkerOptions().position(destLatLng).title(destination).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 9));
                }
            };
            mainHandler.post(initSourceMap);


            requestRoute("shortest");
            requestRoute("fastest");
            requestAlternativesRoute();







    }

    private LatLng computeCentroid(List<LatLng> points) {
        double latitude = 0;
        double longitude = 0;
        int n = points.size();

        for (LatLng point : points) {
            latitude += point.latitude;
            longitude += point.longitude;
        }

        return new LatLng(latitude/n, longitude/n);
    }


    public void requestAlternativesRoute(){
        // request shortest route
        OkHttpClient client = new OkHttpClient();
        StringBuilder routeURL= new StringBuilder();
        routeURL.append("https://api.mapbox.com/directions/v5/mapbox/driving/");
        routeURL.append(sourceLngCoord+"," +sourceLatCoord+";");
        routeURL.append(destLngCoord+","+destLatCoord);
        routeURL.append("?alternatives=true&steps=false&access_token=pk.eyJ1IjoiYWFyb25oYWxsYWVydCIsImEiOiJjam4zbW00OHMyMDFlM3dwbHNmeTZubHU2In0.p4W7257HvvNNPLZukiBTJg&geometries=geojson&alternatives=true");
        Request requestShortest = new Request.Builder()
                .url(routeURL.toString())
                .get()
                .addHeader("cache-control", "no-cache")
                .addHeader("Postman-Token", "efca33ce-430f-4718-a197-5e997ae2ca83")
                .build();

        System.out.println("request= "+ requestShortest.toString());
        client.newCall(requestShortest).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    JSONObject jsonObject= new JSONObject(response.body().string());
                    for (int i = 0; i < jsonObject.getJSONArray("routes").length(); i++) {
                        JSONObject geoJsonData1= new JSONObject(jsonObject.getJSONArray("routes").getJSONObject(i).getJSONObject("geometry").toString());
                        System.out.println(geoJsonData1.toString());

                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                GeoJsonLayer layer= new GeoJsonLayer(mMap, geoJsonData1);
                                ArrayList<LatLng> points = new ArrayList<>();
                                try {
                                    for (int i1 = 0; i1 < geoJsonData1.getJSONArray("coordinates").length(); i1++) {
                                        double lng=(double)geoJsonData1.getJSONArray("coordinates").getJSONArray(i1).get(0);
                                        double lat=(double)geoJsonData1.getJSONArray("coordinates").getJSONArray(i1).get(1);
                                        LatLng pointi= new LatLng(lat, lng);
                                        points.add(pointi);
                                    }
                                }
                                catch (JSONException e){
                                    e.printStackTrace();
                                }

                                List<PatternItem> pattern = Arrays.<PatternItem>asList(
                                        new Dot(), new Gap(20), new Dash(30), new Gap(20));
                                Random rnd= new Random();
                                Polyline line= mMap.addPolyline(new PolylineOptions()
                                .addAll(points)
                                .color(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)))
                                .width(15)
                                .clickable(true)
                                .pattern(pattern)
                                );
                                lines.add(line);

                                try {
                                    distances.put(line.getId(), Double.parseDouble( jsonObject.getJSONArray("routes").getJSONObject(0).getString("distance"))/1000) ;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            } // This is your code

                        };
                        mainHandler.post(myRunnable);
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void requestRoute(String preference){
        // request shortest route
        OkHttpClient client = new OkHttpClient();
        StringBuilder routeURL= new StringBuilder();
        routeURL.append("https://api.openrouteservice.org/directions?api_key=5b3ce3597851110001cf624867c58548a4b8469e93860ace3a05ae98&coordinates=");
        routeURL.append(sourceLngCoord+"," +sourceLatCoord+"%7C");
        routeURL.append(destLngCoord+","+destLatCoord);
        routeURL.append("&profile=driving-car&units=km&preference="+preference+"&instructions=false&geometry_format=geojson");
        Request requestShortest = new Request.Builder()
                .url(routeURL.toString())
                .get()
                .addHeader("cache-control", "no-cache")
                .addHeader("Postman-Token", "efca33ce-430f-4718-a197-5e997ae2ca83")
                .build();

        System.out.println("request= "+ requestShortest.toString());
        client.newCall(requestShortest).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    JSONObject jsonObject= new JSONObject(response.body().string());
                    JSONObject geoJsonData1= new JSONObject(jsonObject.getJSONArray("routes").getJSONObject(0).getJSONObject("geometry").toString());
                    System.out.println(geoJsonData1.toString());



                    Runnable myRunnable = new Runnable() {


                        @Override
                        public void run() {
                            GeoJsonLayer layer= new GeoJsonLayer(mMap, geoJsonData1);
                            ArrayList<LatLng> points = new ArrayList<>();
                            try {
                                for (int i1 = 0; i1 < geoJsonData1.getJSONArray("coordinates").length(); i1++) {
                                    double lng=(double)geoJsonData1.getJSONArray("coordinates").getJSONArray(i1).get(0);
                                    double lat=(double)geoJsonData1.getJSONArray("coordinates").getJSONArray(i1).get(1);
                                    LatLng pointi= new LatLng(lat, lng);
                                    points.add(pointi);
                                }
                            }
                            catch (JSONException e){
                                e.printStackTrace();
                            }

                            Random rnd= new Random();
                            List<PatternItem> pattern = Arrays.<PatternItem>asList(
                                    new Dot(), new Gap(20), new Dash(30), new Gap(20));

                            Polyline line= mMap.addPolyline(new PolylineOptions()
                                    .addAll(points)
                                    .color(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)))
                                    .width(15)
                                    .clickable(true)
                                    .pattern(pattern)
                            );
                            lines.add(line);

                            try {
                                distances.put(line.getId(), Double.parseDouble(jsonObject.getJSONArray("routes").getJSONObject(0).getJSONObject("summary").getString("distance")) );
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        } // This is your code

                    };
                    mainHandler.post(myRunnable);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
