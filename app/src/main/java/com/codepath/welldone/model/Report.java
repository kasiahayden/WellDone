package com.codepath.welldone.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.io.Serializable;

/**
 * Class to represent to a report when a pump has been fixed/looked at.
 */
@ParseClassName("Report")
public class Report extends ParseObject implements Serializable {

    public Report() {

    }

    public Report(String title, String notes, String reportedStatus, Pump pump) {
        setTitle(title);
        setNotes(notes);
        setReportedStatus(reportedStatus);
        setPump(pump);
    }

    public String getTitle() {
        return getString("title");
    }

    public void setTitle(String title) {
        put("title", title);
    }

    public String getNotes() {
        return getString("notes");
    }

    public void setNotes(String notes) {
        put("notes", notes);
    }

    public Pump getPump() {
        return (Pump) getParseObject("pump");
    }

    public void setPump(Pump pump) {
        put("pump", pump);
    }

    public String getReportedStatus() {
        return getString("reportedStatus");
    }

    public void setReportedStatus(String reportedStatus) {
        put("reportedStatus", reportedStatus);
    }
}
