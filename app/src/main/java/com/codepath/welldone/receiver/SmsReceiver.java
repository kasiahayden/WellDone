package com.codepath.welldone.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Base64;
import android.widget.Toast;

import com.codepath.welldone.activity.PumpBrowser;

import org.json.JSONException;
import org.json.JSONObject;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        //---get the SMS message passed in---
        Bundle bundle = intent.getExtras();
        SmsMessage firstMessage;
        JSONObject textBody = null;
        if (bundle != null)
        {
            Object[] pdus = (Object[]) bundle.get("pdus");
            firstMessage = SmsMessage.createFromPdu((byte[]) pdus[0]);
            String strBase64 = firstMessage.getMessageBody().toString();
            try {
                textBody = extractEncodedInformation(strBase64, context);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            //---pass object id as intent to PumpBrowser---
            String objectId;
            String address;
            String status;
            try {
                objectId = textBody.getString(PumpBrowser.EXTRA_PUMP_OBJECT_ID);
                address = textBody.getString("address");
                status = textBody.getString("status");


                Toast.makeText(context, "Pump " + address +" : Status changed to " + status, Toast.LENGTH_SHORT).show();

                Intent i = new Intent();
                i.setAction(PumpBrowser.RECEIVER_PUMP_UPDATE);
                i.addCategory(Intent.CATEGORY_DEFAULT);
                i.putExtra(PumpBrowser.EXTRA_PUMP_OBJECT_ID,
                        objectId);
                i.putExtra("status", status);
                context.sendBroadcast(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendDummyTextBroadcast(Context context, String textMessageContents) {
        //---pass object id as intent to PumpBrowser---
        String objectId;
        String status;
        JSONObject textBody;
        try {
            textBody = extractEncodedInformation(textMessageContents, context);
            objectId = textBody.getString(PumpBrowser.EXTRA_PUMP_OBJECT_ID);
            status = textBody.getString("status");

            Intent i = new Intent();
            i.setAction(PumpBrowser.RECEIVER_PUMP_UPDATE);
            i.addCategory(Intent.CATEGORY_DEFAULT);
            i.putExtra(PumpBrowser.EXTRA_PUMP_OBJECT_ID,
                    objectId);
            i.putExtra("status", status);
            context.sendBroadcast(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject extractEncodedInformation(String str, Context context) throws JSONException {
        String obj = new String(Base64.decode(str, 0));
        JSONObject decoded = new JSONObject(obj);
        return decoded;
    }
}