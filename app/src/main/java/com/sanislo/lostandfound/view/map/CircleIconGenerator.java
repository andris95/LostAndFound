package com.sanislo.lostandfound.view.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.maps.android.ui.IconGenerator;
import com.sanislo.lostandfound.R;

import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by root on 29.04.17.
 */

public class CircleIconGenerator extends IconGenerator {
    public static final String TAG = CircleIconGenerator.class.getSimpleName();
    private Context mContext;
    private ViewGroup mContainer;
    private CircleImageView mCircleImageView;

    public CircleIconGenerator(Context context) {
        super(context);
        mContext = context;
        mContainer = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.layout_cluster_item_icon_container, null);
        mCircleImageView = ButterKnife.findById(mContainer, R.id.iv_cluster_icon);
    }

    @Override
    public Bitmap makeIcon() {
        int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mContainer.measure(measureSpec, measureSpec);

        int measuredWidth = mContainer.getMeasuredWidth();
        int measuredHeight = mContainer.getMeasuredHeight();

        mContainer.layout(0, 0, measuredWidth, measuredHeight);

        Bitmap r = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
        r.eraseColor(Color.TRANSPARENT);

        Canvas canvas = new Canvas(r);
        mContainer.draw(canvas);
        return r;
    }

    public void setImage(Bitmap bitmap) {
        mCircleImageView.setImageBitmap(bitmap);
    }

    @Override
    public Bitmap makeIcon(CharSequence text) {
        return super.makeIcon(text);
    }
}
