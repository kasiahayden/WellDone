package com.codepath.welldone.model;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.io.Serializable;

@ParseClassName("Pump")
public class Pump extends ParseObject implements Serializable {

    public Pump() {

    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }

    public String getStatus() {
        return getString("currentStatus");
    }

}
