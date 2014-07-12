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

        final String pumpObjectId = (String) getIntent().getStringExtra("pumpObjectId");
        Log.d("debug", "Passed pump from list to details: " + pumpObjectId);
        final Intent reportActivityIntent = new Intent(this, CreateReportActivity.class);
        reportActivityIntent.putExtra("pumpObjectId", pumpObjectId);
        startActivity(reportActivityIntent);
    }
}
