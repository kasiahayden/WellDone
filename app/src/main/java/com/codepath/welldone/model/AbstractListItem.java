package com.codepath.welldone.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.codepath.welldone.PumpListAdapter;
import com.parse.ParseGeoPoint;

public interface AbstractListItem {
    public int getViewType();
    public View getView(LayoutInflater inflater, View convertView, ParseGeoPoint location, PumpListAdapter.PumpListListener listener, Context context);

    enum RowType {
        LIST_ITEM, HEADER_ITEM
    }
}
