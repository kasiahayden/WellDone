package com.codepath.welldone.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class PumpListFragment extends Fragment implements OnRefreshListener {

    public static final int TARGET_DETAILS_HEIGHT = 130;
    public PumpListAdapter mPumpArrayAdapter;

    private ListView lvPumps;
    private ProgressBar pbLoading;
    private PullToRefreshLayout ptrlPumps;

    private ParseUser currentUser;

    public PumpListAdapter.PumpListListener mListener;
    private final String TAG = "PumpListFragment";

    public int mCurrentPumpIndex;
    // The default view of pump list is sorted by distance from currently logged-in user.
    private int sortMenuItemSelected;

    private String mCurrentPumpID;

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

    public static PumpListFragment newInstance(String pumpToDisplay) {
        PumpListFragment frag = new PumpListFragment();
        Bundle b = new Bundle();
        b.putString(PumpBrowser.EXTRA_PUSH_NOTIFICATION_PUMP_OBJECT_ID, pumpToDisplay);
        frag.setArguments(b);
        return frag;
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
        sortMenuItemSelected = settings.getInt("lastSortMode", R.id.sortDistance);

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

    // Add a special menu XML for this fragment only.
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.pump_list, menu);
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

    /* This method is overridden so that the correct menu option is shown as checked after
       pull-to-refresh is done.
     */
    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        switch (sortMenuItemSelected) {
            case R.id.sortDistance:
                menu.findItem(R.id.sortDistance).setChecked(true);
                break;
            case R.id.sortPriority:
                menu.findItem(R.id.sortPriority).setChecked(true);
                break;
            case R.id.sortLastUpdated:
                menu.findItem(R.id.sortLastUpdated).setChecked(true);
                break;
            default:
                super.onPrepareOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Only one item in the group is selected; no need to explicitly un-check any item.
        switch (item.getItemId()) {
            case R.id.sortDistance:
                sortMenuItemSelected = R.id.sortDistance;
                if (!item.isChecked()) {
                    prepareForDataFetch();
                    // XXX Adding the extra boolean param is BAD coding practice, but this is what time
                    // allows for. :(
                    fetchPumpsInBackground(ParseQuery.getQuery("Pump"), true
                                           /* apply additional sort, outside of DB query */);
                    item.setChecked(true);
                }
                return true;
            case R.id.sortPriority:
                sortMenuItemSelected = R.id.sortPriority;
                if (!item.isChecked()) {
                    prepareForDataFetch();
                    fetchPumpsInBackground(ParseQuery.getQuery("Pump").orderByAscending("priority"),
                                           false);
                    item.setChecked(true);
                }
                return true;
            case R.id.sortLastUpdated:
                sortMenuItemSelected = R.id.sortLastUpdated;
                if (!item.isChecked()) {
                    prepareForDataFetch();
                    fetchPumpsInBackground(ParseQuery.getQuery("Pump").orderByDescending("updatedAt"),
                                           false);
                    item.setChecked(true);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
        lvPumps.setAdapter(mPumpArrayAdapter);
        pbLoading = (ProgressBar) v.findViewById(R.id.pbLoading);
    }

    private void setupListeners() {

        lvPumps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Pump pump = (Pump) parent.getItemAtPosition(position);
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

        switch (sortMenuItemSelected) {

            case R.id.sortDistance:
                prepareForDataFetch();
                fetchPumpsInBackground(ParseQuery.getQuery("Pump"), true
                                       /* apply additional sort, outside of DB query */);
                break;
            case R.id.sortPriority:
                prepareForDataFetch();
                fetchPumpsInBackground(ParseQuery.getQuery("Pump").orderByAscending("priority"),
                        false);
                break;
            case R.id.sortLastUpdated:
                prepareForDataFetch();
                fetchPumpsInBackground(ParseQuery.getQuery("Pump").orderByDescending("updatedAt"),
                        false);
                break;
            default:
                // no action.
        }
    }

    // Fetch data remotely based on the selected sort option
    private void fetchAndShowRemoteData() {

        switch (sortMenuItemSelected) {

            case R.id.sortDistance:
                prepareForDataFetch();
                fetchPumpsFromRemote(ParseQuery.getQuery("Pump"), true
                                     /* apply additional sort, outside of DB query */);
                break;
            case R.id.sortPriority:
                prepareForDataFetch();
                fetchPumpsFromRemote(ParseQuery.getQuery("Pump").orderByAscending("priority"),
                                     false);
                break;
            case R.id.sortLastUpdated:
                prepareForDataFetch();
                fetchPumpsFromRemote(ParseQuery.getQuery("Pump").orderByDescending("updatedAt"),
                                     false);
                break;
            default:
                // no action.
        }
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

        List<Pump> sortedPumps;

        // return the results as they are
        if (!additionalSort) {
            sortedPumps = new ArrayList<Pump>();
            for (int i = 0; i < pumpList.size(); i++) {
                Pump pump = (Pump)pumpList.get(i);
                if (pump.getObjectId().equals(mCurrentPumpID)) {
                    mCurrentPumpIndex = i;
                }
                sortedPumps.add(pump);
            }
        }
        else {
            // Apply custom sorting (outside of DB query) to sort by distance from current user.
            final List<Pump> pumps = new ArrayList<Pump>(pumpList.size());
            for (ParseObject object : pumpList) {
                final Pump pump = (Pump) object;
                pumps.add(pump);
            }
            sortedPumps = PumpUtil.sortPumps(pumps, ((ParseGeoPoint) currentUser.get("location")));
            for (int i = 0; i < sortedPumps.size(); i++) {
                Pump pump = sortedPumps.get(i);
                if (pump.getObjectId().equals(mCurrentPumpID)) {
                    mCurrentPumpIndex = i;
                }
            }
        }
        mPumpArrayAdapter.addAll(sortedPumps);
        lvPumps.setSelection(mCurrentPumpIndex);

        int firstPosition = lvPumps.getFirstVisiblePosition();
        int wantedChild = mCurrentPumpIndex - firstPosition;
        PumpRowView thePumpView = (PumpRowView)lvPumps.getChildAt(wantedChild);
        if (thePumpView != null) {
            thePumpView.toggleExpandedState();
        }
    }
}
