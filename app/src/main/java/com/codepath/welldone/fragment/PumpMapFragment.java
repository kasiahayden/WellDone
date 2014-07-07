package com.codepath.welldone.fragment;



import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.welldone.R;
import com.codepath.welldone.model.Pump;

public class PumpMapFragment extends Fragment {

    private Pump mPump;
    private String mPumpID;

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
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pump_map_view, container, false);

        return v;
    }

}
