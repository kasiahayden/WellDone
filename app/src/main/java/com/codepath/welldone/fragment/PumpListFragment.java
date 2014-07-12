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
import android.view.animation.Animation;
import android.view.animation.Transformation;
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
    private static final boolean useLocal = false;
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
                Log.d("debug", "Clicked on pump " + pump.getObjectId() + " " + pump.getName());


                View v = view.findViewById(R.id.vgDetailsContainer);
                DropDownAnim anim = new DropDownAnim(v, 100, true);
                anim.setDuration(500);
                v.startAnimation(anim);

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


    public void triggerFetchAndRedraw() {

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
        query.fromLocalDatastore();
        runQueryInBackground(query);
    }

    public class DropDownAnim extends Animation {
        private final int targetHeight;
        private final View view;
        private final boolean down;

        public DropDownAnim(View view, int targetHeight, boolean down) {
            this.view = view;
            this.targetHeight = targetHeight;
            this.down = down;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int newHeight;
            if (down) {
                newHeight = (int) (targetHeight * interpolatedTime);
            } else {
                newHeight = (int) (targetHeight * (1 - interpolatedTime));
            }
            view.getLayoutParams().height = newHeight;
            view.requestLayout();
        }

        @Override
        public void initialize(int width, int height, int parentWidth,
                               int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
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
                        Log.d("debug", "Fetched pump: " + pump.getName() + " " + pump.getObjectId());
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
