package com.codepath.welldone.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Receiver which will be always called when the push notification is recevied -- regardless
 * of whether or not the application is open.
 */
public class CustomReceiver extends BroadcastReceiver {
    private static final String TAG = "CustomReceiver";
    public static final String INTENT_ACTION_PUMP_ALERT = "PUMP_ALERT";


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "push intent received in CustomReceiver");
        try {
            if (intent == null)
            {
                Log.d(TAG, "Receiver intent null");
            }
            else
            {
                String action = intent.getAction();
                Log.d(TAG, "got action " + action );
                if (action.equals(INTENT_ACTION_PUMP_ALERT)) {
                    String channel = intent.getExtras().getString("com.parse.Channel");
                    JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));


                    Log.d(TAG, "got action " + action + " on channel " + channel + " with:");
                    Iterator itr = json.keys();
                    while (itr.hasNext()) {
                        String key = (String) itr.next();
                        Log.d(TAG, "..." + key + " => " + json.getString(key));
                    }

                    String objectId = json.getString("objectId");
                    Intent i = new Intent(context, PumpBrowser.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra(PumpBrowser.EXTRA_PUMP_OBJECT_ID, objectId);
                }
            }
        } catch (JSONException e) {
            Log.d(TAG, "JSONException: " + e.getMessage());
        }
    }
}
