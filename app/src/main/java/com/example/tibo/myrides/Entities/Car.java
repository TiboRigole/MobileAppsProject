package com.example.tibo.myrides.Entities;


import java.util.List;
import java.util.Map;

/**
 * Car met gegevens die opgeslagen dienen te worden in de databank
 */
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

    public Car(Map<String,Object> data) {
        this.eigenaar= (String) data.get("eigenaar");
        this.verbruik = (double) data.get("verbruik");
        this.kenteken = (String) data.get("kenteken");
        this.merk = (String) data.get("merk");
        this.benzineType = (String) data.get("benzineType");
        this.sharedWithUsers= (List<String>) data.get("sharedWithUsers");
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(sharedWithUsers.toString());

        sb.deleteCharAt(0);
        sb.deleteCharAt(sb.length()-1);
        return  "eigenaar: " + eigenaar + '\n' +
                "verbruik: " + verbruik + "€/l" + '\n' +
                "nummmerplaat: "+ kenteken +
                " van " + merk + '\n' +
                "benzineType: " + benzineType + '\'' + '\n' +
                "gedeeld met:" + sb.toString();
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
