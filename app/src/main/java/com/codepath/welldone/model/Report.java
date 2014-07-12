package com.codepath.welldone.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

/**
 * Class to represent to a report when a pump has been fixed/looked at.
 */
@ParseClassName("Report")
public class Report extends ParseObject {

    public Report() {

    }

    public Report(Pump pump, String reportedStatus, String title, String notes) {
        setPump(pump);
        setReportedStatus(reportedStatus);
        setTitle(title);
        setNotes(notes);
    }

    public Report(Pump pump, String reportedStatus, String title, String notes, ParseFile photo) {

        this(pump, reportedStatus, title, notes);
        setPhoto(photo);
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

    public ParseFile getPhoto() {
        return getParseFile("photo");
    }

    public void setPhoto(ParseFile photo) {
        put("photo", photo);
    }
}
