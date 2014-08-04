package com.codepath.welldone.model;

import com.codepath.welldone.R;

import java.util.ArrayList;

/**
 * Created by khayden on 8/3/14.
 */
public class Technician {
    String name;
    double latitude;
    double longitude;
    String phoneNumber;

    public Technician (String name, double latitude, double longitude, String phoneNumber) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() { return latitude; }

    public double getLongitude() { return longitude; }

    public String getPhoneNumber() { return phoneNumber; }

    public int getTechnicianDrawable() {
        return R.drawable.ic_marker_technician;
    }
}
