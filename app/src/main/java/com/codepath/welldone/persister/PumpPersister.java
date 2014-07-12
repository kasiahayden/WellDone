package com.codepath.welldone.persister;

import com.codepath.welldone.model.Pump;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

/**
 * All DB operations pertaining to Report
 */
public class PumpPersister {

    /**
     * Query a pump by its objectId synchronously.
     * XXX set policy to use cache in a later iteration.
     * @param pumpObjectId
     * @return
     */
    public static Pump getPumpByObjectIdSyncly(String pumpObjectId) {

        final ParseQuery<Pump> query = ParseQuery.getQuery(Pump.class);
        query.whereEqualTo("objectId", pumpObjectId);
        try {
            return ((Pump) query.getFirst());
        } catch (ParseException e) {

        }
        return null;
    }
}
