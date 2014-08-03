package com.codepath.welldone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.codepath.welldone.model.AbstractListItem;
import com.codepath.welldone.model.Pump;
import com.codepath.welldone.model.PumpListItem;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

/**
 * Adapter to hold a list of pumps with their numbers, color-coded state, and
 * (reverse geo-coded in future) location.
 */
public class PumpListAdapter extends ArrayAdapter<AbstractListItem> {

    private LayoutInflater mInflater;

    public PumpListListener rowListener;

    private ParseGeoPoint currentUserLocation;

    public PumpListAdapter(Context context) {

        super(context, R.layout.row_pump_list_item);
        currentUserLocation = (ParseGeoPoint) ParseUser.getCurrentUser().get("location");
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getViewTypeCount() {
        return AbstractListItem.RowType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final AbstractListItem pump = getItem(position);
        return pump.getView(mInflater, convertView, currentUserLocation, rowListener, getContext());
    }

    public int indexForPumpIncludingHeaders(Pump pump) {
        for (int i = 0; i < getCount(); i++) {
            AbstractListItem item = getItem(i);
            if (item instanceof PumpListItem) {
                PumpListItem pumpListItem = (PumpListItem)item;
                if (pumpListItem.pump.getObjectId().equals(pump.getObjectId())) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int getPumpIndexBetweenZeroAndNumberOfPumps(Pump pump) {
        int count = 0;
        for (int i = 0; i < getCount(); i++) {
            AbstractListItem item = getItem(i);
            if (item instanceof PumpListItem) {
                PumpListItem pumpListItem = (PumpListItem)item;
                if (pumpListItem.pump.getObjectId().equals(pump.getObjectId())) {
                    return count;
                }
                count++;
            }
        }
        return -1;
    }

    /**
     * @return the nth Pump object in the list. Skips headers. Check for null
     */
    public Pump getPumpAtIndex(int index) {
        int currentIndex = 0;
        for (int i = 0; i < getCount(); i++) {
            AbstractListItem item = getItem(i);
            if (item instanceof PumpListItem) {
                if (index == currentIndex) {
                    return ((PumpListItem) item).pump;
                }
                currentIndex++;
            }
        }
        return null;
    }

    public int getTotalPumpCount() {
        int totalNumberOfRealPumps = 0;
        for (int i = 0; i < getCount(); i++) {
            AbstractListItem item = getItem(i);
            if (item instanceof PumpListItem) {
                totalNumberOfRealPumps++;
            }
        }
        return totalNumberOfRealPumps;
    }
}
