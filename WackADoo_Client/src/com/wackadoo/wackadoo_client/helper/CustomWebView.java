package com.wackadoo.wackadoo_client.helper;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.webkit.WebView;

public class CustomWebView extends WebView {

    private GestureDetector gestureDetector;
    private AtomicBoolean mPreventAction = new AtomicBoolean(false);
    private AtomicLong mPreventActionTime = new AtomicLong(0);

    public CustomWebView(Context context) {
        super(context);
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public CustomWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        int pointId = event.getPointerId(index);

        if (event.getPointerCount() > 1) {
        	return super.onTouchEvent(event);
        } else {
            if (pointId == 0) {
                gestureDetector.onTouchEvent(event);

                if (mPreventAction.get()) {
                    if (System.currentTimeMillis() - mPreventActionTime.get() > ViewConfiguration.getDoubleTapTimeout()){
                        mPreventAction.set(false);
                    } else {
                        return true;
                    }
                }
                return super.onTouchEvent(event);
            } else {
                return true;
            }
        }
    }
    
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            mPreventAction.set(true);
            mPreventActionTime.set(System.currentTimeMillis());
            return true;
        }
        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            mPreventAction.set(true);
            mPreventActionTime.set(System.currentTimeMillis());
            return true;
        }
    }
}