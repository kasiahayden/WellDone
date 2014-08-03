package com.codepath.welldone;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class DisablingScrollView extends ScrollView {

    public DisablingScrollView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

//
//    @Override
//    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
//        View view = (View) getChildAt(getChildCount()-1);
//        int diff = (view.getBottom()-(getHeight()+getScrollY()+view.getTop()));// Calculate the scrolldiff
//        if( diff == 0 ){  // if diff is zero, then the bottom has been reached
//            this.setOnTouchListener( new OnTouchListener(){
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    return false;
//                }
//            });
//        }
//        super.onScrollChanged(l, t, oldl, oldt);
//    }
}
