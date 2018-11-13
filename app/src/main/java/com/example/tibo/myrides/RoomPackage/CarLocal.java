package com.example.tibo.myrides.RoomPackage;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.List;

@Entity
public class CarLocal {

    private double verbruik;
    private String eigenaar;

    @PrimaryKey
    @NonNull
    private String kenteken;
    private String merk;
    private String benzineType;

    @Ignore
    private List<String> sharedWithUsers;

    public double getVerbruik() {
        return verbruik;
    }

    public void setVerbruik(double verbruik) {
        this.verbruik = verbruik;
    }

    public String getEigenaar() {
        return eigenaar;
    }

    public void setEigenaar(String eigenaar) {
        this.eigenaar = eigenaar;
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
