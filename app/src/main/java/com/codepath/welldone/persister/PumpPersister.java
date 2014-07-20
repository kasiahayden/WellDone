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
        final ParseQuery<Pump> remoteQuery = ParseQuery.getQuery(Pump.class);
        Pump remotePump = null;
        try {
            remotePump = remoteQuery.get(pumpObjectId);
            remotePump.refresh();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String status = remotePump.getCurrentStatus();


        final ParseQuery<Pump> query = ParseQuery.getQuery(Pump.class);
        query.fromLocalDatastore();
        query.whereEqualTo("objectId", pumpObjectId);
        try {
            Pump localPump = query.getFirst();
            return localPump;
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("PumpPersister", "getPumpByObjectIdSyncly failed to return pump with objectId: " + pumpObjectId);
        }
        return null;
    }
}
