package com.zhaoli.loadings.loadingViews;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.zhaoli.loadings.interpolator.DecelerateAccelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaoli on 2016/6/20.
 * 跳跃旋转图形加载
 */
public class JumpWhirlGraphLoading extends BaseLoading {

    private final static int BACK_GROUND_COLOR = Color.parseColor("#2B2C30");
    private final static int SQUARE_COLOR = Color.parseColor("#E69D31");
    private final static int ROUND_COLOR = Color.parseColor("#E83131");
    private final static int TRIANGLE_COLOR = Color.parseColor("#318CEA");

    private final static int LINE_COLOR = Color.parseColor("#999DAC");

    private final static int ROUND_RADIUS = 90;    //圆的半径
    private final static int SQUARE_LENGTH = 2 * ROUND_RADIUS;  //正方形边长
    private final static int SQUARE_RADIUS = 30;    //正方形圆角
    private final static int TRIANGLE_LENGTH = (int) (SQUARE_LENGTH / Math.cos(Math.toRadians(45)));   //三角形边长
    private final static int TRIANGLE_IN_LENGTH = (int) (TRIANGLE_LENGTH * Math.cos(Math.toRadians(30)) -
            (TRIANGLE_LENGTH / 2 * Math.sin(Math.toRadians(30))));   //三角形内边长

    private final static int LINE_LENGTH = TRIANGLE_LENGTH;
    private final static int LINE_LENGTH_MIN = TRIANGLE_LENGTH / 4;
    private final static int LINE_HEIGHT = 6;   //px
    private final static float LINE_M_T_SCALE = ((float) 170) / 225; //margin_top/height

    private final static int JUMP_HEIGHT = 2 * SQUARE_LENGTH;   //2倍的正方形边长

    private final static float sin_30 = (float) Math.sin(Math.toRadians(30));
    private final static float cos_30 = (float) Math.cos(Math.toRadians(30));

    private final static int DURATION = 1000;

    private int currentWhirlAngle = 0;  //当前旋转角度
    private int currentJumpHeight = 0;  //当前跳转高度
    private int currentLineLength = LINE_LENGTH;  //当前线长度

    private int graphIndex = 0; //图形index = 0 圆 1 正方形 2 三角形

    private Path path = new Path();
    private List<Animator> animatorList;

    private RectF rectF = new RectF();

    public JumpWhirlGraphLoading(Context context) {
        super(context);
        initAnim();
    }

    public JumpWhirlGraphLoading(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAnim();
    }

    public JumpWhirlGraphLoading(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAnim();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float startY = getHeight() * LINE_M_T_SCALE;
        canvas.translate(getWidth() / 2, startY);
        //画水平线
        loadingPaint.setStrokeWidth(LINE_HEIGHT);
        loadingPaint.setColor(LINE_COLOR);
        canvas.drawLine(-currentLineLength / 2, 0, currentLineLength / 2, 0, loadingPaint);

        //画跳转图形（圆/正方形/三角形）
        if (graphIndex == 0) {
            canvas.translate(0, -currentJumpHeight - ROUND_RADIUS);
        } else if (graphIndex == 1) {
            canvas.translate(0, -currentJumpHeight - SQUARE_LENGTH / 2);
            canvas.rotate(currentWhirlAngle);   //旋转画布
        } else {
            canvas.translate(0, -currentJumpHeight - (TRIANGLE_LENGTH * cos_30 - TRIANGLE_IN_LENGTH));
            canvas.rotate(-currentWhirlAngle);   //旋转画布
        }
        switch (graphIndex) {
            case 0:
                loadingPaint.setColor(ROUND_COLOR);
                canvas.drawCircle(0, 0, ROUND_RADIUS, loadingPaint);
                return;
            case 1:
                loadingPaint.setColor(SQUARE_COLOR);
                rectF.left = -SQUARE_LENGTH / 2;
                rectF.top = -SQUARE_LENGTH / 2;
                rectF.right = SQUARE_LENGTH / 2;
                rectF.bottom = SQUARE_LENGTH / 2;
                canvas.drawRoundRect(rectF, SQUARE_RADIUS, SQUARE_RADIUS, loadingPaint);
                break;
            case 2:
                loadingPaint.setColor(TRIANGLE_COLOR);
                path.reset();
                path.moveTo((int) (-TRIANGLE_LENGTH * sin_30),
                        (int) (TRIANGLE_LENGTH * cos_30 - TRIANGLE_IN_LENGTH));
                path.lineTo(0, -TRIANGLE_IN_LENGTH);
                path.lineTo((int) (TRIANGLE_LENGTH * sin_30),
                        (int) (TRIANGLE_LENGTH * cos_30 - TRIANGLE_IN_LENGTH));
                path.close();
                canvas.drawPath(path, loadingPaint);
                break;
        }
    }

