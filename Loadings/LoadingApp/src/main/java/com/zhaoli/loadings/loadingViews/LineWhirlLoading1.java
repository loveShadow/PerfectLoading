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
import android.view.animation.LinearInterpolator;


/**
 * Created by zhaoli on 2016/6/22.
 * 线条旋转1
 */
public class LineWhirlLoading1 extends BaseLoading {

    private final static int BACK_GROUND_COLOR = Color.parseColor("#836AFF");
    private final static int LINE_COLOR = Color.parseColor("#FEFEFF");

    private final static int CIRCULAR_RADIUS = 100;
    private final static int LINE_WIDTH = 10;
    private final static float P_CIRCULAR = (float) (2 * Math.PI * CIRCULAR_RADIUS); //周长
    private final static float MIN_OFFSET = LINE_WIDTH;
    private final static float MAX_OFFSET = P_CIRCULAR / 2 - 2 * MIN_OFFSET;

    private int currentAngle = 0;
    private float currentLength = MAX_OFFSET;
    private int currentRadius = 0;

    private ValueAnimator.AnimatorUpdateListener angleUpdateListener;
    private ValueAnimator.AnimatorUpdateListener lengthUpdateListener;
    private ValueAnimator.AnimatorUpdateListener scaleUpdateListener;

    private RectF rectF = new RectF();

    public LineWhirlLoading1(Context context) {
        super(context);
        initAnim();
    }

    public LineWhirlLoading1(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAnim();
    }

    public LineWhirlLoading1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAnim();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(getWidth() / 2, getHeight() / 2);

        canvas.rotate(currentAngle);
        int lengthToAngle = (int) (currentLength / P_CIRCULAR * 360);   //弧长角度

        rectF.set(-CIRCULAR_RADIUS + currentRadius,
                -CIRCULAR_RADIUS + currentRadius,
                CIRCULAR_RADIUS - currentRadius,
                CIRCULAR_RADIUS - currentRadius);
        loadingPaint.setColor(LINE_COLOR);
        canvas.drawArc(rectF, lengthToAngle / 2, lengthToAngle, true, loadingPaint);
        canvas.rotate(180);
        canvas.drawArc(rectF, lengthToAngle / 2, lengthToAngle, true, loadingPaint);

        loadingPaint.setColor(BACK_GROUND_COLOR);
        canvas.drawCircle(0, 0, CIRCULAR_RADIUS - LINE_WIDTH - currentRadius, loadingPaint);
    }

    @Override
    protected void initLoading() {
        setBackgroundColor(BACK_GROUND_COLOR);

        AnimatorSet allAnim = new AnimatorSet();

        AnimatorSet animatorSet1 = getWhirlAnim();
        animatorSet1.setDuration(800);
        AnimatorSet animatorSet2 = getWhirlAnim();
        animatorSet2.setDuration(800);

        AnimatorSet animatorSet3 = new AnimatorSet();

        AnimatorSet animatorSet4 = getWhirlAnim(800);
        //TODO 此处有坑，慎踩
        /**
         * 例如：animatorSet1.playSequentially(A1, A2, A3);
         *      animatorSet2.playSequentially(animatorSet1, A4);
         *      animatorSet2.setDuration(time);
         *      运行起来：A1和A4同时执行，之后执行A2,A3
         *
         *      因此，必须在A1,A2,A3,A4中设置时间
         */
        animatorSet3.playTogether(animatorSet4, getScaleAnim());

        allAnim.playSequentially(animatorSet1, animatorSet2, animatorSet3);
        allAnim.setInterpolator(new LinearInterpolator());
        allAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animation.start();
            }
        });

        allAnim.start();
    }

    @Override
    protected void initLoadingPaint() {
        loadingPaint.setAntiAlias(true);
    }

    private AnimatorSet getWhirlAnim() {
        return getWhirlAnim(0);
    }

    private AnimatorSet getWhirlAnim(int time) {
        AnimatorSet animatorSet = new AnimatorSet();
        ValueAnimator constantSpeedWhirlAnim = getConstantSpeedWhirlAnim();
        AnimatorSet whirlToMinAnim = getWhirlToMinAnim();
        AnimatorSet whirlToMaxAnim = getWhirlToMaxAnim();
        animatorSet.playSequentially(constantSpeedWhirlAnim, whirlToMinAnim, whirlToMaxAnim);

        if (time != 0) {
            constantSpeedWhirlAnim.setDuration(time);
            whirlToMinAnim.setDuration(time);
            whirlToMaxAnim.setDuration(time);
        }
        return animatorSet;
    }

    private ValueAnimator getConstantSpeedWhirlAnim() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(135, 225);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(getAngleUpdateListener());
        return valueAnimator;
    }

    private AnimatorSet getWhirlToMinAnim() {
        AnimatorSet animatorSet = new AnimatorSet();

        ValueAnimator whirlAnim = ValueAnimator.ofInt(225, 540);
        whirlAnim.setInterpolator(new AccelerateInterpolator());
        whirlAnim.addUpdateListener(getAngleUpdateListener());

        ValueAnimator lengthAnim = ValueAnimator.ofFloat(MAX_OFFSET, MIN_OFFSET);
        lengthAnim.setInterpolator(new AccelerateInterpolator());
        lengthAnim.addUpdateListener(getLengthUpdateListener());

        animatorSet.playTogether(whirlAnim, lengthAnim);
        return animatorSet;
    }

    private AnimatorSet getWhirlToMaxAnim() {
        AnimatorSet animatorSet = new AnimatorSet();

        ValueAnimator whirlAnim = ValueAnimator.ofInt(180, 495);
        whirlAnim.setInterpolator(new DecelerateInterpolator());
        whirlAnim.addUpdateListener(getAngleUpdateListener());

        ValueAnimator lengthAnim = ValueAnimator.ofFloat(MIN_OFFSET, MAX_OFFSET);
        lengthAnim.setInterpolator(new DecelerateInterpolator());
        lengthAnim.addUpdateListener(getLengthUpdateListener());

        animatorSet.playTogether(whirlAnim, lengthAnim);
        return animatorSet;
    }

    private ValueAnimator getScaleAnim() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(CIRCULAR_RADIUS, 0, CIRCULAR_RADIUS);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(getScaleUpdateListener());
        valueAnimator.setDuration(2400);
        return valueAnimator;
    }

    private ValueAnimator.AnimatorUpdateListener getAngleUpdateListener() {
        if (angleUpdateListener == null) {
            angleUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    currentAngle = (int) animation.getAnimatedValue();
                    if (currentAngle >= 360) {
                        currentAngle -= 360;
                    }
                    invalidate();
                }
            };
        }
        return angleUpdateListener;
    }

    private ValueAnimator.AnimatorUpdateListener getLengthUpdateListener() {
        if (lengthUpdateListener == null) {
            lengthUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    currentLength = (float) animation.getAnimatedValue();
                    invalidate();
                }
            };
        }
        return lengthUpdateListener;
    }

    private ValueAnimator.AnimatorUpdateListener getScaleUpdateListener() {
        if (scaleUpdateListener == null) {
            scaleUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    currentRadius =CIRCULAR_RADIUS - (int) animation.getAnimatedValue();
                    invalidate();
                }
            };
        }
        return scaleUpdateListener;
    }


}
