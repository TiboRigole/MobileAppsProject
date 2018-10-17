package com.example.tibo.myrides.Models;

public class Geolocation {
    private double longitude;
    private double latitude;

    public Geolocation(){
        // Default constructor required for calls to DataSnapshot.getValue(Geolocation.class)
        longitude = 0;
        latitude = 0;
    }

    public Geolocation(double longi, double lat){
        longitude = longi;
        latitude = lat;
    }


}
