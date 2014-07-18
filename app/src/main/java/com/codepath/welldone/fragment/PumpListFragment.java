package com.codepath.welldone.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.welldone.DropDownAnim;
import com.codepath.welldone.PumpListAdapter;
import com.codepath.welldone.R;
import com.codepath.welldone.helper.PumpUtil;
import com.codepath.welldone.model.Pump;
import com.codepath.welldone.persister.PumpPersister;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class PumpListFragment extends Fragment {

    public static final int TARGET_DETAILS_HEIGHT = 130;
    private PumpListAdapter mPumpArrayAdapter;
    private ListView mPumpList;
    private ProgressBar pbLoading;
    private ParseUser currentUser;

    public PumpListAdapter.PumpListListener mListener;
    private final String TAG = "PumpListFragment";

    private int mCurrentPumpIndex;

    public PumpListFragment() {}

    /**
     * @return the currently highlighted (expanded) pump
     */
    public Pump getCurrentPump() {
        try {
            return mPumpArrayAdapter.getItem(mCurrentPumpIndex);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PumpListFragment newInstance() {
        return new PumpListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // There's an options menu for this fragment only
        setHasOptionsMenu(true);

        mCurrentPumpIndex = 0;

        currentUser = ParseUser.getCurrentUser();
        Log.d("debug", "Current user: " + currentUser.getUsername() + " " + currentUser.get("location"));
        mPumpArrayAdapter = new PumpListAdapter((Activity)mListener);
    }

    // Add a special menu XML for this fragment only.
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.pump_list, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pump_list, container, false);

        setupViews(v);
        mPumpArrayAdapter.rowListener =  (PumpListAdapter.PumpListListener)getActivity();
        setupListeners();

        pbLoading.setVisibility(ProgressBar.VISIBLE);
        mPumpArrayAdapter.clear();
        fetchPumpsInBackground(ParseQuery.getQuery("Pump"), true /* additional sorting by distance */);

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (PumpListAdapter.PumpListListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sortDistance:
                pbLoading.setVisibility(ProgressBar.VISIBLE);
                mPumpArrayAdapter.clear();
                // XXX Adding the extra boolean param is BAD coding practice, but this is what time
                // allows for. :(
                fetchPumpsInBackground(ParseQuery.getQuery("Pump"), true
                                                   /* apply additional sort, outside of DB query */);
                return true;
            case R.id.sortPriority:
                pbLoading.setVisibility(ProgressBar.VISIBLE);
                mPumpArrayAdapter.clear();
                fetchPumpsInBackground(ParseQuery.getQuery("Pump").orderByAscending("priority"),
                                       false);
                return true;
            case R.id.sortLastUpdated:
                pbLoading.setVisibility(ProgressBar.VISIBLE);
                mPumpArrayAdapter.clear();
                fetchPumpsInBackground(ParseQuery.getQuery("Pump").orderByDescending("updatedAt"),
                                       false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /* Private methods */
    private void setupViews(View v) {

        mPumpList = (ListView) v.findViewById(R.id.lvPumps);
        mPumpList.setAdapter(mPumpArrayAdapter);
        pbLoading = (ProgressBar) v.findViewById(R.id.pbLoading);
    }

    private void setupListeners() {

        mPumpList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Pump pump = (Pump)parent.getItemAtPosition(position);
                Log.d("debug", "Clicked on pump " + pump.getObjectId() + " " + pump.getName());

                final View v = view.findViewById(R.id.vgDetailsContainer);
                boolean expanded = v.getVisibility() == View.VISIBLE;
                if (expanded) {
                    DropDownAnim anim = new DropDownAnim(v, TARGET_DETAILS_HEIGHT, false);
                    anim.setDuration(500);
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            int x = 0; x++;
                            v.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    v.startAnimation(anim);
                }
                else {
                    v.setVisibility(View.VISIBLE);
                    DropDownAnim anim = new DropDownAnim(v, TARGET_DETAILS_HEIGHT, true);
                    anim.setDuration(500);
                    v.startAnimation(anim);
                }
                mCurrentPumpIndex = position;

            }
        });
    }

    /**
     * Fetch pumps in background.
     * First start with querying the local pin. If nothing there, query the remote source.
     * @param query
     */
    private void fetchPumpsInBackground(final ParseQuery<ParseObject> query,
                                        final boolean additionalSort) {

        query.fromPin(PumpPersister.ALL_PUMPS);

        query.findInBackground(new FindCallback<ParseObject>() {

            public void done(final List<ParseObject> pumpList, ParseException e) {

                if (e == null) {

                    Log.d("info", "Fetching pumps from local DB. Found " + pumpList.size());

                    if (pumpList.size() == 0) {
                        fetchPumpsFromRemote(ParseQuery.getQuery("Pump"), additionalSort);
                    } else {
                        pbLoading.setVisibility(ProgressBar.INVISIBLE);
                        Log.d("debug", "Using pumps fetched from local DB.");
                        addPumpsToAdapter(pumpList, additionalSort);
                    }
                } else {
                    Log.d("error", "Exception while fetching pumps: " + e);
                    pbLoading.setVisibility(ProgressBar.INVISIBLE);
                }
            }
        });
    }

    /**
     * Fetch pumps from remote source.
     * @param query
     */
    private void fetchPumpsFromRemote(ParseQuery<ParseObject> query,
                                      final boolean additionalSort) {

        query.findInBackground(new FindCallback<ParseObject>() {

            public void done(final List<ParseObject> pumpList, ParseException e) {

                pbLoading.setVisibility(ProgressBar.INVISIBLE);
                if (e == null) {
                    Log.d("info", "Fetching pumps from remote DB. Found " + pumpList.size());
                    addPumpsToAdapter(pumpList, additionalSort);

                    // Unpin previously cached data and re-pin the newly fetched.
                    if (pumpList != null && !pumpList.isEmpty()) {
                        unpinAndRepin(pumpList);
                    }

                } else {
                    Log.d("error", "Exception while fetching remote pumps: " + e);
                }
            }
        });
    }

    /**
     * Unpin and repin a given list of pumps.
     * This method is public as other classes might want to call it, potentially.
     * @param pumpList
     */
    private void unpinAndRepin(final List<ParseObject> pumpList) {

        Log.d("debug", "Unpinning previously saved objects");
        ParseObject.unpinAllInBackground(PumpPersister.ALL_PUMPS, pumpList,
                new DeleteCallback() {
                    public void done(ParseException e) {
                        if (e != null) {
                            // There was some error.
                            return;
                        } else {
                            Log.d("info", pumpList.size() + " previous cached pumps deleted.");
                        }
                    }
                }
        );
        // Add the latest results for this query to the cache.
        Log.d("debug", "Pinning newly retrieved objects");
        ParseObject.pinAllInBackground(PumpPersister.ALL_PUMPS, pumpList);
    }

    private void addPumpsToAdapter(List<ParseObject> pumpList, boolean additionalSort) {

        // return the results as they are
        if (!additionalSort) {
            for (ParseObject object : pumpList) {
                final Pump pump = (Pump) object;
                mPumpArrayAdapter.add(pump);
            }
            return;
        }
        // Apply custom sorting (outside of DB query) to sort by distance from current user.
        final List<Pump> pumps = new ArrayList<Pump>(pumpList.size());
        for (ParseObject object : pumpList) {
            final Pump pump = (Pump) object;
            pumps.add(pump);
        }
        pumpList = null;
        final List<Pump> sortedPumps =
                PumpUtil.sortPumps(pumps, ((ParseGeoPoint) currentUser.get("location")));
        for (Pump pump : sortedPumps) {
            mPumpArrayAdapter.add(pump);
        }
    }
}
