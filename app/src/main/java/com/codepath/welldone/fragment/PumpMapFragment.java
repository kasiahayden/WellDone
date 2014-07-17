package com.codepath.welldone.fragment;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.welldone.PumpListAdapter;
import com.codepath.welldone.R;
import com.codepath.welldone.model.Pump;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class PumpMapFragment extends Fragment {

    private Pump mPump;
    private String mPumpID;

    private MapFragment mapFragment;

    private static final String TAG = "PumpMapFragment";

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
        if (getArguments() != null) {
            mPumpID = getArguments().getString("pumpID");

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Pump");
            query.getInBackground(mPumpID, new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        mPump = (Pump)object;
                        redrawUI();
                    }
                }
            });
        }
        mapFragment = new MapFragment();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void addPipsToMap() {
        GoogleMap map = getMap();
        MarkerOptions options = new MarkerOptions();
        double lat = mPump.getLocation().getLatitude();
        double longitude = mPump.getLocation().getLongitude();
        LatLng position = new LatLng(lat, longitude);
        options.position(position);
        map.addMarker(options);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(position);
        LatLngBounds bounds = builder.build();
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
    }

    private void redrawUI() {
        addPipsToMap();
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
        return v;
    }

}
