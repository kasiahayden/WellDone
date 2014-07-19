package com.codepath.welldone.fragment;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.welldone.PumpListAdapter;
import com.codepath.welldone.PumpRowView;
import com.codepath.welldone.R;
import com.codepath.welldone.model.Pump;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class PumpMapFragment extends Fragment {
    public static final double MAP_DISPLAY_DELTA = 0.03;
    public Pump mPump;
    private MapFragment mapFragment;
    PumpRowView pumpRowView;

    public PumpListAdapter mPumpListAdapter;

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
        mapFragment = new MapFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPump != null && getMap() != null) {
            pumpRowView.updateSubviews(mPump);
            centerMapOnPump(mPump);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (mPump != null && getMap() != null) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addPipsToMap();
                        pumpRowView.updateSubviews(mPump);
                        centerMapOnPump(mPump);
                    }
                }, 500);
            }
        }
    }

    private void addPipsToMap() {
        GoogleMap map = getMap();
        for (int i = 0; i < mPumpListAdapter.getCount(); i++) {
            Pump pump = mPumpListAdapter.getItem(i);
            MarkerOptions options = new MarkerOptions();
            double lat = pump.getLocation().getLatitude();
            double longitude = pump.getLocation().getLongitude();
            LatLng position = new LatLng(lat, longitude);
            options.position(position);
            map.addMarker(options);
        }
        centerMapOnPump(mPump);
    }

    void centerMapOnPump(Pump pump) {
        double lat = pump.getLocation().getLatitude();
        double longitude = pump.getLocation().getLongitude();
        LatLng positionTopLeft = new LatLng(lat - MAP_DISPLAY_DELTA, longitude - MAP_DISPLAY_DELTA);
        LatLng fartherAwayPosition = new LatLng(lat + MAP_DISPLAY_DELTA, longitude + MAP_DISPLAY_DELTA);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(positionTopLeft);
        builder.include(fartherAwayPosition);
        LatLngBounds bounds = builder.build();
        if (getMap() != null) {
            getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
        }
    }

    GoogleMap getMap() {
        return mapFragment.getMap();
    }

    void onNewReportClicked(View view) {
        ((PumpListAdapter.PumpListListener)getActivity()).onNewReportClicked(mPump);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pump_map_view, container, false);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.vgMapContainer, mapFragment);
        ft.commit();
        pumpRowView = (PumpRowView)v.findViewById(R.id.pumpDetailsView);
        pumpRowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pumpRowView.toggleExpandedState();
            }
        });
        return v;
    }

}
