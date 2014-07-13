package com.codepath.welldone.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ListView;

import com.codepath.welldone.PumpListAdapter;
import com.codepath.welldone.R;
import com.codepath.welldone.activity.PumpDetails;
import com.codepath.welldone.model.Pump;
import com.codepath.welldone.persister.PumpPersister;
import com.parse.ParseQuery;

public class PumpListFragment extends Fragment implements PumpListAdapter.PumpListListener {

    private PumpListAdapter mPumpArrayAdapter;
    private ListView mPumpList;
    public OnFragmentInteractionListener mListener;


    public PumpListFragment() {}

    /**
     * @return the currently highlighted (expanded) pump
     */
    public Pump getCurrentPump() {
        return mPumpArrayAdapter.getItem(0);
    }

    public static PumpListFragment newInstance() {
        PumpListFragment fragment = new PumpListFragment();
        return fragment ;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mPumpArrayAdapter = new PumpListAdapter((Activity)mListener);

        mPumpArrayAdapter.clear();
        PumpPersister.fetchPumpsInBackground(mPumpArrayAdapter, ParseQuery.getQuery("Pump"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pump_list, container, false);

        mPumpList = (ListView)v.findViewById(R.id.lvPumps);
        mPumpList.setAdapter(mPumpArrayAdapter);
        mPumpArrayAdapter.rowListener = this;

        mPumpList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Pump pump = (Pump)parent.getItemAtPosition(position);
                Log.d("debug", "Clicked on pump " + pump.getObjectId() + " " + pump.getName());
                ViewGroup.LayoutParams params = view.getLayoutParams();

                View v = view.findViewById(R.id.vgDetailsContainer);
                v.setVisibility(View.VISIBLE);
                DropDownAnim anim = new DropDownAnim(v, 200, true);
                anim.setDuration(500);
                v.startAnimation(anim);

            }
        });

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onNewReportClicked(Pump pump) {
        Intent intent = new Intent((Activity)mListener, PumpDetails.class);
        intent.putExtra("pumpObjectId", pump.getObjectId());
        startActivity(intent);
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    public class DropDownAnim extends Animation {
        private final int targetHeight;
        private final View view;
        private final boolean down;

        public DropDownAnim(View view, int targetHeight, boolean down) {
            this.view = view;
            this.targetHeight = targetHeight;
            this.down = down;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int newHeight;
            if (down) {
                newHeight = (int) (targetHeight * interpolatedTime);
            } else {
                newHeight = (int) (targetHeight * (1 - interpolatedTime));
            }
            view.getLayoutParams().height = newHeight;
            view.requestLayout();
        }

        @Override
        public void initialize(int width, int height, int parentWidth,
                               int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }

}
