package com.codepath.welldone.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class DemoSMSReceiver extends BroadcastReceiver
{
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
            if (bundle.get("pdus") == null) {
                Log.d("DBG", "failed to receive a text message. null");
                return;
            }
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i=0; i<msgs.length; i++){
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                str += "SMS from " + msgs[i].getOriginatingAddress();
                str += " :";
                str += msgs[i].getMessageBody().toString();
                str += "\n";
            }

            str = "eyAicHVtcE9iamVjdElkIiA6ICJoMWhub3ZMczJaIiwgInN0YXR1cyI6ICJCcm9rZW4ifQ==";
            String textBody = msgs[0].getMessageBody().toString();
            try {
                extractEncodedInformation(textBody, context);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            //---display the new SMS message---
            Toast.makeText(context, "DemoSMSReceiver:" + str, Toast.LENGTH_SHORT).show();
            Log.d("DemoSMSReceiver", str);
        }
    }

    public static void extractEncodedInformation(String str, Context context) throws JSONException {
        String obj = new String(Base64.decode(str, 0));
        JSONObject decoded = new JSONObject(obj);

        Intent i = new Intent();
        i.setAction(PumpBrowser.RECEIVER_PUMP_UPDATE);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.putExtra(PumpBrowser.EXTRA_PUSH_NOTIFICATION_PUMP_OBJECT_ID,
                decoded.getString(PumpBrowser.EXTRA_PUSH_NOTIFICATION_PUMP_OBJECT_ID));
        i.putExtra("status", decoded.getString("status"));
        context.sendBroadcast(i);
    }
}