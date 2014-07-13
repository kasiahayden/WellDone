package com.codepath.welldone.persister;

import android.util.Log;

import com.codepath.welldone.PumpListAdapter;
import com.codepath.welldone.model.Pump;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * All DB operations pertaining to Pump
 */
public class PumpPersister {

    /**
     * Pin label to manage all pinned pumps together.
     */
    public static final String ALL_PUMPS = "allPumps";

    /**
     * Fetch pumps in background.
     * First start with querying the local pin. If nothing there, query the remote source.
     * @param query
     */
    public static void fetchPumpsInBackground(final PumpListAdapter pumpArrayAdapter,
                                              final ParseQuery<ParseObject> query) {

        query.fromPin(PumpPersister.ALL_PUMPS);

        query.findInBackground(new FindCallback<ParseObject>() {

            public void done(final List<ParseObject> pumpList, ParseException e) {

                if (e == null) {

                    Log.d("info", "Fetching pumps from local DB. Found " + pumpList.size());

                    if (pumpList.size() == 0) {
                        fetchPumpsFromRemote(pumpArrayAdapter, ParseQuery.getQuery("Pump"));
                    } else {
                        Log.d("debug", "Using pumps fetched from local DB.");
                        addPumpsToAdapter(pumpArrayAdapter, pumpList);
                    }
                } else {
                    Log.d("error", "Exception while fetching pumps: " + e);
                }
            }
        });
    }

    /**
     * Unpin and repin a given list of pumps.
     * This method is public as other classes might want to call it, potentially.
     * @param pumpList
     */
    public static void unpinAndRepin(final List<ParseObject> pumpList) {

        Log.d("debug", "Unpinning previously saved objects");
        ParseObject.unpinAllInBackground(PumpPersister.ALL_PUMPS, pumpList,
                new DeleteCallback() {
                    public void done(ParseException e) {
                        if (e != null) {
                            // There was some error.
                            return;
                        } else {
                            Log.d("info", pumpList.size() + " previous cached pumps deleted.");
                        }
                    }
                }
        );
        // Add the latest results for this query to the cache.
        Log.d("debug", "Pinning newly retrieved objects");
        ParseObject.pinAllInBackground(PumpPersister.ALL_PUMPS, pumpList);
    }

    /**
     * Query a pump by its objectId synchronously from the local data store.
     * Typically, this ID belongs to an object already loaded into a fragment, so pinned locally.
     *
     * @param pumpObjectId
     * @return
     */
    public static Pump getPumpByObjectIdSyncly(String pumpObjectId) {

        final ParseQuery<Pump> query = ParseQuery.getQuery(Pump.class);
        query.fromPin(ALL_PUMPS);
        query.whereEqualTo("objectId", pumpObjectId);
        try {
            return ((Pump) query.getFirst());
        } catch (ParseException e) {

        }
        return null;
    }

    /* Private methods */
    private static void fetchPumpsFromRemote(final PumpListAdapter pumpArrayAdapter,
                                             ParseQuery<ParseObject> query) {

        query.findInBackground(new FindCallback<ParseObject>() {

            public void done(final List<ParseObject> pumpList, ParseException e) {

                if (e == null) {
                    Log.d("info", "Fetching pumps from remote DB. Found " + pumpList.size());
                    addPumpsToAdapter(pumpArrayAdapter, pumpList);
                } else {
                    Log.d("error", "Exception while fetching remote pumps: " + e);
                }
                // Unpin previously cached data and re-pin the newly fetched.
                unpinAndRepin(pumpList);
            }
        });
    }

    private static void addPumpsToAdapter(PumpListAdapter pumpArrayAdapter,
                                          List<ParseObject> pumpList) {

        for (ParseObject object : pumpList) {
            final Pump pump = (Pump) object;
            Log.d("debug", "Added pump: " + pump.getName() + " " + pump.getObjectId());
            pumpArrayAdapter.add(pump);
        }
    }
}
