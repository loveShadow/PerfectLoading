package com.zhaoli.loadings.loadingViews;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by zhaoli on 2016/6/22.
 * 线条旋转2
 */
public class LineWhirlLoading2 extends BaseLoading{

    private final static int BACK_GROUND_COLOR = Color.parseColor("#2C3E50");
    private final static int LINE_COLOR = Color.parseColor("#FAFBFC");

    private final static int CIRCULAR_RADIUS = 100;
    private final static int LINE_WIDTH = 10;

    private int currentAngle1 = 0;
    private int currentAngle2 = 0;

    private RectF rectF = new RectF();

    public LineWhirlLoading2(Context context) {
        super(context);
        initAnim();
    }

    public LineWhirlLoading2(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAnim();
    }

    public LineWhirlLoading2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAnim();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(getWidth() / 2, getHeight() / 2);
        canvas.rotate(-90);

        rectF.set(-CIRCULAR_RADIUS, -CIRCULAR_RADIUS, CIRCULAR_RADIUS, CIRCULAR_RADIUS);
        loadingPaint.setColor(LINE_COLOR);
        canvas.drawArc(rectF, currentAngle2, currentAngle1 - currentAngle2, true, loadingPaint);

        loadingPaint.setColor(BACK_GROUND_COLOR);
        canvas.drawCircle(0, 0, CIRCULAR_RADIUS - LINE_WIDTH, loadingPaint);
    }

    @Override
    protected void initLoading() {
        setBackgroundColor(BACK_GROUND_COLOR);

        AnimatorSet animatorSet = new AnimatorSet();

        ValueAnimator whirlAnim1 = ValueAnimator.ofInt(0, 405);
        whirlAnim1.setDuration(1000);
        whirlAnim1.setInterpolator(new AccelerateInterpolator());
        whirlAnim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentAngle1 = (int) animation.getAnimatedValue();
                if (currentAngle1 >= 360) {
                    currentAngle1 = 360;
                }
                invalidate();
            }
        });

        ValueAnimator whirlAnim2 = ValueAnimator.ofInt(0, 360);
        whirlAnim2.setDuration(1200);
        whirlAnim2.setStartDelay(600);
        whirlAnim2.setInterpolator(new DecelerateInterpolator());
        whirlAnim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentAngle2 = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        whirlAnim2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                currentAngle2 = 0;
            }
        });

        animatorSet.playTogether(whirlAnim1, whirlAnim2);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animation.start();
            }
        });
        animatorSet.start();
    }

    @Override
    protected void initLoadingPaint() {
        loadingPaint.setAntiAlias(true);
    }
}
