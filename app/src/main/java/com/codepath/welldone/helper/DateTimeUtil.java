package com.codepath.welldone.helper;

import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A simple class to do basic Date/Time manipulations
 */
public class DateTimeUtil {

    public static final String friendlyDateFormat = "hh:mm a";

    private static final String PARSE_TIMESTAMP_FORMAT = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";

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

    /**
     * Get date/time stamp with spaces replaced with underscores.
     * Typically needed when it has to be used in a file name, etc.
     * @return
     */
    public static String getFriendlyTimeStamp() {

        return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    }

    /**
     * Return an absolute timestamp such as "49 seconds ago"
     * @param timeStamp
     * @return
     */
    public static String getRelativeTimeofTweet(String timeStamp) {

        final SimpleDateFormat sdf =
                new SimpleDateFormat(PARSE_TIMESTAMP_FORMAT, Locale.ENGLISH);
        String relativeTime = "";

        try {
            long dateInMillis = sdf.parse(timeStamp).getTime();

            relativeTime = getRelativeTimeAgo(dateInMillis);

        } catch (ParseException e) {
            Log.d("info", "Couldn't parse relative timestamp for: " + timeStamp);
        }

        return relativeTime;
    }

    public static String getRelativeTimeAgo(long dateMillis) {
        String relativeDate = "";
        relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();

        HashMap<String, String> replaceMappings = new HashMap<String, String>();
        replaceMappings.put(" hours ago", "h");
        replaceMappings.put(" hour ago", "h");
        replaceMappings.put(" minutes ago", "m");
        replaceMappings.put(" minute ago", "m");
        replaceMappings.put(" seconds ago", "s");
        replaceMappings.put(" second ago", "s");
        replaceMappings.put(" day ago", "d");
        replaceMappings.put(" days ago", "d");
        replaceMappings.put("Yesterday", "1d");
        for (String suffixKey: replaceMappings.keySet()) {
            if (relativeDate.endsWith(suffixKey)) {
                relativeDate = relativeDate.replace(suffixKey, replaceMappings.get(suffixKey));
            }
        }

        if (relativeDate.contains(",")) {
            int commaIndex = relativeDate.indexOf(",");
            relativeDate = relativeDate.substring(0, commaIndex);
        }

        return relativeDate;
    }

}
