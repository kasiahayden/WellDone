package com.codepath.welldone.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.codepath.welldone.PumpListAdapter;
import com.codepath.welldone.R;
import com.codepath.welldone.model.Pump;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


public class PumpList extends Activity {

    private ArrayAdapter<Pump> mPumpArrayAdapter;
    private ListView mPumpList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pump_list);

        mPumpArrayAdapter = new PumpListAdapter(this);
        mPumpList = (ListView)findViewById(R.id.lvPumps);
        mPumpList.setAdapter(mPumpArrayAdapter);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Pump");
        query.include("currentStatus");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                for (ParseObject object : parseObjects) {
                    Pump pump = (Pump)object;
                    pump.getStatus().fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            mPumpArrayAdapter.notifyDataSetChanged();
                        }
                    });
                    mPumpArrayAdapter.add((Pump)pump);
                }
            }
        });
        ParseAnalytics.trackAppOpened(getIntent());

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
