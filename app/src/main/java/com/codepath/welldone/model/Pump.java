package com.codepath.welldone.model;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.HashMap;

/**
 * Class to represent a pump.
 */
@ParseClassName("Pump")
public class Pump extends ParseObject {

    private static HashMap<String, String> pumpStatuses;

    public static String GOOD = "GOOD";
    public static String BROKEN = "BROKEN";
    public static String FIX_IN_PROGRESS = "FIX_IN_PROGRESS";

    public static String humanReadableStringForStatus(String status) {
        if (pumpStatuses == null) {
            pumpStatuses = new HashMap<String, String>();
            pumpStatuses.put(GOOD, "Good");
            pumpStatuses.put(BROKEN, "Broken");
            pumpStatuses.put(FIX_IN_PROGRESS, "Fix in progress");
        }
        return pumpStatuses.get(status);
    }

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
