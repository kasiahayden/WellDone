package com.codepath.welldone.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.codepath.welldone.PumpListAdapter;
import com.codepath.welldone.R;
import com.codepath.welldone.activity.PumpDetails;
import com.codepath.welldone.model.Pump;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class PumpListFragment extends Fragment {

    public static final String ARG_PUMP = "pump";

    // XXX debug option only: toggle this to select local vs. remote DB.
    private static final boolean useLocal = true;
    private ArrayAdapter<Pump> mPumpArrayAdapter;
    private ListView mPumpList;

    Pump mPump;

    public OnFragmentInteractionListener mListener;

    public static PumpListFragment newInstance(Pump pump) {
        PumpListFragment fragment = new PumpListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PUMP, pump);
        fragment.setArguments(args);
        return fragment;
    }
    public PumpListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPump = (Pump)getArguments().getSerializable(ARG_PUMP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pump_list, container, false);

        mPumpArrayAdapter = new PumpListAdapter((Activity)mListener);
        mPumpList = (ListView)v.findViewById(R.id.lvPumps);
        mPumpList.setAdapter(mPumpArrayAdapter);

        mPumpList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Pump pump = (Pump)parent.getItemAtPosition(position);
                Intent intent = new Intent((Activity)mListener, PumpDetails.class);
                intent.putExtra("pump", pump);
                startActivity(intent);
            }
        });

        triggerFetchAndRedraw();

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }


    void triggerFetchAndRedraw() {

        mPumpArrayAdapter.clear();

        if (useLocal) {
            fetchFromLocalDataStore();
        } else {
            fetchFromRemoteDataSource();
        }
    }

    private void fetchFromLocalDataStore() {

        Log.d("debug", "Fetching data from local data source");
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Pump");
        //query.include("currentStatus");
        query.fromLocalDatastore();
        runQueryInBackground(query);
    }

    private void fetchFromRemoteDataSource() {

        Log.d("debug", "Fetching data from remote data source");
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Pump");
        runQueryInBackground(query);
    }

    private void runQueryInBackground(ParseQuery<ParseObject> query) {

        query.findInBackground(new FindCallback<ParseObject>() {

            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ParseObject.pinAllInBackground(objects);
                    for (ParseObject object : objects) {
                        final Pump pump = (Pump) object;
                        Log.d("debug", "Fetched pump: " + pump.getStatus() + " " + pump.getObjectId());
                        //mPumpArrayAdapter.notifyDataSetChanged();
                        mPumpArrayAdapter.add(pump);
                    }
                } else {
                    Log.d("debug", "Exception: " + e);
                }
            }
        });
    }

}
