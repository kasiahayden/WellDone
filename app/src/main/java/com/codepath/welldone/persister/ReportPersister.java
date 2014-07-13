package com.codepath.welldone.persister;

import android.util.Log;

import com.codepath.welldone.model.Report;

/**
 * All DB operations pertaining to Report
 */
public class ReportPersister {

    /**
     * Pin label to manage all pinned reports together.
     */
    public static final String ALL_REPORTS = "allReports";

    public static void persistReport(Report report) {

        Log.d("debug", "Saving report with title: " + report.getTitle());
        report.saveEventually();
    }
}
