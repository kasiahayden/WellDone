package com.codepath.welldone;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.welldone.helper.AddressUtil;
import com.codepath.welldone.helper.DateTimeUtil;
import com.codepath.welldone.model.Pump;
import com.parse.ParseGeoPoint;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

public class PumpRowView extends RelativeLayout {

    public ViewHolder viewHolder;

    private DecimalFormat df = new DecimalFormat("#.#");

    public Pump mPump;

    private ImageView mGraphWord;
    private Context mContext;

    public PumpRowView(Context context, AttributeSet attrs){
        super(context, attrs);
        mContext = context;
        View.inflate(context, R.layout.row_pump, this);
        viewHolder = new ViewHolder();
        populateViewHolder();
    }

    private void populateViewHolder() {
        viewHolder.tvLastUpdated = (TextView)findViewById(R.id.tvPumpLastUpdated);
        viewHolder.tvLocation = (TextView)findViewById(R.id.tvPumpLocation);
        viewHolder.tvPumpDistance = (TextView)findViewById(R.id.tvPumpDistance);
        viewHolder.ivStatusIndicator = (ImageView)findViewById(R.id.ivPumpStatusIndicator);
        mGraphWord = (ImageView)findViewById(R.id.ivGraphWord);
    }

    private Bitmap getBitmapFromAsset(String strName) {
        AssetManager assetManager = mContext.getAssets();
        InputStream istr = null;
        try {
            istr = assetManager.open(strName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(istr);
        return bitmap;
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

        String fname = String.format("listviewSparkline%d.png", Math.abs(mPump.getHash()) % 9);
        mGraphWord.setImageBitmap(getBitmapFromAsset(fname));

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
