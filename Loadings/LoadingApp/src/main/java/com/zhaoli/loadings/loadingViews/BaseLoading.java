package com.zhaoli.loadings.loadingViews;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
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
    }

    public BaseLoading(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public BaseLoading(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    /**
     * 重载必须调用 (构造结束后调用)
     */
    protected void initAnim() {
        initLoadingPaint();
        initLoading();
    }

    protected abstract void initLoading();
    protected abstract void initLoadingPaint();
}
