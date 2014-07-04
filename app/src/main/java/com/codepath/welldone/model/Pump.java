package com.codepath.welldone.model;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

@ParseClassName("Pump")
public class Pump extends ParseObject {

    public Pump() {

    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }

    public PumpStatus getStatus() {
        return (PumpStatus)getParseObject("currentStatus");
    }


}
