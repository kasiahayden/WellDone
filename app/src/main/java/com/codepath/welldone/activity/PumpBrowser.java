package com.codepath.welldone.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.welldone.PumpListListener;
import com.codepath.welldone.R;
import com.codepath.welldone.fragment.PumpListFragment;
import com.codepath.welldone.fragment.PumpMapFragment;
import com.codepath.welldone.helper.NetworkUtil;
import com.codepath.welldone.helper.StringUtil;
import com.codepath.welldone.model.Pump;
import com.codepath.welldone.persister.PumpPersister;
import com.parse.ParseAnalytics;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Fragment container: either displays a list of pumps or the map.
 */
public class PumpBrowser extends FragmentActivity implements PumpListListener {

    public static final String EXTRA_PUSH_NOTIFICATION_PUMP_OBJECT_ID = "pumpObjectId";
    public static final String RECEIVER_PUMP_UPDATE = "com.welldone.PUMP_UPDATE";
    private MenuItem mLogMeOut;


    private BroadcastReceiver mReceiver;

    ViewPager mPager;
    PagerSlidingTabStrip mTabs;

    private boolean PRETEND_WE_ARE_COMING_FROM_A_PUSH_NOTIF = false;
    private ListMapPagerAdapter mListMapPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupTextMessageReceiver();


        setContentView(R.layout.activity_pump_browser);
        mPager = (ViewPager)findViewById(R.id.pumpsViewPager);
        mListMapPagerAdapter = new ListMapPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mListMapPagerAdapter);
        mTabs = (PagerSlidingTabStrip)findViewById(R.id.slidingTabs);
        mTabs.setViewPager(mPager);

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

    }

    private void setupTextMessageReceiver() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(PumpBrowser.class.toString(), "Received an intent");
                String pumpObjectId = intent.getStringExtra(EXTRA_PUSH_NOTIFICATION_PUMP_OBJECT_ID);
                String newStatus = intent.getStringExtra("status");
                Pump p = PumpPersister.getPumpByObjectIdSyncly(pumpObjectId);
                if (p != null) {
                    p.setCurrentStatus(newStatus);
                    p.saveEventually();
                    Log.d("DBG", String.format("Pump %s is now: %s", p.getObjectId(), p.getCurrentStatus()));
                    switchToMapViewForPump(p);
                }
                else {
                    Log.d("DBG", String.format("Failed to load pump with ID %s", pumpObjectId));
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVER_PUMP_UPDATE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(mReceiver, intentFilter);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pump_browser, menu);
        mLogMeOut = menu.findItem(R.id.action_log_me_the_fuck_out);
        return true;
    }

    public void onListRefreshederested() {
        Log.d("DBG", "onListRefreshederested");
        if (getPumpMapFragment().mMapPagerAdapter != null) {
            getPumpMapFragment().mMapPagerAdapter.notifyDataSetChanged();
        }
        getPumpMapFragment().mPumpListAdapter = getPumpListFragment().mPumpArrayAdapter;
        Pump p = getPumpListFragment().mPumpArrayAdapter.getPumpAtIndex(0);
        getPumpMapFragment().setCurrentlyDisplayedPump(p);
    }

    @Override
    public void onPumpListRowSelected(Pump pump) {
        switchToMapViewForPump(pump);
    }

    private void switchToMapViewForPump(Pump pump) {
        mPager.setCurrentItem(1, true);
        getPumpMapFragment().setCurrentlyDisplayedPump(pump);
    }

    private PumpMapFragment getPumpMapFragment() {
        return mListMapPagerAdapter.mMapFragment;
    }

    private PumpListFragment getPumpListFragment() {
        return mListMapPagerAdapter.mPumpListFragment;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == mLogMeOut) {
            ParseUser.logOut();
            Intent loginIntent = new Intent(this, SignInActivity.class);
            startActivity(loginIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    public static class ListMapPagerAdapter extends FragmentPagerAdapter {

        protected PumpListFragment mPumpListFragment;
        protected PumpMapFragment mMapFragment;

        public ListMapPagerAdapter(FragmentManager fm) {
            super(fm);
            mMapFragment = PumpMapFragment.newInstance();
            mPumpListFragment = PumpListFragment.newInstance();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mPumpListFragment;
                case 1:
                    return mMapFragment;
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "LIST";
                case 1:
                    return "MAP";
            }
            return super.getPageTitle(position);
        }
    }

}
