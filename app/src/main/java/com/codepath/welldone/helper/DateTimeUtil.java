package com.codepath.welldone.helper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * A simple class to do basic Date/Time manipulations
 */
public class DateTimeUtil {

    public static final String friendlyDateFormat = "hh:mm a";

    /**
     * Return timestamp in human-readable form wrt local timezone
     *
     * @param date to be parsed
     * @return formatted timestamp
     */
    public static String getFriendlyLocalDateTime(Date date) {

        final SimpleDateFormat sdf = new SimpleDateFormat(friendlyDateFormat);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    /**
     * Return current date time as a concatenated string, to be used as filename.
     * @param date
     * @return
     */
    public static String getLocalDateTimeForFileName(Date date) {

        final SimpleDateFormat sdf = new SimpleDateFormat("MMddyyyy_HHmmss");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    public static String getFriendlyTimeStamp() {

        return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    }

}
