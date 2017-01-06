package com.sanislo.lostandfound.utils;

import android.graphics.drawable.Animatable;
import android.os.Handler;

/**
 * Created by root on 06.01.17.
 */

public class AVDWrapper {

    private Handler mHandler;
    private Animatable mDrawable;
    private Callback mCallback;
    private Runnable mAnimationDoneRunnable = new Runnable() {

        @Override
        public void run() {
            if (mCallback != null)
                mCallback.onAnimationDone();
        }
    };

    public interface Callback {
        public void onAnimationDone();
        public void onAnimationStopped();
    }

    public AVDWrapper(Animatable drawable, Callback callback) {
        mDrawable = drawable;
        mHandler = new Handler();
        mCallback = callback;
    }

    // Duration of the animation
    public void start(long duration) {
        mDrawable.start();
        mHandler.postDelayed(mAnimationDoneRunnable, duration);
    }

    public void stop() {
        mDrawable.stop();
        mHandler.removeCallbacks(mAnimationDoneRunnable);

        if (mCallback != null)
            mCallback.onAnimationStopped();
    }
}