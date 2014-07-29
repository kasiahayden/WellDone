package com.codepath.welldone.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.codepath.welldone.PumpListListener;
import com.codepath.welldone.PumpRowView;
import com.codepath.welldone.R;
import com.parse.ParseGeoPoint;

public class PumpListItem implements AbstractListItem {

    public Pump pump;


    public PumpListItem(Pump pump) {
        this.pump = pump;
    }

    @Override
    public View getView(final LayoutInflater inflater, final View convertView, final ParseGeoPoint location, final PumpListListener listener, final Context context) {

        PumpRowView pumpRowView;

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            pumpRowView = (PumpRowView)inflater.inflate(R.layout.row_pump_list_item, null);
        }
        else {
            pumpRowView = (PumpRowView)convertView;
        }
        pumpRowView.clearTextViews();

        pumpRowView.mPump = pump;
        pumpRowView.updateSubviews(location);

        return pumpRowView;
    }

    @Override
    public int getViewType() {
        return RowType.LIST_ITEM.ordinal();
    }
}
