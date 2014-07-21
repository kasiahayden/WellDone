package com.codepath.welldone.persister;

import android.util.Log;

import com.codepath.welldone.model.Pump;
import com.parse.ParseException;
import com.parse.ParseQuery;

/**
 * DB operations pertaining to Pump
 */
public class PumpPersister {

    /**
     * Pin label to manage all pinned pumps together.
     */
    public static final String ALL_PUMPS = "allPumps";

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
            Log.d("debug", "getPumpByObjectIdSyncly failed to return pump with objectId: "
                    + pumpObjectId + " " + e.toString());
        }
        return null;
    }
}
