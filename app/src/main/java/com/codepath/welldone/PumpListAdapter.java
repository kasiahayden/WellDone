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

    static class ViewHolder {

        ImageView ivPump;
        TextView tvLastUpdated;
        TextView tvLocation;
        TextView tvPumpName;
    }
    private ViewHolder viewHolder; // view lookup cache stored in tag

    public PumpListAdapter(Context context) {
        super(context, R.layout.row_pump);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Pump pump = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_pump, parent, false);
            viewHolder.ivPump = (ImageView) convertView.findViewById(R.id.ivPump);
            viewHolder.tvLastUpdated = (TextView) convertView.findViewById(R.id.tvPumpLastUpdated);
            viewHolder.tvLocation = (TextView) convertView.findViewById(R.id.tvPumpLocation);
            viewHolder.tvPumpName = (TextView) convertView.findViewById(R.id.tvPumpName);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
            View expandedContainer = convertView.findViewById(R.id.vgDetailsContainer);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)expandedContainer.getLayoutParams();
            params.height = 0;
            expandedContainer.setLayoutParams(params);
        }

        // The last updated date is wrt the local time zone.
        viewHolder.tvLastUpdated.setText(DateTimeUtil.getFriendlyLocalDateTime(pump.getUpdatedAt()));
        viewHolder.tvPumpName.setText(pump.getName());
        setPumpColorBasedOnStatus(pump.getCurrentStatus());
        setupLocationLabel(pump);

        Button newReport = (Button)convertView.findViewById(R.id.btnNewReport);
        newReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rowListener.onNewReportClicked(pump);
            }
        });

        return convertView;
    }

    private void setupLocationLabel(Pump pump) {
        // XXX These should be reverse geo-coded to be human-readable
        viewHolder.tvLocation.setText(String.format("(%f, %f)",
                pump.getLocation().getLatitude(), pump.getLocation().getLongitude()));
    }

    // Set the color of a pump number based on its work state
    private void setPumpColorBasedOnStatus(String pumpStatus) {

        // XXX Status should probably be an enum, in which case this would
        // reduce to a switch case.
        if (pumpStatus == null || pumpStatus.equalsIgnoreCase("broken_permanent")) {
            viewHolder.ivPump.setBackgroundColor(Color.RED);
        } else if (pumpStatus.equalsIgnoreCase("fix_in_progress")) {
            viewHolder.ivPump.setBackgroundColor(Color.YELLOW);
        } else if (pumpStatus.equalsIgnoreCase("good")) {
            viewHolder.ivPump.setBackgroundColor(Color.GREEN);
        } else {
            viewHolder.ivPump.setBackgroundColor(Color.DKGRAY);
        }
    }
}
