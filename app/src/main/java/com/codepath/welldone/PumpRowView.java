package com.codepath.welldone;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.welldone.helper.AddressUtil;
import com.codepath.welldone.helper.DateTimeUtil;
import com.codepath.welldone.model.Pump;
import com.parse.ParseGeoPoint;

import java.text.DecimalFormat;

public class PumpRowView extends RelativeLayout {

    public ViewHolder viewHolder;

    private DecimalFormat df = new DecimalFormat("#.#");

    public Pump mPump;

    public PumpRowView(Context context, AttributeSet attrs){
        super(context, attrs);
        View.inflate(context, R.layout.row_pump, this);
        viewHolder = new ViewHolder();
        populateViewHolder();
    }

    private void populateViewHolder() {
        viewHolder.tvLastUpdated = (TextView)findViewById(R.id.tvPumpLastUpdated);
        viewHolder.tvLocation = (TextView)findViewById(R.id.tvPumpLocation);
        viewHolder.tvPumpDistance = (TextView)findViewById(R.id.tvPumpDistance);
        viewHolder.ivStatusIndicator = (ImageView)findViewById(R.id.ivPumpStatusIndicator);
    }

    public void updateSubviews(ParseGeoPoint currentUserLocation) {

        String mostOfTheTimeCorrectRelativeTime = DateTimeUtil.getRelativeTimeofTweet(mPump.getUpdatedAt().toString());
        if (mostOfTheTimeCorrectRelativeTime.equalsIgnoreCase("yesterday")) {
            viewHolder.tvLastUpdated.setText(mostOfTheTimeCorrectRelativeTime);
        }
        else {
            viewHolder.tvLastUpdated.setText(String.format("%s ago", mostOfTheTimeCorrectRelativeTime));
        }

        viewHolder.ivStatusIndicator.setImageResource(mPump.getDrawableBasedOnStatus());

        final Double distanceFromOrigin =
                currentUserLocation.distanceInKilometersTo(mPump.getLocation());
        viewHolder.tvPumpDistance.setText(
                String.format("%s km", df.format(distanceFromOrigin.doubleValue())));

        setupLocationLabel(mPump);

        if (mPump.isClaimedByATechnician()) {
            viewHolder.ivStatusIndicator.setImageResource(R.drawable.ic_star_blue);
        }
    }


    private void setupLocationLabel(Pump pump) {
        try {
            viewHolder.tvLocation.setText(AddressUtil.stripCountryFromAddress(pump.getAddress()));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearTextViews() {
        viewHolder.tvLocation.setText("");
        viewHolder.tvLastUpdated.setText("");
        viewHolder.tvPumpDistance.setText("");
    }

    static class ViewHolder {
        TextView tvLastUpdated;
        TextView tvLocation;
        TextView tvPumpDistance;
        ImageView ivStatusIndicator;
    }
}
