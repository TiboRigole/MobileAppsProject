package com.example.tibo.myrides.Entities;

import java.util.List;

public class Car {

    // gemiddeld verbruik - liter per 100 km
    private double verbruik;
    private String eigenaar;
    private String kenteken;
    private String merk;
    private String benzineType;
    private List<String> sharedWithUsers;

    public Car(String eigenaar, double verbruik, String kenteken, String merk, String benzineType, List<String> sharedWithUsers) {
        this.eigenaar= eigenaar;
        this.verbruik = verbruik;
        this.kenteken = kenteken;
        this.merk = merk;
        this.benzineType = benzineType;
        this.sharedWithUsers=sharedWithUsers;
    }

    @Override
    public String toString() {
        return "Car{" +
                "eigenaar=" + eigenaar +
                "verbruik=" + verbruik +
                ", kenteken='" + kenteken + '\'' +
                ", merk='" + merk + '\'' +
                ", benzineType='" + benzineType + '\'' +
                ", sharedWithUsers=" + sharedWithUsers +
                '}';
    }


    public String getEigenaar() {
        return eigenaar;
    }

    public void setEigenaar(String eigenaar) {
        this.eigenaar = eigenaar;
    }

    public void setSharedWithUsers(List<String> sharedWithUsers) {
        this.sharedWithUsers = sharedWithUsers;
    }

    public List<String> getSharedWithUsers() {
        return sharedWithUsers;
    }

    public double getVerbruik() {
        return verbruik;
    }

    public void setVerbruik(double verbruik) {
        this.verbruik = verbruik;
    }

    public String getKenteken() {
        return kenteken;
    }

    public void setKenteken(String kenteken) {
        this.kenteken = kenteken;
    }

    public String getMerk() {
        return merk;
    }

    public void setMerk(String merk) {
        this.merk = merk;
    }

    public String getBenzineType() {
        return benzineType;
    }

    public void setBenzineType(String benzineType) {
        this.benzineType = benzineType;
    }
}
