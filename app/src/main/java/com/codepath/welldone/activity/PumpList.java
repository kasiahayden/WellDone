package com.codepath.welldone.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.codepath.welldone.PumpListAdapter;
import com.codepath.welldone.R;
import com.codepath.welldone.model.Pump;
import com.parse.FindCallback;
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
        ParseAnalytics.trackAppOpened(getIntent());

        mPumpList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Pump pump = (Pump)parent.getItemAtPosition(position);
                Intent intent = new Intent(PumpList.this, PumpDetails.class);
                intent.putExtra("pump", pump);
                startActivity(intent);
            }
        });

        triggerFetchAndRedraw();

    }

    void triggerFetchAndRedraw() {


        ParseQuery<ParseObject> query = ParseQuery.getQuery("Pump");
        query.include("currentStatus");
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> parseObjects, ParseException e) {
                ParseObject.pinAllInBackground(parseObjects);
                for (ParseObject object : parseObjects) {
                    Pump pump = (Pump)object;
                    mPumpArrayAdapter.notifyDataSetChanged();
                    mPumpArrayAdapter.add(pump);
                }
            }
        });
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
