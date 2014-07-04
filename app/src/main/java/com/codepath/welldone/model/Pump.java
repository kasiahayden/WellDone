package com.codepath.welldone.model;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Pump")
public class Pump extends ParseObject {
    public ParseGeoPoint location;
    public PumpStatus status;
    public String statusString;

    public Pump() {

    }

    public static List<Pump> makePumpList(List<ParseObject> objects) {
        ArrayList<Pump> pumps = new ArrayList<Pump>();
        for (ParseObject object : objects) {
            pumps.add(makePump(object));
        }
        return pumps;
    }

    public static Pump makePump(ParseObject object) {
        Pump pump = new Pump();
        pump.location = object.getParseGeoPoint("location");
        pump.statusString = object.getString("currentStatusHumanReadable");
        int x = 0; x++;

        return pump;
    }

}
