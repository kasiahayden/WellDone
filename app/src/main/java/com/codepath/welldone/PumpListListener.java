package com.codepath.welldone;

import com.codepath.welldone.model.Pump;

/**
* Created by androiddev on 7/28/14.
*/
public interface PumpListListener {
    public void onListRefreshederested();
    public void onPumpListRowSelected(Pump pump);
    public void onShouldInvalidatePagers();
}
