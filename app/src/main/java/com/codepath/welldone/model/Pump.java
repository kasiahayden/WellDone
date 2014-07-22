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

    public void setPriority(int priority) { put("priority", priority); }

    public String getName() {
        return getString("name");
    }

    public static int getPriorityFromStatus(String status) {

        if (status.equalsIgnoreCase("fix in progress")) {
            return 4;
        }
        if (status.equalsIgnoreCase("operational")) {
            return 5;
        }
        return 0;
    }

    public int getHash() {
        if (getAddress() == null) {
            return 0;
        }
        return getAddress().hashCode();
    }

}
