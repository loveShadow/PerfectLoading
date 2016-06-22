package com.zhaoli.loadings.interpolator;

import android.view.animation.Interpolator;

/**
 * Created by zhaoli on 2016/6/21.
 *
 * 减速加速
 */
public class DecelerateAccelerateInterpolator implements Interpolator {

    public float getInterpolation(float t) {
        float x = 2.0f * t - 1.0f;
        return 0.5f * ( x * x * x + 1.0f);
    }
}
