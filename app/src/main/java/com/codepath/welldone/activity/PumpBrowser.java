package com.codepath.welldone.activity;

import android.content.Intent;
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
import com.codepath.welldone.PumpListAdapter;
import com.codepath.welldone.R;
import com.codepath.welldone.fragment.PumpListFragment;
import com.codepath.welldone.fragment.PumpMapFragment;
import com.codepath.welldone.helper.NetworkUtil;
import com.codepath.welldone.helper.StringUtil;
import com.codepath.welldone.model.Pump;
import com.codepath.welldone.model.PumpListItem;
import com.parse.ParseAnalytics;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Fragment container: either displays a list of pumps or the map.
 */
public class PumpBrowser extends FragmentActivity implements PumpListAdapter.PumpListListener {

    public static final String EXTRA_PUSH_NOTIFICATION_PUMP_OBJECT_ID = "pumpObjectId";
    PumpMapFragment mMapFragment;
    PumpListFragment mListFragment;
    private MenuItem mLogMeOut;

    ViewPager mPager;
    PagerSlidingTabStrip mTabs;

    private boolean PRETEND_WE_ARE_COMING_FROM_A_PUSH_NOTIF = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_pump_browser);
        mPager = (ViewPager)findViewById(R.id.pumpsViewPager);
        mPager.setAdapter(new ListMapPagerAdapter(getSupportFragmentManager()));
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == mLogMeOut) {
            ParseUser.logOut();
            Intent loginIntent = new Intent(this, SignInActivity.class);
            startActivity(loginIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    public int getCurrentPumpIndex() {
        return mListFragment.mCurrentPumpIndex;
    }

    public void onNewReportClicked(Pump pump) {
        Intent intent = new Intent(this, CreateReportActivity.class);
        intent.putExtra(EXTRA_PUSH_NOTIFICATION_PUMP_OBJECT_ID, pump.getObjectId());
        int index = mListFragment.mPumpArrayAdapter.indexForPump(pump) + 1;
        PumpListItem pumpListItem = (PumpListItem)mListFragment.mPumpArrayAdapter.getItem(index);
        intent.putExtra("nextPumpObjectId", pumpListItem.pump.getObjectId());
        startActivity(intent);
    }

    public static class ListMapPagerAdapter extends FragmentPagerAdapter {

        PumpListFragment mPumpListFragment;
        PumpMapFragment mMapFragment;

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