    @Override
    protected void initLoading() {
        setBackgroundColor(BACK_GROUND_COLOR);

        animatorList = new ArrayList<>();

        AnimatorSet animatorSet = new AnimatorSet();

        animatorList.add(getRoundAnimator());
        animatorList.add(getSquareAnimator());
        animatorList.add(getTriangleAnimator());

        for (int i = 0; i < animatorList.size(); i ++) {
            animatorList.get(i).addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    graphIndex ++;
                    if (graphIndex == 3) {
                        graphIndex = 0;
                    }
                }
            });
        }

        animatorSet.playSequentially(animatorList);
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

    private AnimatorSet getRoundAnimator() {
        AnimatorSet animatorSet = new AnimatorSet();

        ValueAnimator whirlAnim = getWhirlAnim(90);
        ValueAnimator lineAnim = getLineAnim();
        ValueAnimator jumpAnim = getJumpAnim();

        animatorSet.playTogether(whirlAnim, lineAnim, jumpAnim);
        animatorSet.setDuration(DURATION);
        return animatorSet;
    }

    private AnimatorSet getSquareAnimator() {
        AnimatorSet animatorSet = new AnimatorSet();

        ValueAnimator whirlAnim = getWhirlAnim(90);
        ValueAnimator lineAnim = getLineAnim();
        ValueAnimator jumpAnim = getJumpAnim();

        animatorSet.playTogether(whirlAnim, lineAnim, jumpAnim);
        animatorSet.setDuration(DURATION);
        return animatorSet;
    }

    private AnimatorSet getTriangleAnimator() {
        AnimatorSet animatorSet = new AnimatorSet();

        ValueAnimator whirlAnim = getWhirlAnim(120);
        ValueAnimator lineAnim = getLineAnim();
        ValueAnimator jumpAnim = getJumpAnim();

        animatorSet.playTogether(whirlAnim, lineAnim, jumpAnim);
        animatorSet.setDuration(DURATION);
        return animatorSet;
    }

    private ValueAnimator getLineAnim() {
        ValueAnimator lineAnim = ValueAnimator.ofInt(LINE_LENGTH
                , LINE_LENGTH_MIN, LINE_LENGTH);
        lineAnim.setDuration(DURATION);
        lineAnim.setInterpolator(new DecelerateAccelerateInterpolator());
        lineAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentLineLength = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        return lineAnim;
    }

    private ValueAnimator getJumpAnim() {
        ValueAnimator jumpAnim = ValueAnimator.ofInt(0, JUMP_HEIGHT, 0);
        jumpAnim.setDuration(DURATION);
        jumpAnim.setInterpolator(new DecelerateAccelerateInterpolator());
        jumpAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentJumpHeight = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        return jumpAnim;
    }

    private ValueAnimator getWhirlAnim(int angle) {
        ValueAnimator whirlAnim = ValueAnimator.ofInt(0, angle, 2 * angle);
        whirlAnim.setDuration(DURATION);
        whirlAnim.setInterpolator(new DecelerateAccelerateInterpolator());
        whirlAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentWhirlAngle = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        return whirlAnim;
    }
}
