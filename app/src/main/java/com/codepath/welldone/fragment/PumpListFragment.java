package com.codepath.welldone.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.welldone.PumpListAdapter;
import com.codepath.welldone.PumpRowView;
import com.codepath.welldone.R;
import com.codepath.welldone.activity.PumpBrowser;
import com.codepath.welldone.helper.NetworkUtil;
import com.codepath.welldone.model.AbstractListItem;
import com.codepath.welldone.model.HeaderListItem;
import com.codepath.welldone.model.Pump;
import com.codepath.welldone.model.PumpListItem;
import com.codepath.welldone.persister.PumpPersister;
import com.nhaarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class PumpListFragment extends Fragment implements OnRefreshListener {

    public static final int TARGET_DETAILS_HEIGHT = 130;
    public PumpListAdapter mPumpArrayAdapter;
    private AlphaInAnimationAdapter alphaAdapter;

    private ListView lvPumps;
    private ProgressBar pbLoading;
    private PullToRefreshLayout ptrlPumps;


    private ParseUser currentUser;

    public PumpListAdapter.PumpListListener mListener;
    private final String TAG = "PumpListFragment";

    private boolean shouldExpandSelectedRow;
    public int mCurrentPumpIndex;
    // The default view of pump list is sorted by distance from currently logged-in user.
    private int sortMenuItemSelected;

    private String mCurrentPumpID;

    public PumpListFragment() {}

    /**
     * @return the currently highlighted (expanded) pump
     */
    public Pump getCurrentPump() {
        return mPumpArrayAdapter.getPumpAtIndex(mCurrentPumpIndex);
    }

    public static PumpListFragment newInstance() {
        return new PumpListFragment();
    }

    public static PumpListFragment newInstance(String pumpToDisplay) {
        PumpListFragment frag = new PumpListFragment();
        Bundle b = new Bundle();
        b.putString(PumpBrowser.EXTRA_PUSH_NOTIFICATION_PUMP_OBJECT_ID, pumpToDisplay);
        frag.setArguments(b);
        return frag;
    }


    @Override
    public void onResume() {
        super.onResume();
        fetchAndShowData();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // There's an options menu for this fragment only
        setHasOptionsMenu(true);

        mCurrentPumpIndex = 0;
        mPumpArrayAdapter = new PumpListAdapter((Activity)mListener);

        currentUser = ParseUser.getCurrentUser();
        Log.d("debug", "Current user: " + currentUser.getUsername() + " "
                + currentUser.get("location") + " " + currentUser.getACL());

        final SharedPreferences settings = getActivity().getPreferences(0);
        if (getArguments() != null) {
            mCurrentPumpID = getArguments().getString(PumpBrowser.EXTRA_PUSH_NOTIFICATION_PUMP_OBJECT_ID);
        }
    }

    @Override
    public void onStop() {

        super.onStop();

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        final SharedPreferences settings = getActivity().getPreferences(0);
        final SharedPreferences.Editor editor = settings.edit();
        editor.putInt("lastSortMode", sortMenuItemSelected);

        // Commit the edits!
        editor.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_pump_list, container, false);

        setupViews(v);
        mPumpArrayAdapter.rowListener = (PumpListAdapter.PumpListListener) getActivity();

        ActionBarPullToRefresh.from(getActivity())
                // Here we mark just the ListView and it's Empty View as pullable
                .theseChildrenArePullable(R.id.lvPumps, android.R.id.empty)
                .listener(this)
                .setup(ptrlPumps);

        setupListeners();

        fetchAndShowData();

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

    // Pull to refresh fetches data from the remote server.
    @Override
    public void onRefreshStarted(View view) {

        // Cannot refresh if network is not available.
        if (!NetworkUtil.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity().getBaseContext(),
                    "Could not refresh. Network not available.",
                    Toast.LENGTH_LONG).show();
            ptrlPumps.setRefreshComplete();
            return;
        }

        fetchAndShowRemoteData();
        ptrlPumps.setRefreshComplete();
    }

    /* Private methods */
    private void setupViews(View v) {

        ptrlPumps = (PullToRefreshLayout) v.findViewById(R.id.ptrlPumps);

        lvPumps = (ListView) v.findViewById(R.id.lvPumps);

        alphaAdapter = new AlphaInAnimationAdapter(mPumpArrayAdapter);
        alphaAdapter.setAbsListView(lvPumps);

        lvPumps.setAdapter(alphaAdapter);
        pbLoading = (ProgressBar) v.findViewById(R.id.pbLoading);
    }

    private void setupListeners() {

        lvPumps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                shouldExpandSelectedRow = true;
                Pump pump = mPumpArrayAdapter.getPumpAtIndex(position);
                Log.d("debug", "Clicked on pump " + pump.getObjectId() + " " + pump.getName());

                PumpRowView v = (PumpRowView) view;
                v.toggleExpandedState();

                mCurrentPumpIndex = position;

            }
        });
    }

    // This is done each time before we fetch data both locally or remotely
    private void prepareForDataFetch() {

        pbLoading.setVisibility(ProgressBar.VISIBLE);
        mPumpArrayAdapter.clear();
    }

    // Fetch data (from local or remote source) based on the selected sort option
    private void fetchAndShowData() {
        prepareForDataFetch();
        fetchPumpsInBackground(ParseQuery.getQuery("Pump"), true);
    }

    // Fetch data remotely based on the selected sort option
    private void fetchAndShowRemoteData() {
        prepareForDataFetch();
        fetchPumpsFromRemote(ParseQuery.getQuery("Pump"), true);

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

        Log.d("debug", "Fetching pumps from remote DB.");
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
        Log.d("debug", "Pinning newly fetched pumps " + pumpList.size());
        ParseObject.pinAllInBackground(PumpPersister.ALL_PUMPS, pumpList, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("debug", "Pinned newly fetched pumps " + pumpList.size());
                } else {
                    Log.d("debug", "Couldn't pin pumps: " + e.toString());
                }
            }
        });
    }


    private void addPumpsToAdapter(List<ParseObject> pumpList, boolean additionalSort) {

        List<AbstractListItem> sortedPumps = new ArrayList<AbstractListItem>();
        sortedPumps.add(new HeaderListItem("Broken"));

        for (int i = 0; i < pumpList.size(); i++) {
            Pump pump = (Pump)pumpList.get(i);
            if (pump.isBroken()) {
                sortedPumps.add(new PumpListItem(pump));
            }
        }

        sortedPumps.add(new HeaderListItem("Fix in progress"));

        for (int i = 0; i < pumpList.size(); i++) {
            Pump pump = (Pump)pumpList.get(i);
            if (pump.getCurrentStatus().equals("Fix in progress")) {
                sortedPumps.add(new PumpListItem(pump));
            }
        }

        mPumpArrayAdapter.addAll(sortedPumps);

    }


}
