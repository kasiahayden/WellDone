package com.codepath.welldone.helper;

import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

            relativeTime = getRelativeTimeFromMilliSeconds(dateInMillis);

        } catch (ParseException e) {
            Log.d("info", "Couldn't parse relative timestamp for: " + timeStamp);
        }

        return relativeTime;
    }

    private static String getRelativeTimeFromMilliSeconds(long dateInMillis) {

        final String relativeTime = DateUtils.getRelativeTimeSpanString(dateInMillis,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();

        return formatRelativeTime(relativeTime);
    }

    // Return "49 seconds ago" as "49s"
    // Or "in 4 seconds" as "4s" (for newly composed tweets)
    private static String formatRelativeTime(String fullRelativeDate) {

        final String relativeDateFormat1 = "(\\d+)\\s(\\w+)\\s\\w+"; // "49 seconds ago"
        final Pattern relativeDatePattern1 = Pattern.compile(relativeDateFormat1);
        final String relativeDateFormat2 = "(\\w+)\\s(\\d+)\\s(\\w+)"; // "in 4 seconds"
        final Pattern relativeDatePattern2 = Pattern.compile(relativeDateFormat2);

        final Matcher matcher1 = relativeDatePattern1.matcher(fullRelativeDate);
        final Matcher matcher2 = relativeDatePattern2.matcher(fullRelativeDate);
        final StringBuilder sb = new StringBuilder();

        if (matcher1.matches()) {
            return sb.append(matcher1.group(1)).append(matcher1.group(2).charAt(0)).toString();
        } else if (matcher2.matches()) {
            return sb.append(matcher2.group(2)).append(matcher2.group(3).charAt(0)).toString();
        }

        // If the regex couldn't be parsed, make the relative timestamp manually
        // Should never happen though
        if (fullRelativeDate.equalsIgnoreCase("yesterday")) {
            return fullRelativeDate;
        }
        Log.d("debug", "Relative date didn't match pattern: " + fullRelativeDate);
        final String[] info = fullRelativeDate.split("\\s");
        if (info.length > 2) {
            sb.append(info[0].toString()).append(" ").append(info[1].toString().substring(0, 1));
        } else {
            sb.append(info[0].toString());
        }
        return sb.toString();
    }

}
