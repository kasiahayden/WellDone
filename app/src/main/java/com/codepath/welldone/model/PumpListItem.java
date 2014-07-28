package com.codepath.welldone.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.codepath.welldone.PumpListAdapter;
import com.codepath.welldone.PumpRowView;
import com.codepath.welldone.R;
import com.codepath.welldone.activity.CreateReportActivity;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

public class PumpListItem implements AbstractListItem {

    public Pump pump;


    public PumpListItem(Pump pump) {
        this.pump = pump;
    }

    @Override
    public View getView(final LayoutInflater inflater, final View convertView, final ParseGeoPoint location, final PumpListAdapter.PumpListListener listener, final Context context) {

        PumpRowView pumpRowView;

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            pumpRowView = (PumpRowView)inflater.inflate(R.layout.row_pump_list_item, null);
        }
        else {
            pumpRowView = (PumpRowView)convertView;
        }
        pumpRowView.clearTextViews();

        pumpRowView.mPump = pump;
        pumpRowView.updateSubviews(location);

        Button newReport = (Button)pumpRowView.findViewById(R.id.btnNewReport);
        newReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onNewReportClicked(pump);
            }
        });

        Button navigateButton = (Button)pumpRowView.findViewById(R.id.btnNavigate);
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseGeoPoint point = (ParseGeoPoint) ParseUser.getCurrentUser().get("location");
                String fromLocation = "-4.377073, 34.281780";//String.format("%s,%s", point.getLatitude(), point.getLongitude());
                CreateReportActivity.askAboutPumpNavigation(context, fromLocation, pump, "Open in Maps?", false);
            }
        });

        return pumpRowView;
    }

    @Override
    public int getViewType() {
        return RowType.LIST_ITEM.ordinal();
    }
}
