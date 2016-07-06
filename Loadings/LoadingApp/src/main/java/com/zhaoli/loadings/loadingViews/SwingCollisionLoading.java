package com.zhaoli.loadings.loadingViews;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by zhaoli on 2016/6/17.
 *
 * 摇摆碰撞加载
 */
public class SwingCollisionLoading extends BaseLoading {

    private final static int BACK_GROUND_COLOR = Color.parseColor("#ECE8DE");
    private final static int BALL_START_COLOR = Color.parseColor("#355773");
    private final static int BALL_END_COLOR = Color.parseColor("#D73B27");

    private final static int BALL_START_SHADOW_COLOR = Color.parseColor("#33355773");
    private final static int BALL_END_SHADOW_COLOR = Color.parseColor("#33D73B27");

    private final static int BALL_RADIUS = 16;  //px
    private final static int BALL_MAX = 7;

    private LinearGradient linearGradient = null;
    private LinearGradient shadowLinearGradient = null;
    private float currentAngle = 0;

    private RectF shadowRectF = new RectF();

    public SwingCollisionLoading(Context context) {
        super(context);
        initAnim();
    }

    public SwingCollisionLoading(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAnim();
    }

    public SwingCollisionLoading(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAnim();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //线条长度
        int lineLength = BALL_RADIUS * 10;

        //第一步：绘制球
        int startX = (getWidth() - (BALL_MAX * BALL_RADIUS * 2)) / 2;
        int startY = (getHeight() - BALL_RADIUS * 2) / 2;

        if (linearGradient == null) {
            linearGradient = new LinearGradient(startX, startY,
                    startX + BALL_MAX * BALL_RADIUS * 2, startY,
                    BALL_START_COLOR, BALL_END_COLOR, Shader.TileMode.MIRROR);
        }

        //设置渐变
        loadingPaint.setShader(linearGradient);

        int index = 0;
        if (currentAngle > 0) {
            index = 1;
        }

        //绘制固定的球
        for (; index < ((currentAngle >= 0) ? BALL_MAX : (BALL_MAX - 1)); index ++) {
            canvas.drawCircle(startX + index * BALL_RADIUS * 2 + BALL_RADIUS,
                    startY + BALL_RADIUS,
                    BALL_RADIUS,
                    loadingPaint);
        }

        //绘制动的球
        if (currentAngle > 0) {
            //球在左边翘起
            int leftX = (int) (startX + BALL_RADIUS * 3 - lineLength * Math.sin(Math.toRadians(currentAngle)));
            int leftY = (int) (getHeight() / 2 - lineLength + lineLength * Math.cos(Math.toRadians(currentAngle)));
            System.out.println();
            canvas.drawCircle(leftX,
                    leftY,
                    BALL_RADIUS,
                    loadingPaint);
        } else if (currentAngle < 0) {
            //球在右边翘起
            int rightX = (int) (int) (startX + BALL_RADIUS * (BALL_MAX * 2 - 3) +
                    lineLength * Math.sin(Math.toRadians(-currentAngle)));
            int rightY = (int) (getHeight() / 2 - lineLength +
                    lineLength * Math.cos(Math.toRadians(-currentAngle)));
            canvas.drawCircle(rightX,
                    rightY,
                    BALL_RADIUS,
                    loadingPaint);
        }

        //第二步：绘制阴影
        int shadowStartY = startY + BALL_RADIUS * 4;

        if (shadowLinearGradient == null) {
            shadowLinearGradient = new LinearGradient(startX, shadowStartY,
                    startX + BALL_MAX * BALL_RADIUS * 2, shadowStartY,
                    BALL_START_SHADOW_COLOR, BALL_END_SHADOW_COLOR, Shader.TileMode.MIRROR);
        }

        //设置渐变
        loadingPaint.setShader(shadowLinearGradient);
        for (index = (currentAngle > 0) ? 1 : 0; index < ((currentAngle > 0) ? BALL_MAX : (BALL_MAX - 1)); index ++) {
            shadowRectF.left = startX + index * BALL_RADIUS * 2;
            shadowRectF.top = shadowStartY;
            shadowRectF.right = startX + BALL_RADIUS * 2 + index * BALL_RADIUS * 2;
            shadowRectF.bottom = shadowStartY + BALL_RADIUS / 2;
            canvas.drawOval(shadowRectF, loadingPaint);
        }
    }

    @Override
    protected void initLoading() {
        //设置背景色
        setBackgroundColor(BACK_GROUND_COLOR);

        //初始化动画
        AnimatorSet animatorSet = new AnimatorSet();
        //计算初始角度(0.2为2/10 线摆长度为半径的10倍)
        float startAngle = (float) Math.toDegrees(Math.asin(0.2));

        ValueAnimator animator1 = ValueAnimator.ofFloat(startAngle, 45);
        animator1.setInterpolator(new DecelerateInterpolator());
        animator1.addUpdateListener(getAnimatorUpdateListener());

        ValueAnimator animator2 = ValueAnimator.ofFloat(45, startAngle);
        animator2.setInterpolator(new AccelerateInterpolator());
        animator2.addUpdateListener(getAnimatorUpdateListener());

        ValueAnimator animator3 = ValueAnimator.ofFloat(-startAngle, -45);
        animator3.setInterpolator(new DecelerateInterpolator());
        animator3.addUpdateListener(getAnimatorUpdateListener());

        ValueAnimator animator4 = ValueAnimator.ofFloat(-45, -startAngle);
        animator4.setInterpolator(new AccelerateInterpolator());
        animator4.addUpdateListener(getAnimatorUpdateListener());

        animatorSet.playSequentially(animator1, animator2, animator3, animator4);
        animatorSet.setDuration(300);
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
        //设置抗锯齿
        loadingPaint.setAntiAlias(true);
    }

    private ValueAnimator.AnimatorUpdateListener getAnimatorUpdateListener() {
        return new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentAngle = (float) animation.getAnimatedValue();
                invalidate();
            }
        };
    }
}
