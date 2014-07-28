package com.codepath.welldone.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.welldone.PumpListAdapter;
import com.codepath.welldone.PumpListListener;
import com.codepath.welldone.PumpRowView;
import com.codepath.welldone.R;
import com.codepath.welldone.activity.CreateReportActivity;
import com.codepath.welldone.model.Pump;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

public class PumpMapFragment extends Fragment {
    public static final String EXTRA_PUMP_ID_TO_DISPLAY = "pumpIDToDisplay";
    public static final double MAP_DISPLAY_DELTA = 0.96;
    public Pump mPump;
    public PumpListAdapter mPumpListAdapter;
    private SupportMapFragment mapFragment;
    private ParseGeoPoint currentUserLocation;
    ViewPager mDetailsPager;


    public PumpMapFragment() {
        // Required empty public constructor
    }

    public static PumpMapFragment newInstance() {
        PumpMapFragment fragment = new PumpMapFragment();
        return fragment;
    }

    public static PumpMapFragment newInstance(String pumpToDisplay) {
        PumpMapFragment frag = new PumpMapFragment ();
        Bundle b = new Bundle();
        b.putString(EXTRA_PUMP_ID_TO_DISPLAY, pumpToDisplay);
        frag.setArguments(b);
        return frag;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapFragment = new SupportMapFragment();
        currentUserLocation = (ParseGeoPoint) ParseUser.getCurrentUser().get("location");
    }

    @Override
    public void onResume() {
        Log.d("DBG", "Map resuming.");
        super.onResume();
        if (mPump != null && getMap() != null) {
            centerMapOnPump(mPump);
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                resetMapUIAndCardView();
            }
        }, 500);
    }

    public void resetMapUIAndCardView() {
        getMap().getUiSettings().setZoomControlsEnabled(false);
        getMap().clear();
        addPipsToMap();
        mDetailsPager.setAdapter(getViewPagerAdapter());
        centerMapOnPump(mPump);
//        mDetailsPager.setCurrentItem(((PumpBrowser)getActivity()).getCurrentPumpIndex());
    }

    private void addPipsToMap() {
        GoogleMap map = getMap();
        if (map == null || mPumpListAdapter == null) {
            return;
        }
        for (int i = 0; i < mPumpListAdapter.getTotalPumpCount(); i++) {
            final Pump pump = mPumpListAdapter.getPumpAtIndex(i);
            MarkerOptions options = new MarkerOptions();
            if (pump.getCurrentStatus().equalsIgnoreCase("broken")) {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_broken_unfocused));
            }
            else {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_working_unfocused));
            }
            double lat = pump.getLocation().getLatitude();
            double longitude = pump.getLocation().getLongitude();
            LatLng position = new LatLng(lat, longitude);
            options.position(position);
            map.addMarker(options);
        }
        centerMapOnPump(mPump);
    }

    void centerMapOnPump(Pump pump) {
        if (pump == null) {
            return;
        }
        double lat = pump.getLocation().getLatitude();
        double longitude = pump.getLocation().getLongitude();
        LatLng positionTopLeft = new LatLng(lat - MAP_DISPLAY_DELTA, longitude - MAP_DISPLAY_DELTA);
        LatLng fartherAwayPosition = new LatLng(lat + MAP_DISPLAY_DELTA, longitude + MAP_DISPLAY_DELTA);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(positionTopLeft);
        builder.include(fartherAwayPosition);
        LatLngBounds bounds = builder.build();
        if (getMap() != null) {
            getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
        }
    }

    GoogleMap getMap() {
        return mapFragment.getMap();
    }

    void onNewReportClicked(Pump pump) {
        ((PumpListListener)getActivity()).onNewReportClicked(pump);
    }

    PagerAdapter getViewPagerAdapter() {
        return new PagerAdapter() {
            @Override
            public int getCount() {
                if (mPumpListAdapter == null) {
                    return 0;
                }
                return mPumpListAdapter.getCount();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            /*
            A very simple PagerAdapter may choose to use the page Views themselves as key objects,
            returning them from instantiateItem(ViewGroup, int) after creation and adding them to
            the parent ViewGroup. A matching destroyItem(ViewGroup, int, Object) implementation
            would remove the View from the parent ViewGroup and isViewFromObject(View, Object)
            could be implemented as return view == object;.
             */
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                final PumpRowView pumpRow = new PumpRowView(getActivity(), null);
                pumpRow.setRootBackgroundColor(getResources().getColor(android.R.color.transparent));
                final Pump thePump = mPumpListAdapter.getPumpAtIndex(position);
                pumpRow.mPump = thePump;
                pumpRow.updateSubviews( currentUserLocation);
                container.addView(pumpRow);
                pumpRow.newReportButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onNewReportClicked(thePump);
                    }
                });
                pumpRow.navigateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ParseGeoPoint point = (ParseGeoPoint)ParseUser.getCurrentUser().get("location");
                        String fromLocation = String.format("%s,%s", point.getLatitude(), point.getLongitude());
                        CreateReportActivity.askAboutPumpNavigation(getActivity(), fromLocation, thePump, "Open in Maps?", false);
                    }
                });

                pumpRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pumpRow.toggleExpandedState();
                    }
                });

                return pumpRow;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                PumpRowView prv = (PumpRowView)object;
                container.removeView(prv);
            }

        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pump_map_view, container, false);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.vgMapContainer, mapFragment);
        ft.commit();

        mDetailsPager = (ViewPager)v.findViewById(R.id.vpPumpRows);
        return v;
    }

}
