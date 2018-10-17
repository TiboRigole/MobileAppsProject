package com.example.tibo.myrides.Models;

public class Rit {

    private double aantalKm;
    private boolean betaald;
    private Geolocation begin;
    private Geolocation eind;

    public Rit(){
        // Default constructor required for calls to DataSnapshot.getValue(Rit.class)
        aantalKm = 0;
        betaald = false;
        begin = new Geolocation();
        eind = new Geolocation();
    }

    public Rit(double akm, boolean betaald, Geolocation begin, Geolocation eind){
        aantalKm = akm;
        this.betaald = betaald;
        this.begin = begin;
        this.eind = eind;
    }
}
