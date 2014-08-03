package com.codepath.welldone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;

import com.codepath.welldone.model.Pump;

public class ExternalNavigation {

    public static final String HARD_CODED_START_LOCAITON = "-5.006505,32.836221";

    public static void askAboutPumpNavigation(final Context context, final String currentAddress, final Pump newPump, final boolean shouldPopActivityStackOnDecision) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context)
                .setTitle("Navigate to Pump?")
                .setMessage(String.format("A route to %s will be calculated from your current position.", newPump.getAddress()))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (shouldPopActivityStackOnDecision && context instanceof Activity) {
                            ((Activity) context).finish();
                        }
                    }
                });
        final AlertDialog dialog = dialogBuilder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button b = (Button)dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                b.setTextColor(context.getResources().getColor(R.color.wellDoneBlue));
            }
        });
        dialog.show();
        int x = 0; x++;
    }
}
