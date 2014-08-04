package com.codepath.welldone;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import com.codepath.welldone.model.AbstractListItem;
import com.codepath.welldone.model.PumpListItem;
import com.codepath.welldone.model.Technician;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Created by khayden on 8/3/14.
 */
public class TechnicianArrayList {
    public ArrayList TechArray = new ArrayList();

    public TechnicianArrayList() {
        TechArray.add(new Technician("Apiyo", -5.019485, 32.826052, "8575401591"));
        TechArray.add(new Technician("Fenny", -4.999167, 32.797964, "8575401591"));
        TechArray.add(new Technician("Riffat", -5.076716, 32.695934, "8575401591"));
    }

    public int getTotalTechCount() {
        return TechArray.size();
    }
}
