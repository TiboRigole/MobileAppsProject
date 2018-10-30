package com.example.tibo.myrides.Entities;

import java.util.Map;

/**
 * Rit met gegevens die opgeslagen dienen te worden in de databank
 */
public class Rit {

    private String vertrekpunt;
    private String bestemming;
    private String nummerplaat;
    private Double afstand;
    private Double prijsNafte;
    private Double totalePrijs;
    private boolean heenenterug;
    private String uitvoerder;
    private String eigenaarAuto;
    // YYYY-MM-DD
    private String date;

    public Rit(String date, String uitvoerder, String eigenaarAuto, String vertrekpunt, String bestemming, String nummerplaat, Double afstand, Double prijsNafte, Double totalePrijs, boolean heenenterug) {
        this.date = date;
        this.uitvoerder = uitvoerder;
        this.eigenaarAuto = eigenaarAuto;
        this.vertrekpunt = vertrekpunt;
        this.bestemming = bestemming;
        this.nummerplaat = nummerplaat;
        this.afstand = afstand;
        this.prijsNafte = prijsNafte;
        this.totalePrijs = totalePrijs;
        this.heenenterug = heenenterug;
    }

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
