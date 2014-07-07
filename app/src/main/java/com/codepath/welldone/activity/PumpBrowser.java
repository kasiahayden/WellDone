package com.codepath.welldone.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.welldone.R;
import com.codepath.welldone.fragment.PumpListFragment;
import com.codepath.welldone.fragment.PumpMapFragment;
import com.parse.ParseAnalytics;


/**
 * Fragment container: either displays a list of pumps or the map.
 */
public class PumpBrowser extends Activity implements PumpListFragment.OnFragmentInteractionListener {

    PumpMapFragment mMapFragment;
    PumpListFragment mListFragment;
    private MenuItem mMapToggleMenuItem;

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pump_list);
        ParseAnalytics.trackAppOpened(getIntent());
        mListFragment = PumpListFragment.newInstance();

        addInitialListFragment();
    }

    void addInitialListFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        mListFragment = PumpListFragment.newInstance();
        ft.add(R.id.vgFragmentContainer, mListFragment);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pump_browser, menu);
        mMapToggleMenuItem = menu.findItem(R.id.action_map_me_bro);
        return true;
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

    private void swapInMapFragment() {
        createMapFragmentIfNecessary();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.vgFragmentContainer, mMapFragment);
        ft.commit();

        swapMenuItemText();
    }


    private void swapInListFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.vgFragmentContainer, mListFragment);
        ft.commit();
        swapMenuItemText();
    }

    private void swapMenuItemText() {
        if (mMapToggleMenuItem.getTitle().equals("Map")) {
            mMapToggleMenuItem.setTitle("List");
        }
        else {
            mMapToggleMenuItem.setTitle("Map");
        }
    }

    private void createMapFragmentIfNecessary() {
        mMapFragment = PumpMapFragment.newInstance(mListFragment.getCurrentPump());
    }
}
