package com.example.tibo.myrides.Entities;

public class Rit {

    String vertrekpunt;
    String bestemming;
    String nummerplaat;
    Double afstand;
    Double prijsNafte;
    Double totalePrijs;
    boolean heenenterug;
    String uitvoerder;
    String eigenaarAuto;

    public Rit(String uitvoerder, String eigenaarAuto, String vertrekpunt, String bestemming, String nummerplaat, Double afstand, Double prijsNafte, Double totalePrijs, boolean heenenterug) {
        this.uitvoerder=uitvoerder;
        this.eigenaarAuto= eigenaarAuto;
        this.vertrekpunt = vertrekpunt;
        this.bestemming = bestemming;
        this.nummerplaat = nummerplaat;
        this.afstand = afstand;
        this.prijsNafte = prijsNafte;
        this.totalePrijs = totalePrijs;
        this.heenenterug = heenenterug;
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
