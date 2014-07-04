package com.codepath.welldone.model;

import com.parse.ParseObject;

public enum PumpStatus {
    GOOD(),
    BROKEN_FIX_IN_PROGRESS();

    PumpStatus() {

    }

    public static PumpStatus makeStatus(ParseObject object) {
        if (object.getString("HumanReadableString").equals(GOOD.name())) {
            return GOOD;
        }
        if (object.getString("HumanReadableString").equals(BROKEN_FIX_IN_PROGRESS.name())) {
            return BROKEN_FIX_IN_PROGRESS;
        }
        return null;
    }
}
