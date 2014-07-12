package com.codepath.welldone.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.welldone.R;
import com.codepath.welldone.model.Pump;

public class PumpDetails extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pump_details);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pump_details, menu);
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

    public void onNewReportClicked(View view) {

        final Pump pump = (Pump) getIntent().getSerializableExtra("pump");
        if (pump == null) {
            Log.d("debug", "null pump");
        } else {
            Log.d("debug", "good pump");
        }
        Log.d("debug", "Passed pump from list to details: " + pump.getObjectId() + " " + pump.getName());
        Log.d("debug", "Passed " + pump.getCurrentStatus() + " " + pump.getLocation() + " " + pump.toString());
        final Intent reportActivityIntent = new Intent(this, CreateReportActivity.class);
        reportActivityIntent.putExtra("pump", pump);
        startActivity(reportActivityIntent);
    }
}
