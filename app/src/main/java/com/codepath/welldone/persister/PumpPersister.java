package com.codepath.welldone.persister;

import android.util.Log;

import com.codepath.welldone.model.Pump;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * All DB operations pertaining to Report
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

        }
        return null;
    }
}
