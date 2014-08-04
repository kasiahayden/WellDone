package com.codepath.welldone.model;

import com.codepath.welldone.R;
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.HashMap;

/**
 * Class to represent a pump.
 */
@ParseClassName("Pump")
public class Pump extends ParseObject {

    public static final String FIX_IN_PROGRESS = "Fix in progress";
    public static final String OPERATIONAL = "Operational";
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

    public boolean isBroken() {
        return getCurrentStatus().equalsIgnoreCase("broken");
    }

    public boolean isClaimedByATechnician() { return getBoolean("isClaimedByATechnician"); }

    public void setIsClaimedByATechnician(boolean isClaimed) {
        if (isClaimed) {
            setCurrentStatus(FIX_IN_PROGRESS);
        }
        put("isClaimedByATechnician", isClaimed);
    }

    public static int getPriorityFromStatus(String status) {

        if (status.equalsIgnoreCase(FIX_IN_PROGRESS)) {
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

    public int getDrawableBasedOnStatus() {
        if (isBroken()) {
            return R.drawable.ic_list_broken;
        }
        else if (getCurrentStatus().equalsIgnoreCase(FIX_IN_PROGRESS)) {
            return R.drawable.ic_list_in_progress;
        }
        else {
            return R.drawable.ic_list_good;
        }
    }

}
