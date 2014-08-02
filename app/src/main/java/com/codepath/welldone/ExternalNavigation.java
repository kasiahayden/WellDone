package com.codepath.welldone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.codepath.welldone.model.Pump;

public class ExternalNavigation {

    public static final String HARD_CODED_START_LOCAITON = "-5.006505,32.836221";

    public static void askAboutPumpNavigation(final Context context, final String currentAddress, final Pump newPump, String title, final boolean shouldPopActivityStackOnDecision) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(String.format("Navigate to next pump? \n\n%s: %s\n(%s, priority %d)",
                        newPump.getName(),
                        newPump.getAddress(),
                        newPump.getCurrentStatus(),
                        newPump.getPriority()))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String url = String.format("http://maps.google.com/maps?saddr=%s&daddr=%s",
                                currentAddress,
                                newPump.getAddress());
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(url));
                        context.startActivity(intent);
                        if (shouldPopActivityStackOnDecision && context instanceof Activity) {
                            ((Activity) context).finish();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (shouldPopActivityStackOnDecision && context instanceof Activity) {
                            ((Activity) context).finish();
                        }
                    }
                })
                .setIcon(R.drawable.ic_check)
                .show();
    }
}
