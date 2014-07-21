package com.codepath.welldone.helper;

/**
 * Simple class to manipulate addresses
 */
public class AddressUtil {

    /**
     * Kusini Unguja, TZ -> Kusini_Unguja
     * @param address
     * @return
     */
    public static String getConcatenatedCityFromAddress(String address) {

        return address.replace(" ", "_").split(",")[0];
    }

    /**
     * Kusini Unguja, TZ -> Kusini Unguja (no underscore, like above)
     * @param address
     * @return
     */
    public static String stripCountryFromAddress(String address) {

        return address.split(",")[0];
    }
}
