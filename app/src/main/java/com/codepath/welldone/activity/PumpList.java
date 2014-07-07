package com.codepath.welldone.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.welldone.R;
import com.codepath.welldone.fragment.PumpListFragment;
import com.codepath.welldone.model.Pump;
import com.parse.ParseAnalytics;


public class PumpList extends Activity implements PumpListFragment.OnFragmentInteractionListener {

    private Pump mPump;

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pump_list);
        ParseAnalytics.trackAppOpened(getIntent());
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        mPump = (Pump)getIntent().getSerializableExtra(PumpListFragment.ARG_PUMP);
        ft.add(R.id.vgFragmentContainer, PumpListFragment.newInstance(mPump));
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pump_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
