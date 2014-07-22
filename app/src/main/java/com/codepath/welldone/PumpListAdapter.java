package com.codepath.welldone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.codepath.welldone.model.Pump;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

/**
 * Adapter to hold a list of pumps with their numbers, color-coded state, and
 * (reverse geo-coded in future) location.
 */
public class PumpListAdapter extends ArrayAdapter<Pump> {

    public interface PumpListListener {
        public void onNewReportClicked(Pump pump);
    }

    public PumpListListener rowListener;

    private ParseGeoPoint currentUserLocation;

    public PumpListAdapter(Context context) {

        super(context, R.layout.row_pump_list_item);
        currentUserLocation = (ParseGeoPoint) ParseUser.getCurrentUser().get("location");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Pump pump = getItem(position);

        PumpRowView pumpRowView;

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            pumpRowView = (PumpRowView)LayoutInflater.from(getContext()).inflate(R.layout.row_pump_list_item, parent, false);
            pumpRowView.clearTextViews();
        }
        else {
            pumpRowView = (PumpRowView)convertView;
        }

        pumpRowView.mPump = pump;
        pumpRowView.updateSubviews(currentUserLocation);

        Button newReport = (Button)pumpRowView.findViewById(R.id.btnNewReport);
        newReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rowListener.onNewReportClicked(pump);
            }
        });

        return pumpRowView;
    }
}
