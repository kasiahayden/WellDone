package com.codepath.welldone.model;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

/**
 * Class to represent a pump.
 */
@ParseClassName("Pump")
public class Pump extends ParseObject {

    public Pump() {

    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }

    public String getAddress() { return getString("address"); }

    public String getCurrentStatus() {
        return getString("currentStatus");
    }

    public void setCurrentStatus(String currentStatus) { put("currentStatus", currentStatus); }

    public int getPriority() { return getInt("priority"); }

    public String getName() {
        return getString("name");
    }

}
