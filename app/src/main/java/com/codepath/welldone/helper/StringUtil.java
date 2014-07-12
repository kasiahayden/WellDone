package com.codepath.welldone.helper;

/**
 * Just some basic string operations
 */
public class StringUtil {

    public static String getConcatenatedString(String... args) {

        final StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(arg);
        }
        return sb.toString();
    }
}
