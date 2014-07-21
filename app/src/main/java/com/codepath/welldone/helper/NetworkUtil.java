package com.codepath.welldone.helper;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Simple class to assess and manage network connections
 */
public class NetworkUtil {

    /**
     * Check is network is available on the device.
     * @param activity
     * @return
     */
    public static boolean isNetworkAvailable(Activity activity) {

        final ConnectivityManager connectivityManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();

        //return false;
        return (activeNetworkInfo != null && activeNetworkInfo
                .isConnectedOrConnecting());
    }
}
