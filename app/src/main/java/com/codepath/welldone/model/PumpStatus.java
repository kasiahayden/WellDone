package com.codepath.welldone.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("PumpStatus")
public class PumpStatus extends ParseObject {

    public PumpStatus() {

    }

    public String getHumanReadableStatus() {
        return getString("humanReadableStatus");
    }

}
