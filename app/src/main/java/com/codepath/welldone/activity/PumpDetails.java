package com.codepath.welldone.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.codepath.welldone.R;

public class PumpDetails extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pump_map_view);
    }
    
    public void onNewReportClicked(View view) {

        final String pumpObjectId = (String) getIntent().getStringExtra("pumpObjectId");
        Log.d("debug", "Passed pump from list to details: " + pumpObjectId);
        final Intent reportActivityIntent = new Intent(this, CreateReportActivity.class);
        reportActivityIntent.putExtra("pumpObjectId", pumpObjectId);
        startActivity(reportActivityIntent);
    }
}
