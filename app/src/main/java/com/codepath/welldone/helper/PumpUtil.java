package com.codepath.welldone.helper;

import android.util.Log;

import com.codepath.welldone.model.Pump;
import com.parse.ParseGeoPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A utility class for manipulating a collection of pumps
 */
public class PumpUtil {

    // Wrapper for a pump along with its distance from a given origin (such as
    // current user location).
    private static class WrappedPump implements Comparable<WrappedPump> {

        private Pump pump;
        private Double distance;

        WrappedPump(Pump pump, Double distance) {
            this.pump = pump;
            this.distance = distance;
        }

        @Override
        public int compareTo(WrappedPump otherPump) {
            if (this.distance < otherPump.distance) return -1;
            if (this.distance > otherPump.distance) return 1;
            return 0;
        }
    }

    /**
     * Given a list of pumps, return a sorted list based on distance from an origin (such as
     * current user location).
     *
     * @param pumpList
     * @param origin
     * @return
     */
    public static List<Pump> sortPumps(List<Pump> pumpList, ParseGeoPoint origin) {

        final List<WrappedPump> pumpsWithDistance = sortByDistanceInKm(pumpList, origin);
        Collections.sort(pumpsWithDistance);
        final List<Pump> sortedPumps = new ArrayList<Pump>(pumpList.size());
        for (WrappedPump wp : pumpsWithDistance) {
            sortedPumps.add(wp.pump);
            Log.d("debug", "Pump with distance: " + wp.pump.getObjectId() + " " +
                    wp.pump.getName() + " " + wp.distance + " " + wp.pump.getPriority());
        }
        return sortedPumps;
    }

    // Sort a given list of pumps from a given origin by distance in kilometers
    private static List<WrappedPump> sortByDistanceInKm(List<Pump> pumpList,
                                                ParseGeoPoint origin) {

        final List<WrappedPump> wrappedPumps = new ArrayList<WrappedPump>(pumpList.size());
        for (Pump pump : pumpList) {
            try {
                final double distanceFromUser = origin.distanceInKilometersTo(pump.getLocation());
                wrappedPumps.add(new WrappedPump(pump, distanceFromUser));
            }
            catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        return wrappedPumps;
    }
}
