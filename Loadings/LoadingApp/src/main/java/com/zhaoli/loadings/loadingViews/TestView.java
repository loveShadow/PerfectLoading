package com.zhaoli.loadings.loadingViews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.zhaoli.loadings.R;

/**
 * Created by zhaoli on 2016/6/17.
 */
public class TestView extends LinearLayout {

    public TestView(Context context) {
        super(context);
        inflate(context, R.layout.test_item, this);
    }

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.test_item, this);
    }

    public TestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.test_item, this);
    }
}
