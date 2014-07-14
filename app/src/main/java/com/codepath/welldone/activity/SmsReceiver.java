package com.codepath.welldone.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by khayden on 7/13/14.
 */

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";
    public static final String smsAction = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        //---get the SMS message passed in---
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String str = "";
        if (bundle != null)
        {
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i=0; i<msgs.length; i++){
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                str += "SMS from " + msgs[i].getOriginatingAddress();
                str += " :";
                str += msgs[i].getMessageBody().toString();
                str += "\n";
            }
            //---display the new SMS message---
            Toast.makeText(context, "SmsReceiver:" + str, Toast.LENGTH_SHORT).show();
            Log.d("SmsReceiver", str);

            // Example sms: "Broken: Pump 16. Location: 37.7858, -122.4079. ID: dDccN2A8K3"
            Pattern OBJ_ID_PATTERN = Pattern.compile("ID: (\\w+)");
            Matcher m = OBJ_ID_PATTERN.matcher(str);
            String objectId = null;
            if (m.find()) {
                objectId = m.group(1);
            }
            Log.d("SmsReceiver", "match: " + objectId);
            if (objectId != null) {
                Intent i = new Intent(context, PumpDetails.class);
                i.putExtra("pumpObjectId", objectId);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.getApplicationContext().startActivity(i);
            }

        }
    }

    /*@Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d(TAG, "push intent received in SmsReceiver");
        try {
            if (intent == null) {
                Log.d(TAG, "Receiver intent null");
            } else {
                String action = intent.getAction();
                Log.d(TAG, "got action " + action);
                if (action.equals(smsAction)) {
                    //---get the SMS message passed in---
                    Bundle bundle = intent.getExtras();
                    SmsMessage[] msgs = null;
                    String msg_from;
                    if (bundle != null){
                        //---retrieve the SMS message received---
                        try{
                            Object[] pdus = (Object[]) bundle.get("pdus");
                            msgs = new SmsMessage[pdus.length];
                            for(int i=0; i<msgs.length; i++){
                                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                                msg_from = msgs[i].getOriginatingAddress();
                                String msgBody = msgs[i].getMessageBody();
                                Log.d(TAG, "from: " + msg_from + " body: " + msgBody);
                                Toast.makeText(context, "from: " + msg_from + " body: " + msgBody, Toast.LENGTH_SHORT).show();
                            }
                        }catch(Exception e){
//                            Log.d("Exception caught",e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception: " + e.getMessage());
        }
    }*/
}