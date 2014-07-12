package com.codepath.welldone.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * A simple class to do basic Date/Time manipulations
 */
public class DateTimeUtil {

    public static final String friendlyDateFormat = "yyyy-MM-dd HH:mm:ss";

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

    public static String getFriendlyTimeStamp() {

        return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    }

}
