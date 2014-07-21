package com.codepath.welldone.fragment;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class PumpMapFragment extends Fragment {
    public static final double MAP_DISPLAY_DELTA = 0.03;
    public Pump mPump;
    private MapFragment mapFragment;

    ViewPager mDetailsPager;

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
                        getMap().clear();
                        addPipsToMap();
                        mDetailsPager.setAdapter(getViewPagerAdapter());
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
            if (pump == mPump) {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mappin_enormous));
            }
            else {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mappin));
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

    void onNewReportClicked() {
        ((PumpListAdapter.PumpListListener)getActivity()).onNewReportClicked(mPump);
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
                pumpRow.updateSubviews(mPumpListAdapter.getItem(position));
                container.addView(pumpRow);

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
