package com.codepath.welldone.persister;

import android.util.Log;

import com.codepath.welldone.model.Pump;
import com.codepath.welldone.model.Report;
import com.parse.ParseException;
import com.parse.ParseQuery;

/**
 * DB operations pertaining to Report
 */
public class ReportPersister {

    /**
     * Pin label to manage all pinned reports together.
     */
    public static final String ALL_REPORTS = "allReports";

    /**
     * Get the latest report associated with a given pump.
     * Latest = most recent "updatedAt"
     *
     * @param pump
     * @return
     */
    public static Report getLatestReportForPump(Pump pump) {

        final ParseQuery<Report> query = ParseQuery.getQuery(Report.class);
        query.fromPin(ALL_REPORTS);
        query.whereEqualTo("pump", pump);
        query.orderByDescending("updatedAt");
        try {

            final Report latestReport = ((Report) query.getFirst());
            Log.d("ReportPersister", "Found latest report for pump " + pump.getAddress() + " "
                    + latestReport.getNotes());
            return latestReport;
        } catch (ParseException e) {
            Log.d("ReportPersister", "getLatestReportForPump failed to return latest report for pump ID: "
                    + pump.getAddress() + " " + e.toString());
        }
        return null;
    }
}
