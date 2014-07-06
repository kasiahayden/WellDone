package com.codepath.welldone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.codepath.welldone.model.Pump;

public class PumpListAdapter extends ArrayAdapter<Pump> {
    public PumpListAdapter(Context context) {
        super(context, R.layout.row_pump);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Pump pump = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_pump, parent, false);
        }
        TextView tvLocation = (TextView) convertView.findViewById(R.id.tvPumpLocation);
        TextView tvStatus = (TextView) convertView.findViewById(R.id.tvPumpStatus);
        tvLocation.setText(String.format("(%f, %f)", pump.getLocation().getLatitude(), pump.getLocation().getLongitude()));
        tvStatus.setText(pump.getStatus());
        return convertView;
    }
}
