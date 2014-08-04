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

import com.codepath.welldone.ExpandablePumpRowView;
import com.codepath.welldone.ExternalNavigation;
import com.codepath.welldone.PumpListAdapter;
import com.codepath.welldone.R;
import com.codepath.welldone.TechnicianArrayList;
import com.codepath.welldone.model.Pump;
import com.codepath.welldone.model.Technician;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

public class PumpMapFragment extends Fragment implements ExpandablePumpRowView.PumpRowDelegate, GoogleMap.OnMarkerClickListener {
    public static final double MAP_DISPLAY_DELTA = 0.03;
    public static final String MARKER_TECH_TITLE = "TECH";
    public Pump mPump;
    public PumpListAdapter mPumpListAdapter;
    public TechnicianArrayList mTechnicianArrayList = new TechnicianArrayList();
    private SupportMapFragment mapFragment;
    private ParseGeoPoint currentUserLocation;
    ViewPager mDetailsPager;
    public PagerAdapter mMapPagerAdapter;

    public PumpMapFragment() {
        // Required empty public constructor
    }

    public static PumpMapFragment newInstance() {
        PumpMapFragment fragment = new PumpMapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DBG", String.format(this.getClass().toString(), "onCreate"));
        mapFragment = new SupportMapFragment();
        currentUserLocation = (ParseGeoPoint) ParseUser.getCurrentUser().get("location");
    }

    @Override
    public void onResume() {
        Log.d("DBG", "Map resuming.");
        super.onResume();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                resetMapUIAndCardView();
            }
        }, 500);
    }


    public void resetMapUIAndCardView() {
        resetMapUI();
        resetCardView();
    }

    private void resetMapUI() {
        getMap().getUiSettings().setZoomControlsEnabled(false);
        getMap().clear();
        addPipsToMap();
        centerMapOnPump(mPump);
    }

    private void resetCardView() {
        mDetailsPager.setAdapter(getViewPagerAdapter());
    }

    private void addPipsToMap() {
        GoogleMap map = getMap();
        double lat;
        double longitude;
        LatLng position;
        if (map == null || mPumpListAdapter == null) {
            return;
        }
        for (int i = 0; i < mTechnicianArrayList.getTotalTechCount(); i++) {
            final Technician tech = (Technician) mTechnicianArrayList.TechArray.get(i);
            MarkerOptions options = new MarkerOptions();
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_technician));
            lat = tech.getLatitude();
            longitude = tech.getLongitude();
            position = new LatLng(lat, longitude);
            options.position(position);
            options.title(MARKER_TECH_TITLE);
            map.addMarker(options);
        }
        for (int i = 0; i < mPumpListAdapter.getTotalPumpCount(); i++) {
            final Pump pump = mPumpListAdapter.getPumpAtIndex(i);
            MarkerOptions options = new MarkerOptions();
            if (pump.getCurrentStatus().equalsIgnoreCase("broken")) {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_broken));
            }
            else if (pump.getCurrentStatus().equalsIgnoreCase("Fix in progress")) {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_fix_in_progress));
            }
            else {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_good));
            }
            lat = pump.getLocation().getLatitude();
            longitude = pump.getLocation().getLongitude();
            position = new LatLng(lat, longitude);
            options.position(position);
            map.addMarker(options);
        }
        // Setting a custom info window adapter for the google map
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {
                View v = getActivity().getLayoutInflater().inflate(R.layout.marker_info_window, null);
                return v;
            }
        });
        map.setOnMarkerClickListener(this);
        centerMapOnPump(mPump);
    }

    public boolean onMarkerClick(Marker marker) {
        if (marker.getTitle() != null && marker.getTitle().equals(MARKER_TECH_TITLE)) {
            marker.showInfoWindow();
            //Toast.makeText(getActivity(), "technician clicked", Toast.LENGTH_SHORT).show();
            Log.w("PumpMapFragment", "Clicked on map marker for technician");
        }
        return true; // false defaults to showing infoWindow set for every marker
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

    PagerAdapter getViewPagerAdapter() {
        mMapPagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                int returnValue;
                if (mPumpListAdapter == null) {
                    returnValue = 0;
                } else {
                    returnValue = mPumpListAdapter.getTotalPumpCount();
                }
                return returnValue;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                final ExpandablePumpRowView pumpRow = new ExpandablePumpRowView(getActivity(), null);
                pumpRow.rowDelegate = PumpMapFragment.this;
                final Pump thePump = mPumpListAdapter.getPumpAtIndex(position);
                pumpRow.mPump = thePump;
                pumpRow.updateSubviews(currentUserLocation);
                container.addView(pumpRow);

                pumpRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pumpRow.onRowClick();
                    }
                });

                pumpRow.setTag(position);

                return pumpRow;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                ExpandablePumpRowView prv = (ExpandablePumpRowView) object;
                container.removeView(prv);
            }

        };
        return mMapPagerAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pump_map_view, container, false);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.vgMapContainer, mapFragment);
        ft.commit();

        mDetailsPager = (ViewPager)v.findViewById(R.id.vpPumpRows);
        mDetailsPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                onPumpPagerSwitchedPages(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        return v;
    }

    private void onPumpPagerSwitchedPages(int i) {
        Pump currentPump = mPumpListAdapter.getPumpAtIndex(i);
        Log.d("DBG", String.format("Centering on pump %s with status %s", currentPump.getAddress(), currentPump.getCurrentStatus()));
        centerMapOnPump(currentPump);
        mPump = currentPump;
    }

    @Override
    public void onPumpNavigateClicked(Pump pumpThatWasClicked) {
        ExternalNavigation.askAboutPumpNavigation(getActivity(), ExternalNavigation.HARD_CODED_START_LOCAITON, pumpThatWasClicked, false);
    }

    @Override
    public void onPumpClaimClicked(Pump pumpThatWasClicked) {
        mPump = pumpThatWasClicked;
        resetMapUI();
        mPumpListAdapter.notifyDataSetChanged();
        mMapPagerAdapter.notifyDataSetChanged();
    }

    public void setCurrentlyDisplayedPump(Pump p) {
        int currentPumpIndexInBottomPagerThingy = mPumpListAdapter.getPumpIndexBetweenZeroAndNumberOfPumps(p);
        mDetailsPager.setCurrentItem(currentPumpIndexInBottomPagerThingy, true);
        centerMapOnPump(p);
    }

    public void animateCurrentPumpToUpdateItself(final Pump currentPump) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int index = mPumpListAdapter.getPumpIndexBetweenZeroAndNumberOfPumps(currentPump);
                mDetailsPager.setCurrentItem(index, true);
            }
        }, 1000);
    }
}
