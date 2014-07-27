package com.codepath.welldone;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.welldone.helper.AddressUtil;
import com.codepath.welldone.helper.DateTimeUtil;
import com.codepath.welldone.model.Pump;
import com.codepath.welldone.model.Report;
import com.codepath.welldone.persister.ReportPersister;
import com.parse.ParseGeoPoint;

import java.io.IOException;
import java.text.DecimalFormat;

public class PumpRowView extends RelativeLayout {

    public static final int TARGET_DETAILS_HEIGHT = 900;
    public static final int ANIMATE_IN_DURATION_MILLIS = 300;
    public static final int ANIMATE_OUT_DURATION_MILLIS = 500;

    public Button newReportButton;
    public Button navigateButton;
    private ViewGroup detailsContainer;

    public ViewHolder viewHolder;

    private DecimalFormat df = new DecimalFormat("#.#");

    public Pump mPump;

    public PumpRowView(Context context, AttributeSet attrs){
        super(context, attrs);
        View.inflate(context, R.layout.row_pump, this);
        newReportButton = (Button)findViewById(R.id.btnNewReport);
        navigateButton = (Button)findViewById(R.id.btnNavigate);
        detailsContainer = (ViewGroup)findViewById(R.id.vgDetailsContainer);
        detailsContainer.setVisibility(View.GONE);

        viewHolder = new ViewHolder();
        populateViewHolder();
    }

    public void toggleExpandedState() {
        boolean expanded = detailsContainer.getVisibility() == View.VISIBLE;
        if (expanded) {
            DropDownAnim anim = new DropDownAnim(detailsContainer, TARGET_DETAILS_HEIGHT, false);
            anim.setDuration(ANIMATE_OUT_DURATION_MILLIS);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    detailsContainer.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            detailsContainer.startAnimation(anim);
        }
        else {
            detailsContainer.setVisibility(View.VISIBLE);
            DropDownAnim anim = new DropDownAnim(detailsContainer, TARGET_DETAILS_HEIGHT, true);
            anim.setDuration(ANIMATE_IN_DURATION_MILLIS);
            detailsContainer.startAnimation(anim);
        }
    }

    private void populateViewHolder() {
        viewHolder.ivPump = (ImageView)findViewById(R.id.ivPump);
        viewHolder.tvLastUpdated = (TextView)findViewById(R.id.tvPumpLastUpdated);
        viewHolder.tvPriority = (TextView)findViewById(R.id.tvPriority);
        viewHolder.tvStatus = (TextView)findViewById(R.id.tvPumpStatus);
        viewHolder.tvLocation = (TextView)findViewById(R.id.tvPumpLocation);
        viewHolder.tvPumpDistance = (TextView)findViewById(R.id.tvPumpDistance);
        viewHolder.tvFlavor = (TextView)findViewById(R.id.tvFlavor);
        viewHolder.tvMostRecentUpdate = (TextView)findViewById(R.id.tvMostRecentUpdate);
    }

    public void updateSubviews(ParseGeoPoint currentUserLocation) {

        viewHolder.tvLastUpdated.setText(String.format("%s ago",
                DateTimeUtil.getRelativeTimeofTweet(mPump.getUpdatedAt().toString())));
        viewHolder.tvStatus.setText(mPump.getCurrentStatus());
        if (mPump.isBroken()) {
            viewHolder.tvStatus.setTextColor(getResources().getColor(R.color.textRed));
        }
        viewHolder.tvPriority.setText(String.format("Priority Level %d", mPump.getPriority()));
        viewHolder.tvFlavor.setText(getResources().getString(R.string.default_pump_flavor_text, mPump.getName(), mPump.getName()));

        new AsyncTask<Void, Void, Report>() {
            @Override
            protected Report doInBackground(Void... params) {
                return ReportPersister.getLatestReportForPump(mPump);
            }

            @Override
            protected void onPostExecute(Report report) {
                if (report != null) {
                    viewHolder.tvMostRecentUpdate.setText(report.getNotes());
                }
            }
        }.execute();

        final Double distanceFromOrigin =
                currentUserLocation.distanceInKilometersTo(mPump.getLocation());
        viewHolder.tvPumpDistance.setText(
                String.format("%s km", df.format(distanceFromOrigin.doubleValue())));

        setPumpToRandomImage();
        setupLocationLabel(mPump);
    }

    private void setPumpToRandomImage() {
        String filename = String.format("pump%d.png", 1 + Math.abs(mPump.getHash()) % 8);
        try {
            viewHolder.ivPump.setImageDrawable(Drawable.createFromStream(getContext().getAssets().open(filename), null));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRootBackgroundColor(int colorResource) {
        View v = findViewById(R.id.vgPumpRoot);
        v.setBackgroundColor(colorResource);
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
        viewHolder.tvStatus.setText("");
        viewHolder.tvStatus.setTextColor(getResources().getColor(R.color.darkGrayTextV2));
        viewHolder.tvPriority.setText("");
        viewHolder.tvLastUpdated.setText("");
        viewHolder.tvPumpDistance.setText("");
        viewHolder.tvFlavor.setText("");
    }

    static class ViewHolder {
        ImageView ivPump;
        TextView tvLastUpdated;
        TextView tvLocation;
        TextView tvStatus;
        TextView tvPriority;
        TextView tvPumpDistance;
        TextView tvFlavor;
        TextView tvMostRecentUpdate;
    }
}
