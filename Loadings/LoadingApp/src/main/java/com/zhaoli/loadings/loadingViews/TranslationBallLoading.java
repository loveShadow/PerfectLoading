package com.zhaoli.loadings.loadingViews;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaoli on 2016/6/20.
 *
 * 平移球体加载
 */
public class TranslationBallLoading extends BaseLoading {

    private final static int[] BALL_COLOR = new int[] {
            Color.parseColor("#ED7070"),
            Color.parseColor("#E9995A"),
            Color.parseColor("#6DE9A5"),
            Color.parseColor("#4EB3E8"),
            Color.parseColor("#C25CEC")
    };

    private final static int TEXT_COLOR = Color.parseColor("#D5D5D5");

    private final static int SPACE_PROPORTION = 12;    //Width / 13 //起始的间距

    private final static int BALL_MAX = 5;
    private final static int BALL_RADIUS = 10;

    private final static int SPACE_MIDDLE = 2 * BALL_RADIUS + 8;    //相聚时的间距

    private List<Animator> animatorList;

    private int[] xList = new int[BALL_MAX];

    boolean startAnimation = false;

    public TranslationBallLoading(Context context) {
        super(context);
        initAnim();
    }

    public TranslationBallLoading(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAnim();
    }

    public TranslationBallLoading(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAnim();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (! startAnimation) {
            startAnimation();
            startAnimation = true;
        }

        int y = getHeight() / 2;

        for (int i = 0; i < BALL_MAX; i ++) {
            drawBall(i, y, canvas);
        }

        //绘制文字
        loadingPaint.setColor(TEXT_COLOR);
        loadingPaint.setTextSize(48);
        loadingPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Loading...", getWidth() / 2, (float) (y * 1.5), loadingPaint);
    }

    private void drawBall(int index, int y, Canvas canvas) {
        loadingPaint.setColor(BALL_COLOR[index]);
        canvas.drawCircle(xList[index], y, BALL_RADIUS, loadingPaint);
    }

    @Override
    protected void initLoading() {
        if (animatorList == null) {
            animatorList = new ArrayList<>();
        }
    }

    private void startAnimation() {
        int width = getWidth();

        int space = width / SPACE_PROPORTION;

        AnimatorSet animatorSet = new AnimatorSet();
        for (int i = 0; i < BALL_MAX; i ++) {
            AnimatorSet animatorSet1 = new AnimatorSet();

            int middleSpace = width / 2 - i * SPACE_MIDDLE +
                    (BALL_MAX * BALL_RADIUS * 2 + (BALL_MAX - 1) * (SPACE_MIDDLE - 2 * BALL_RADIUS)) / 2;

            ValueAnimator valueAnimator1 = ValueAnimator.ofInt(- i * space, middleSpace);
            valueAnimator1.addUpdateListener(getAnimatorUpdateListener(i));
            valueAnimator1.setInterpolator(getInterpolator(i, false));
            valueAnimator1.setDuration(3000);

            ValueAnimator valueAnimator2 = ValueAnimator.ofInt(middleSpace, width - i * space);
            valueAnimator2.addUpdateListener(getAnimatorUpdateListener(i));
            valueAnimator2.setInterpolator(getInterpolator(i, true));
            valueAnimator2.setDuration(1500);

            animatorSet1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    animation.start();
                }
            });
            animatorSet1.playSequentially(valueAnimator1,  valueAnimator2);
            animatorSet1.setInterpolator(new LinearInterpolator());
            animatorSet1.start();
            animatorList.add(animatorSet1);
        }
        animatorSet.playTogether(animatorList);
        animatorSet.start();
    }

    private ValueAnimator.AnimatorUpdateListener getAnimatorUpdateListener(final int index) {
        return new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                xList[index] = (int) animation.getAnimatedValue();
                invalidate();
            }
        };
    }

    private Interpolator getInterpolator(int index, boolean isAcc) {
        if (isAcc) {
            return new AccelerateInterpolator(index + 1);
        }
        if (index < (BALL_MAX / 2 + 1)) {
            return new DecelerateInterpolator((BALL_MAX / 2 + 1) - index);
        } else if (index > (BALL_MAX / 2 + 1)) {
            return new AccelerateInterpolator(index - (BALL_MAX / 2 + 1));
        } else {
            return new LinearInterpolator();
        }
    }

    @Override
    protected void initLoadingPaint() {
        loadingPaint.setAntiAlias(true);
    }
}
