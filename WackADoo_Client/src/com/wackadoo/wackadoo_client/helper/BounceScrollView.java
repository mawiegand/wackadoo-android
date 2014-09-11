package com.wackadoo.wackadoo_client.helper;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ScrollView;

public class BounceScrollView extends ScrollView {
    private static final int MAX_Y_OVERSCROLL_DISTANCE = 100;

    private Context context;
    private int maxYOverscrollDistance;

    public BounceScrollView(Context context) {
        super(context);
        this.context = context;
        initBounceScrollView();
    }

    public BounceScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initBounceScrollView();
    }

    public BounceScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initBounceScrollView();
    }

    //get the density of the screen and do some maths with it on the max overscroll distance
    private void initBounceScrollView() {
        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        final float density = metrics.density;
        maxYOverscrollDistance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);
    }

    // replace incoming maxOverScrollY with own custom mMaxYOverscrollDistance
    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) { 
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxYOverscrollDistance, isTouchEvent);  
    }

}
