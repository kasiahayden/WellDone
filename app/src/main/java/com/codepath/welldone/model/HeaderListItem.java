package com.codepath.welldone.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.codepath.welldone.PumpListAdapter;
import com.codepath.welldone.R;
import com.parse.ParseGeoPoint;

public class HeaderListItem implements AbstractListItem {

    private String mTitle;

    public HeaderListItem(String theTitle) {
        mTitle = theTitle;
    }

    @Override
    public int getViewType() {
        return RowType.HEADER_ITEM.ordinal();

    }

    @Override
    public View getView(LayoutInflater inflater, View convertView, ParseGeoPoint location, PumpListAdapter.PumpListListener listener, Context context) {
        View v = inflater.inflate(R.layout.pump_list_row_header, null);
        TextView tv = (TextView)v.findViewById(R.id.tvListRowHeader);
        tv.setText(mTitle);

        return v;
    }
}
