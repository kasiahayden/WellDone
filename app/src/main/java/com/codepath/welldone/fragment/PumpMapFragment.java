package com.codepath.welldone.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    private TextView tvPumpName;
    private TextView tvLocation;

    public PumpMapFragment() {
        // Required empty public constructor
    }

    public static PumpMapFragment newInstance(Pump pump) {
        PumpMapFragment fragment = new PumpMapFragment();
        Bundle args = new Bundle();
        args.putString("pumpID", pump.getObjectId());
        fragment.setArguments(args);
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
        if (mPump.getLocation() != null) {
            tvLocation.setText(String.format("%f, %f", mPump.getLocation().getLatitude(), mPump.getLocation().getLongitude()));
        }
        tvPumpName.setText(mPump.getName());
    }

    GoogleMap getMap() {
        return ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
    }

    void onNewReportClicked(View view) {
        ((PumpListAdapter.PumpListListener)getActivity()).onNewReportClicked(mPump);
    }

    /**
     * Hack to not crash when re-entering the map fragment
     * http://stackoverflow.com/a/14484640/143913
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MapFragment f = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        if (f != null)
            getFragmentManager().beginTransaction().remove(f).commit();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pump_map_view, container, false);
        tvPumpName = (TextView)v.findViewById(R.id.tvPumpName);
        tvPumpName.setText("");
        tvLocation = (TextView)v.findViewById(R.id.tvPumpLocation);
        tvLocation.setText("");
        return v;
    }

}
