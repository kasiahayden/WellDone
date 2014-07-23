package com.codepath.welldone.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.welldone.PumpListAdapter;
import com.codepath.welldone.R;
import com.codepath.welldone.fragment.PumpListFragment;
import com.codepath.welldone.fragment.PumpMapFragment;
import com.codepath.welldone.helper.NetworkUtil;
import com.codepath.welldone.helper.StringUtil;
import com.codepath.welldone.model.Pump;
import com.parse.ParseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Fragment container: either displays a list of pumps or the map.
 */
public class PumpBrowser extends Activity implements PumpListAdapter.PumpListListener {

    public static final String EXTRA_PUSH_NOTIFICATION_PUMP_OBJECT_ID = "pumpObjectId";
    PumpMapFragment mMapFragment;
    PumpListFragment mListFragment;
    private MenuItem mMapToggleMenuItem;

    private boolean isDisplayingMap;

    private boolean PRETEND_WE_ARE_COMING_FROM_A_PUSH_NOTIF = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pump_browser);
        ParseAnalytics.trackAppOpened(getIntent());
        try {
            if (getIntent().hasExtra("com.parse.Data")) {
                JSONObject json = new JSONObject(getIntent().getStringExtra("com.parse.Data"));
                if (json.has("objectId")) {
                    getIntent().putExtra(EXTRA_PUSH_NOTIFICATION_PUMP_OBJECT_ID, json.getString("objectId"));
                }
            }
        }
        catch (JSONException e) {

        }

        if (PRETEND_WE_ARE_COMING_FROM_A_PUSH_NOTIF) {
            getIntent().putExtra(EXTRA_PUSH_NOTIFICATION_PUMP_OBJECT_ID, "mLZB4GWceT");
        }

        if (getIntent().hasExtra(EXTRA_PUSH_NOTIFICATION_PUMP_OBJECT_ID)) {
            addInitialListFragment(getIntent().getStringExtra(EXTRA_PUSH_NOTIFICATION_PUMP_OBJECT_ID));
        }
        else {
            // Standard initial launch: begin displaying list.
            addInitialListFragment(null);
        }

        isDisplayingMap = false;
    }

    private void initializeBothFragmentsAndDisplayMap() {

    }

    @Override
    protected void onResume() {

        super.onResume();

        final Resources appResources = getApplicationContext().getResources();
        final CharSequence appName = appResources.getText(appResources.getIdentifier("app_name",
                "string", getApplicationContext().getPackageName()));
        Log.d("PumpBrowser", "Application name from Context: " + appName);
        if (!NetworkUtil.isNetworkAvailable(this)) {
            getActionBar().setTitle(StringUtil.getConcatenatedString(appName.toString(),
                                    " (Offline)"));
        } else {
            getActionBar().setTitle(appName.toString());
        }
    }

    void addInitialListFragment(String objectIDToDisplayInMap) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        mListFragment = PumpListFragment.newInstance();
        if (objectIDToDisplayInMap == null) {
            mMapFragment = PumpMapFragment.newInstance();
            mListFragment = PumpListFragment.newInstance();
        }
        else {
            mMapFragment = PumpMapFragment.newInstance(objectIDToDisplayInMap);
            mListFragment = PumpListFragment.newInstance(objectIDToDisplayInMap);
        }
        ft.add(R.id.vgFragmentContainer, mListFragment);
        ft.add(R.id.vgFragmentContainer, mMapFragment);
        ft.hide(mMapFragment);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pump_browser, menu);
        mMapToggleMenuItem = menu.findItem(R.id.action_map_me_bro);
        setupMapToggleMenuItem();
        return true;
    }

    private void setupMapToggleMenuItem() {
        if (isDisplayingMap) {
            mMapToggleMenuItem.setTitle("List");
            mMapToggleMenuItem.setIcon(R.drawable.ic_list);
        }
        else {
            mMapToggleMenuItem.setTitle("Map");
            mMapToggleMenuItem.setIcon(R.drawable.ic_map);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_map_me_bro) {
            if (item.getTitle().equals("Map")) {
                swapInMapFragment();
            }
            else {
                swapInListFragment();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void swapInListFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        ft.hide(mMapFragment);
        ft.show(mListFragment);
        ft.commit();
        isDisplayingMap = false;
        setupMapToggleMenuItem();
    }

    public int getCurrentPumpIndex() {
        return mListFragment.mCurrentPumpIndex;
    }

    private void swapInMapFragment() {
        mMapFragment.mPump = mListFragment.getCurrentPump();
        mMapFragment.mPumpListAdapter = mListFragment.mPumpArrayAdapter;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        ft.show(mMapFragment);
        ft.hide(mListFragment);
        ft.commit();
        isDisplayingMap = true;
        setupMapToggleMenuItem();
    }

    public void onNewReportClicked(Pump pump) {
        Intent intent = new Intent(this, CreateReportActivity.class);
        intent.putExtra(EXTRA_PUSH_NOTIFICATION_PUMP_OBJECT_ID, pump.getObjectId());
        int index = mListFragment.mPumpArrayAdapter.getPosition(pump) + 1;
        intent.putExtra("nextPumpObjectId", mListFragment.mPumpArrayAdapter.getItem(index).getObjectId());
        startActivity(intent);
    }

}
