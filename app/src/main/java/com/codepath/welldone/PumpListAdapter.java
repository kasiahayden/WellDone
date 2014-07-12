package com.codepath.welldone;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.welldone.helper.DateTimeUtil;
import com.codepath.welldone.model.Pump;

/**
 * Adapter to hold a list of pumps with their numbers, color-coded state, and
 * (reverse geo-coded in future) location.
 */
public class PumpListAdapter extends ArrayAdapter<Pump> {

    public interface PumpListListener {
        public void onNewReportClicked(Pump pump);
    }

    public PumpListListener rowListener;
    private ImageView ivPump;
    private TextView tvLastUpdated;
    private TextView tvLocation;
    private TextView tvPumpName;

    public PumpListAdapter(Context context) {
        super(context, R.layout.row_pump);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Pump pump = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_pump, parent, false);
        }
        else {
            View expandedContainer = convertView.findViewById(R.id.vgDetailsContainer);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)expandedContainer.getLayoutParams();
            params.height = 0;
            expandedContainer.setLayoutParams(params);
        }

        ivPump = (ImageView) convertView.findViewById(R.id.ivPump);
        tvLastUpdated = (TextView) convertView.findViewById(R.id.tvPumpLastUpdated);
        tvLocation = (TextView) convertView.findViewById(R.id.tvPumpLocation);
        tvPumpName = (TextView) convertView.findViewById(R.id.tvPumpName);
        Button newReport = (Button)convertView.findViewById(R.id.btnNewReport);
        newReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rowListener.onNewReportClicked(pump);
            }
        });


        // The last updated date is wrt the local time zone.
        tvLastUpdated.setText(DateTimeUtil.getFriendlyLocalDateTime(pump.getUpdatedAt()));
        tvPumpName.setText(pump.getName());
        setPumpColorBasedOnStatus(pump.getCurrentStatus());

        setupLocationLabel(tvLocation, pump);

        return convertView;
    }

    private void setupLocationLabel(TextView locationTextView, Pump pump) {
        // XXX These should be reverse geo-coded to be human-readable
        if (pump == null || pump.getLocation() == null) {
            return;
        }
        locationTextView.setText(String.format("(%f, %f)",
                pump.getLocation().getLatitude(), pump.getLocation().getLongitude()));
    }

    // Set the color of a pump number based on its work state
    private void setPumpColorBasedOnStatus(String pumpStatus) {

        // XXX Status should probably be an enum, in which case this would
        // reduce to a switch case.
        if (pumpStatus == null || pumpStatus.equalsIgnoreCase("broken_permanent")) {
            ivPump.setBackgroundColor(Color.RED);
        } else if (pumpStatus.equalsIgnoreCase("fix_in_progress")) {
            ivPump.setBackgroundColor(Color.YELLOW);
        } else if (pumpStatus.equalsIgnoreCase("good")) {
            ivPump.setBackgroundColor(Color.GREEN);
        } else {
            ivPump.setBackgroundColor(Color.DKGRAY);
        }
    }
}
