package com.codepath.welldone.persister;

import android.util.Log;

import com.codepath.welldone.model.Report;

/**
 * All DB operations pertaining to Report
 */
public class ReportPersister {

    public static void persistReport(Report report) {

        Log.d("debug", "Saving report with title: " + report.getTitle());
        report.saveEventually();
    }
}
