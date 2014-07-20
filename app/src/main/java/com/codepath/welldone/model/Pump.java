package com.codepath.welldone.model;

import android.util.Log;

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
    public static String BROKEN_PERMANENT = "BROKEN_PERMANENT";
    public static String FIX_IN_PROGRESS = "FIX_IN_PROGRESS";

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

    public static String humanReadableStringForStatus(String status) {

        if (pumpStatuses == null) {
            pumpStatuses = new HashMap<String, String>();
            pumpStatuses.put(GOOD, "Good");
            pumpStatuses.put(BROKEN, "Broken");
            pumpStatuses.put(BROKEN_PERMANENT, "Broken permanent");
            pumpStatuses.put(FIX_IN_PROGRESS, "Fix in progress");
        }
        return pumpStatuses.get(status);
    }

    public static int getPriorityFromStatus(String status) {

        if (status.equalsIgnoreCase(BROKEN)) {
            return 1;
        }
        if (status.equalsIgnoreCase(BROKEN_PERMANENT)) {
            return 3;
        }
        if (status.equalsIgnoreCase(GOOD)) {
            return 5;
        }
        if (status.equalsIgnoreCase(FIX_IN_PROGRESS)) {
            return 4;
        }
        Log.d("warn", "Received unknown status: " + status);
        return 1;
    }

}
