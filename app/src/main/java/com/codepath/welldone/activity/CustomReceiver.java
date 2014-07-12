package com.codepath.welldone.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
//import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.welldone.fragment.PumpListFragment;
import com.codepath.welldone.model.Pump;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class CustomReceiver extends BroadcastReceiver {
    private static final String TAG = "CustomReceiver";

    //public static final String intentAction = "SEND_PUSH";
    public static final String intentAction = "PUMP_ALERT";

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "push intent received in Custom Receiver", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "push intent received");
        try {
            if (intent == null)
            {
                Log.d(TAG, "Receiver intent null");
            }
            else
            {
                String action = intent.getAction();
                Log.d(TAG, "got action " + action );
                if (action.equals(intentAction))
                {
                    String channel = intent.getExtras().getString("com.parse.Channel");
                    JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));


                    Log.d(TAG, "got action " + action + " on channel " + channel + " with:");
                    Iterator itr = json.keys();
                    while (itr.hasNext()) {
                        String key = (String) itr.next();
                        Log.d(TAG, "..." + key + " => " + json.getString(key));
                    }


                    /*Log.d(TAG, "got action " + action + " on channel " + channel + " with:");
                    Iterator<String> itr = json.keys();
                    while (itr.hasNext()) {
                        String key = (String) itr.next();
                        if (key.equals("customdata"))
                        {
                            // Handle push notif by invoking activity directly
                            Intent pupInt = new Intent(context, ShowPopUp.class);
                            pupInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                            pupInt.putExtra("customdata", json.getString(key));
                            context.getApplicationContext().startActivity(pupInt);

                            // Handle push notif by sending a local braoadcast to which the activity
                            // subscirbes to
                            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(intentAction));
                        }
                        Log.d(TAG, "..." + key + " => " + json.getString(key));
                    }*/
                }
            }

        } catch (JSONException e) {
            Log.d(TAG, "JSONException: " + e.getMessage());
        }
    }
}
