package com.codepath.welldone;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Outline;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.welldone.helper.AddressUtil;
import com.codepath.welldone.helper.DateTimeUtil;
import com.codepath.welldone.model.Pump;
import com.parse.ParseGeoPoint;

import java.text.DecimalFormat;

public class ExpandablePumpRowView extends RelativeLayout {

    public static final int TARGET_DETAILS_HEIGHT = 900;
    public static final int ANIMATE_IN_DURATION_MILLIS = 300;
    public static final int ANIMATE_OUT_DURATION_MILLIS = 500;
    public static final int CIRCULAR_REVEAL_DURATION_NAVIGATE = 500;

    private ViewGroup detailsContainer;

    public ViewHolder viewHolder;
    View fabStartNavigation;
    View fabEndNavigation;
    View mNavigationOverlayViewToBeRevealed;


    private DecimalFormat df = new DecimalFormat("#.#");

    public Pump mPump;

    public ExpandablePumpRowView(Context context, AttributeSet attrs){
        super(context, attrs);
        View.inflate(context, R.layout.expandable_row_pump, this);
        detailsContainer = (ViewGroup)findViewById(R.id.vgDetailsContainer);
        detailsContainer.setVisibility(View.GONE);
        mNavigationOverlayViewToBeRevealed = findViewById(R.id.viewToBeRevealed);

        viewHolder = new ViewHolder();
        populateViewHolder();


        int size = getResources().getDimensionPixelSize(R.dimen.fab_size);
        fabStartNavigation = findViewById(R.id.fabStartNavigate);
        fabEndNavigation = findViewById(R.id.fabEndNavigate);
        setOutlinesOnFabs(size);
        fabEndNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beginAnimationToUnrevealEndNavFAB();
                beginAnimationToUnrevealNavigationOverlayView();
            }
        });

        fabStartNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beginAnimationToRevealNavigationOverviewAndHidePager();
                beginAnimationToRevealEndNavFAB();
            }
        });
    }
    private void setOutlinesOnFabs(int size) {
        Outline outline = new Outline();
        outline.setOval(0, 0, size, size);
        fabStartNavigation.setOutline(outline);
        fabEndNavigation.setOutline(outline);
    }
    private void beginAnimationToRevealEndNavFAB() {
        int xpos = 0;
        int ypos = 0;
        int finalRadius = 320; /// Width of the FAB, hopefully
        ValueAnimator revealEndButton = ViewAnimationUtils.createCircularReveal(fabEndNavigation, xpos, ypos, 0, finalRadius);
        Log.d("DBG", String.format("Revealing fabEnd from x:%d, y:%d, width:%d", xpos, ypos, finalRadius));
        revealEndButton.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                fabEndNavigation.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                fabStartNavigation.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        revealEndButton.setDuration(CIRCULAR_REVEAL_DURATION_NAVIGATE);
        revealEndButton.start();
    }

    private void beginAnimationToUnrevealNavigationOverlayView() {
        int xpos = 0;
        int ypos = 0;
        int beginWidth = 1000; /// Width of the FAB, hopefully
        ValueAnimator revealStartButton = ViewAnimationUtils.createCircularReveal(mNavigationOverlayViewToBeRevealed, xpos, ypos, beginWidth, 0);
        Log.d("DBG", String.format("Unrevealing fabEnd from x:%d, y:%d, begin width:%d", xpos, ypos, beginWidth));
        revealStartButton.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mNavigationOverlayViewToBeRevealed.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        revealStartButton.setDuration(CIRCULAR_REVEAL_DURATION_NAVIGATE);
        revealStartButton.start();
    }

    private void beginAnimationToUnrevealEndNavFAB() {
        int xpos = 0;
        int ypos = 0;
        int finalRadius = 320; /// Width of the FAB, hopefully
        ValueAnimator revealStartButton = ViewAnimationUtils.createCircularReveal(fabEndNavigation, xpos, ypos, finalRadius, 0);
        Log.d("DBG", String.format("Revealing fabEnd from x:%d, y:%d, width:%d", xpos, ypos, finalRadius));
        revealStartButton.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                fabStartNavigation.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                fabEndNavigation.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        revealStartButton.setDuration(CIRCULAR_REVEAL_DURATION_NAVIGATE);
        revealStartButton.start();
    }

    private void beginAnimationToRevealNavigationOverviewAndHidePager() {
        int xpos = this.getRight();
        int ypos = 0;
        int finalRadius = this.getWidth() * 2;
        Log.d("DBG", String.format("Revealing navigation overlay view from x:%d, y:%d, width:%d", xpos, ypos, finalRadius));
        ValueAnimator reveal = ViewAnimationUtils.createCircularReveal(
                mNavigationOverlayViewToBeRevealed,
                xpos,
                ypos,
                112,
                finalRadius);
        reveal.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                mNavigationOverlayViewToBeRevealed.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        reveal.setDuration(CIRCULAR_REVEAL_DURATION_NAVIGATE);
        reveal.start();
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
        viewHolder.tvLastUpdated = (TextView)findViewById(R.id.tvPumpLastUpdated);
        viewHolder.tvLocation = (TextView)findViewById(R.id.tvPumpLocation);
        viewHolder.tvPumpDistance = (TextView)findViewById(R.id.tvPumpDistance);
        viewHolder.tvFlavor = (TextView)findViewById(R.id.tvFlavor);
        viewHolder.tvMostRecentUpdate = (TextView)findViewById(R.id.tvMostRecentUpdate);
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
        viewHolder.tvFlavor.setText(getResources().getString(R.string.default_pump_flavor_text, mPump.getName(), mPump.getName()));
        viewHolder.ivStatusIndicator.setImageResource(mPump.isBroken() ?
                R.drawable.ic_well_broken:
                R.drawable.ic_well_working);

        final Double distanceFromOrigin =
                currentUserLocation.distanceInKilometersTo(mPump.getLocation());
        viewHolder.tvPumpDistance.setText(
                String.format("%s km", df.format(distanceFromOrigin.doubleValue())));

        setupLocationLabel(mPump);
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
        viewHolder.tvFlavor.setText("");
    }

    static class ViewHolder {
        TextView tvLastUpdated;
        TextView tvLocation;
        TextView tvPumpDistance;
        TextView tvFlavor;
        TextView tvMostRecentUpdate;
        ImageView ivStatusIndicator;
    }
}
