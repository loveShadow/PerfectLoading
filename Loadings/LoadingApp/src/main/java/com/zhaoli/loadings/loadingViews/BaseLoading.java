package com.zhaoli.loadings.loadingViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by zhaoli on 2016/6/17.
 */
public abstract class BaseLoading extends View {

    protected Context context = null;
    protected Paint loadingPaint = new Paint();

    public BaseLoading(Context context) {
        super(context);
        this.context = context;
        initLoading();
        initLoadingPaint();
    }

    public BaseLoading(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initLoading();
        initLoadingPaint();
    }

    public BaseLoading(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initLoading();
        initLoadingPaint();
    }

    protected abstract void initLoading();
    protected abstract void initLoadingPaint();
}
