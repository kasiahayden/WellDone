package com.codepath.welldone;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.welldone.model.Pump;

import java.io.IOException;
import java.util.Random;

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
        TextView tvStatus;
        TextView tvPriority;
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
            viewHolder.tvPriority = (TextView)convertView.findViewById(R.id.tvPriority);
            viewHolder.tvStatus = (TextView)convertView.findViewById(R.id.tvPumpStatus);
            viewHolder.tvLocation = (TextView) convertView.findViewById(R.id.tvPumpLocation);
            clearTextViews();
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
        viewHolder.tvLastUpdated.setText(DateUtils.formatDateTime(getContext(), pump.getUpdatedAt().getTime(), DateUtils.FORMAT_SHOW_DATE));
        viewHolder.tvStatus.setText(Pump.humanReadableStringForStatus(pump.getCurrentStatus()));
        viewHolder.tvPriority.setText(String.format("Priority Level %d", pump.getPriority()));
        setPumpToRandomImage();
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

    private void clearTextViews() {
        viewHolder.tvLocation.setText("");
        viewHolder.tvStatus.setText("");
        viewHolder.tvPriority.setText("");
        viewHolder.tvLastUpdated.setText("");
    }

    private void setPumpToRandomImage() {
        String filename = String.format("pump%d.png", 1 + Math.abs(new Random().nextInt()) % 4);
        try {
            viewHolder.ivPump.setImageDrawable(Drawable.createFromStream(getContext().getAssets().open(filename), null));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void setupLocationLabel(Pump pump) {
        String fullyQualifiedName = String.format("%s", pump.getAddress());

        viewHolder.tvLocation.setText(fullyQualifiedName.split(",")[0]);
    }

    // Set the color of a pump number based on its priority
    private void setPumpColorBasedOnPriority(int pumpPriority) {

        switch (pumpPriority) {
            case 0: viewHolder.ivPump.setBackgroundColor(Color.RED); break;
            case 1: viewHolder.ivPump.setBackgroundColor(Color.MAGENTA); break;
            case 2:
            case 3: viewHolder.ivPump.setBackgroundColor(Color.BLACK); break;
            case 4: viewHolder.ivPump.setBackgroundColor(Color.YELLOW); break;
            case 5: viewHolder.ivPump.setBackgroundColor(Color.GREEN); break;
            default: viewHolder.ivPump.setBackgroundColor(Color.DKGRAY);
        }
    }
}
