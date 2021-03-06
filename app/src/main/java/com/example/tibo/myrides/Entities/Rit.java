package com.example.tibo.myrides.Entities;


import com.example.tibo.myrides.RoomPackage.RitLocal;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

/**
 * Rit met gegevens die opgeslagen dienen te worden in de databank
 */
public class Rit {

    private String vertrekpunt;
    private String bestemming;
    private LatLng vertrekCoord;
    private LatLng eindCoord;
    private String nummerplaat;
    private Double afstand;
    private Double prijsNafte;
    private Double totalePrijs;
    private boolean heenenterug;
    private String uitvoerder;
    private String eigenaarAuto;
    private boolean betaald;
    // YYYY-MM-DD
    private String date;



    public Rit(Map<String,Object> data) {
        this.date = (String) data.get("date");
        this.uitvoerder = (String) data.get("uitvoerder");
        this.eigenaarAuto = (String) data.get("eigenaarAuto");
        this.vertrekpunt = (String) data.get("vertrekpunt");
        this.bestemming = (String) data.get("bestemming");
        this.nummerplaat = (String) data.get("nummerplaat");
        this.afstand = (double) data.get("afstand");
        this.prijsNafte = (double) data.get("prijsNafte");
        this.totalePrijs = (double) data.get("totalePrijs");
        this.heenenterug = (boolean) data.get("heenenterug");
        HashMap<String, Double> vertrekHash= (HashMap<String, Double>) data.get("vertrekCoord");
        HashMap<String, Double> eindHash= (HashMap<String, Double>) data.get("eindCoord");
        this.vertrekCoord= new LatLng (vertrekHash.get("latitude"), vertrekHash.get("longitude"));
        this.eindCoord=new LatLng (eindHash.get("latitude"), eindHash.get("longitude"));
        this.betaald= (boolean) data.get("betaald");
    }

    public Rit(String date, String uitvoerder, String eigenaarAuto, String vertrekpunt, String bestemming, String nummerplaat, double afstand, double prijsNafte, double totalePrijs, boolean heenenterug, LatLng vertrekCoord, LatLng eindCoord) {
        this.vertrekpunt = vertrekpunt;
        this.bestemming = bestemming;
        this.vertrekCoord = vertrekCoord;
        this.eindCoord = eindCoord;
        this.nummerplaat = nummerplaat;
        this.afstand = afstand;
        this.prijsNafte = prijsNafte;
        this.totalePrijs = totalePrijs;
        this.heenenterug = heenenterug;
        this.uitvoerder = uitvoerder;
        this.eigenaarAuto = eigenaarAuto;
        this.date = date;
        this.betaald=false;
    }

    public Rit(RitLocal ritLocal) {
        this.vertrekpunt = ritLocal.getVertrekpunt();
        this.bestemming = ritLocal.getBestemming();
        this.vertrekCoord = ritLocal.getVertrekCoord();
        this.eindCoord = ritLocal.getEindCoord();
        this.nummerplaat = ritLocal.getNummerplaat();
        this.afstand = ritLocal.getAfstand();
        this.prijsNafte = ritLocal.getPrijsNafte();
        this.totalePrijs = ritLocal.getTotalePrijs();
        this.heenenterug = ritLocal.isHeenenterug();
        this.uitvoerder = ritLocal.getUitvoerder();
        this.eigenaarAuto = ritLocal.getEigenaarAuto();
        this.date = ritLocal.getDate();
        this.betaald= ritLocal.isBetaald();
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

    @Override
    public String toString() {
        return "Rit{" +'\n'+
                "vertrekpunt='" + vertrekpunt + '\'' + '\n'+
                ", bestemming='" + bestemming + '\'' +'\n'+
                ", nummerplaat='" + nummerplaat + '\'' +'\n'+
                ", afstand=" + afstand +"km"+'\n'+
                ", prijsNafte=" + prijsNafte +"€/l"+'\n'+
                ", totalePrijs=" + totalePrijs +"€"+'\n'+
                ", heenenterug=" + heenenterug +'\n'+
                ", uitvoerder='" + uitvoerder + '\'' +'\n'+
                ", eigenaarAuto='" + eigenaarAuto + '\'' +'\n'+
                ", date='" + date + '\'' +'\n'+
                '}';
    }

    public String toBeautifulString(){

        String slot =null;
        if(heenenterug){
            slot = "heen en terug";
        }
        else{slot =" dit is een enkele rit";}

        return "van "+vertrekpunt+" naar "+bestemming+ '\n'+
                "met auto "+nummerplaat+'\n'+
                "totale afstand: "+afstand+" km"+'\n'+
                "aan "+prijsNafte+ "€/l"+'\n'+
                "totaalbedrag: "+totalePrijs + "€"+'\n'+
                "bestuurder: "+uitvoerder+'\n'+
                "te betalen aan: "+eigenaarAuto+ '\n'+
                "gereden op :" +date + '\n' + slot;

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isBetaald() {
        return betaald;
    }

    public void setBetaald(boolean betaald) {
        this.betaald = betaald;
    }

    public String getEigenaarAuto() {
        return eigenaarAuto;
    }

    public void setEigenaarAuto(String eigenaarAuto) {
        this.eigenaarAuto = eigenaarAuto;
    }

    public String getUitvoerder() {
        return uitvoerder;
    }

    public void setUitvoerder(String uitvoerder) {
        this.uitvoerder = uitvoerder;
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
}
