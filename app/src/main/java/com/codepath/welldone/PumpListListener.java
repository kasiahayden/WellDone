package com.codepath.welldone;

import com.codepath.welldone.model.Pump;

/**
* Created by androiddev on 7/28/14.
*/
public interface PumpListListener {
    public void onNewReportClicked(Pump pump);
    public void onListRefreshederested();
}
