package com.example.tibo.myrides.HelperPackage;

import com.google.android.gms.maps.model.Polyline;

/**
 * deze klasse dient om de polyline van de ene naar de andere activity door te geven
 * kan niet meegegeven worden in een intent aangezien deze niet Serializable is
 */
public class PassPolyline {

    public static Polyline polyline;
}
