package com.zhaoli.loadings.loadingViews;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

/**
 * Created by zhaoli on 2016/6/30.
 *
 * 环形旋转动画
 */
public class AnnulusWhirlLoading extends BaseLoading {

    private final static int BACKGROUND_COLOR = Color.parseColor("#FFFFFF");
    private final static int ANNULUS_COLOR_1 = Color.parseColor("#2A343D");
    private final static int ANNULUS_COLOR_2 = Color.parseColor("#3DB0EA");
    private final static int ANNULUS_COLOR_3 = Color.parseColor("#57B779");
    private final static int ANNULUS_COLOR_4 = Color.parseColor("#EC6D63");

    private final static int LINE_WIDTH = 80;
    private final static int ANNULUS_RADIUS = 2 * LINE_WIDTH;

    private final static float ANNULUS_LENGTH = (float) (2 * LINE_WIDTH * Math.PI) / 4 / 7;
    private final static float ANNULUS_ANGLE_1 = -((float)90 / 14 * 11);
    private final static float ANNULUS_ANGLE_2 = -((float)90 / 14 * 7);
    private final static float ANNULUS_ANGLE_3 = -((float)90 / 14 * 3);

    private final RectF rectF = new RectF(-ANNULUS_RADIUS, -ANNULUS_RADIUS, ANNULUS_RADIUS, ANNULUS_RADIUS);
    //计算区
    private float currentAnnulusLeftAngle = 270;
    private float currentAnnulusRightAngle = 0;

    private float currentLineWidth = LINE_WIDTH;

    private float currentAnnulusAngle1 = ANNULUS_ANGLE_1;
    private float currentAnnulusAngle2 = ANNULUS_ANGLE_2;
    private float currentAnnulusAngle3 = ANNULUS_ANGLE_3;

    public AnnulusWhirlLoading(Context context) {
        super(context);
        initAnim();
    }

    public AnnulusWhirlLoading(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAnim();
    }

    public AnnulusWhirlLoading(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAnim();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(getWidth() / 2, getHeight() / 2);

        if (currentAnnulusRightAngle > (currentAnnulusAngle1 - (float)90 / 14)) {
            drawAnnulus(currentAnnulusAngle1, ANNULUS_COLOR_2, ANNULUS_RADIUS + LINE_WIDTH / 2, canvas);
        }
        if (currentAnnulusRightAngle > (currentAnnulusAngle2 - (float)90 / 14)) {
            drawAnnulus(currentAnnulusAngle2, ANNULUS_COLOR_3, ANNULUS_RADIUS + LINE_WIDTH / 2, canvas);
        }
        if (currentAnnulusRightAngle > (currentAnnulusAngle3 - (float)90 / 14)) {
            drawAnnulus(currentAnnulusAngle3, ANNULUS_COLOR_4, ANNULUS_RADIUS + LINE_WIDTH / 2, canvas);
        }

        loadingPaint.setStrokeWidth(LINE_WIDTH);
        loadingPaint.setStyle(Paint.Style.STROKE);
        loadingPaint.setColor(ANNULUS_COLOR_1);
        canvas.drawArc(rectF, currentAnnulusRightAngle, currentAnnulusLeftAngle - currentAnnulusRightAngle, false, loadingPaint);

        //当长度变化的时候 绘制线
        if (currentLineWidth < LINE_WIDTH) {
            drawAnnulus(0, ANNULUS_COLOR_1, ANNULUS_RADIUS - LINE_WIDTH / 2 + currentLineWidth, canvas);
        }
    }

    private void drawAnnulus(float currentAngle, int color, float right, Canvas canvas) {
        canvas.rotate(currentAngle);
        loadingPaint.setStrokeWidth(0);
        loadingPaint.setStyle(Paint.Style.FILL);
        loadingPaint.setColor(color);
        canvas.drawRect(ANNULUS_RADIUS - LINE_WIDTH / 2, -ANNULUS_LENGTH / 2, right, ANNULUS_LENGTH / 2, loadingPaint);
        canvas.rotate(-currentAngle);
    }

    @Override
    protected void initLoading() {
        setBackgroundColor(BACKGROUND_COLOR);

        AnimatorSet animatorSet = new AnimatorSet();
        Animator closeAnim = getCloseAnim();
        Animator openAnim = getOpenAnim();
        animatorSet.playSequentially(closeAnim, openAnim);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animation.start();
            }
        });
        animatorSet.start();
    }

    private Animator getCloseAnim() {
        AnimatorSet animatorSet = new AnimatorSet();

        ValueAnimator leftAnim = ValueAnimator.ofFloat(270, -90);
        leftAnim.setDuration(2000);
        //左边弧度的变化
        leftAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentAnnulusLeftAngle = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        ValueAnimator rightAnim = ValueAnimator.ofFloat(0, -90);
        rightAnim.setDuration(2000);
        //右边弧度的变化
        rightAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentAnnulusRightAngle = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        animatorSet.playTogether(leftAnim, rightAnim);
        animatorSet.setInterpolator(new LinearInterpolator());
        return animatorSet;
    }

    private Animator getOpenAnim() {
        AnimatorSet animatorSet = new AnimatorSet();
        ValueAnimator startAnim = ValueAnimator.ofFloat(0, LINE_WIDTH);
        startAnim.setDuration(500);
        //打开时的起始动画
        startAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentLineWidth = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        ValueAnimator whirlAnim = ValueAnimator.ofFloat(0, 270);
        whirlAnim.setDuration(500);
        whirlAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentAnnulusLeftAngle = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        whirlAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                currentAnnulusRightAngle = 0;
                currentAnnulusAngle1 = Float.MAX_VALUE;
                currentAnnulusAngle2 = Float.MAX_VALUE;
                currentAnnulusAngle3 = Float.MAX_VALUE;
            }
        });

        ValueAnimator annulusAnim1 = getAnnulusAnim(360 + ANNULUS_ANGLE_1, 1);
        ValueAnimator annulusAnim2 = getAnnulusAnim(360 + ANNULUS_ANGLE_2, 2);
        ValueAnimator annulusAnim3 = getAnnulusAnim(360 + ANNULUS_ANGLE_3, 3);

        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.playSequentially(startAnim, whirlAnim, annulusAnim3, annulusAnim2, annulusAnim1);
        return animatorSet;
    }

    private ValueAnimator getAnnulusAnim(float endAngle, final int index) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(270, endAngle);
        valueAnimator.setDuration(200);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (index == 1) {
                    currentAnnulusAngle1 = (float) animation.getAnimatedValue() - 360;
                } else if (index == 2) {
                    currentAnnulusAngle2 = (float) animation.getAnimatedValue() - 360;
                } else if (index == 3) {
                    currentAnnulusAngle3 = (float) animation.getAnimatedValue() - 360;
                }
                invalidate();
            }
        });
        return valueAnimator;
    }

    @Override
    protected void initLoadingPaint() {
        loadingPaint.setAntiAlias(true);
    }
}
