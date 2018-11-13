package com.example.tibo.myrides.RoomPackage;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

@Entity
public class RitLocal {

    @PrimaryKey(autoGenerate = true)
    private int ritID;
    private String vertrekpunt;
    private String bestemming;
    @Ignore
    private LatLng vertrekCoord;
    @Ignore
    private LatLng eindCoord;
    private String nummerplaat;
    private Double afstand;
    private Double prijsNafte;
    private Double totalePrijs;
    private boolean heenenterug;
    private String uitvoerder;
    private String eigenaarAuto;
    // YYYY-MM-DD
    private String date;


    public int getRitID() {
        return ritID;
    }

    public void setRitID(int ritID) {
        this.ritID = ritID;
    }

    public String getVertrekpunt() {
        return vertrekpunt;
    }

    public void setVertrekpunt(String vertrekpunt) {
        this.vertrekpunt = vertrekpunt;
    }

    public String getBestemming() {
        return bestemming;
    }

    public void setBestemming(String bestemming) {
        this.bestemming = bestemming;
    }

    public LatLng getVertrekCoord() {
        return vertrekCoord;
    }

    public void setVertrekCoord(LatLng vertrekCoord) {
        this.vertrekCoord = vertrekCoord;
    }

    public LatLng getEindCoord() {
        return eindCoord;
    }

    public void setEindCoord(LatLng eindCoord) {
        this.eindCoord = eindCoord;
    }

    public String getNummerplaat() {
        return nummerplaat;
    }

    public void setNummerplaat(String nummerplaat) {
        this.nummerplaat = nummerplaat;
    }

    public Double getAfstand() {
        return afstand;
    }

    public void setAfstand(Double afstand) {
        this.afstand = afstand;
    }

    public Double getPrijsNafte() {
        return prijsNafte;
    }

    public void setPrijsNafte(Double prijsNafte) {
        this.prijsNafte = prijsNafte;
    }

    public Double getTotalePrijs() {
        return totalePrijs;
    }

    public void setTotalePrijs(Double totalePrijs) {
        this.totalePrijs = totalePrijs;
    }

    public boolean isHeenenterug() {
        return heenenterug;
    }

    public void setHeenenterug(boolean heenenterug) {
        this.heenenterug = heenenterug;
    }

    public String getUitvoerder() {
        return uitvoerder;
    }

    public void setUitvoerder(String uitvoerder) {
        this.uitvoerder = uitvoerder;
    }

    public String getEigenaarAuto() {
        return eigenaarAuto;
    }

    public void setEigenaarAuto(String eigenaarAuto) {
        this.eigenaarAuto = eigenaarAuto;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
