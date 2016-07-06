package com.zhaoli.loadings.loadingViews;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

/**
 * Created by zhaoli on 2016/6/21.
 * 变换球体加载
 */
public class ConvertBallLoading extends BaseLoading {

    private final static int BALL_RADIUS = 20;

    private final static int[] BALL_COLOR = new int[] {
            Color.parseColor("#3E8EFF"),    //蓝
            Color.parseColor("#FF4B4B"),    //红
            Color.parseColor("#FFD91E")     //黄
    };

    private final static int BACK_GROUND = Color.parseColor("#FBF8F0");

    //left
    private int[] offset_radius;
    private int[] offset;
    private int leftStep = 0;//步骤 0, 1, 2, 3

    //right
    private int[] center;   //中心点（1：左 2：右）
    private int[] whirl_radius;
    private final static int BALL_RADIUS_2 = 2 * BALL_RADIUS;
    private final static int BALL_RADIUS_3 = 3 * BALL_RADIUS;


    public ConvertBallLoading(Context context) {
        super(context);
        initAnim();
    }

    public ConvertBallLoading(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAnim();
    }

    public ConvertBallLoading(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAnim();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制第一幅
        if (leftStep == 0) {
            drawLeft(2, 0, 1, canvas);  //0-1交1 0-2重1
        } else if (leftStep == 1) {
            drawLeft(1, 2, 0, canvas);  //1-2交2
        } else if (leftStep == 2) {
            drawLeft(2, 0, 1, canvas);  //0-1交1 0-2重0
        } else {
            drawLeft(1, 2, 0, canvas);  //1-2交2
        }

        //绘制第二幅
        canvas.translate(getWidth() / 2, 0);
        drawRight(0, canvas);
        drawRight(1, canvas);
        drawRight(2, canvas);
    }

    private void drawLeft(int i, int j, int k, Canvas canvas) {
        canvas.translate(getWidth() / 4, getHeight() / 2);
        loadingPaint.setColor(BALL_COLOR[i]);
        canvas.drawCircle(offset[i], 0, BALL_RADIUS + offset_radius[i], loadingPaint);

        loadingPaint.setColor(BALL_COLOR[j]);
        canvas.drawCircle(offset[j], 0, BALL_RADIUS + offset_radius[j], loadingPaint);

        loadingPaint.setColor(BALL_COLOR[k]);
        canvas.drawCircle(offset[k], 0, BALL_RADIUS + offset_radius[k], loadingPaint);
    }

    private void drawRight(int index, Canvas canvas) {

        float x, y;

        if (whirl_radius[index] >= 0 && whirl_radius[index] < 90) {
            y = -BALL_RADIUS_3 * sin(whirl_radius[index]);
        } else if (whirl_radius[index] >= 90 && whirl_radius[index] < 180) {
            y = -BALL_RADIUS_3 * sin(180 - whirl_radius[index]);
        } else if (whirl_radius[index] >= 180 && whirl_radius[index] < 270) {
            y = BALL_RADIUS_3 * cos(270 - whirl_radius[index]);
        } else {
            y = BALL_RADIUS_3 * sin(360 - whirl_radius[index]);
        }

        if (whirl_radius[index] >= 0 && whirl_radius[index] < 90) {
            x = (center[index] == 1) ? (-BALL_RADIUS_2 - BALL_RADIUS_2 * cos(whirl_radius[index])) :
                                       (BALL_RADIUS_2 - BALL_RADIUS_2 * cos(whirl_radius[index]));
        } else if (whirl_radius[index] >= 90 && whirl_radius[index] < 180) {
            x = (center[index] == 1) ? (-BALL_RADIUS_2 + BALL_RADIUS_2 * cos(180 - whirl_radius[index])) :
                                       (BALL_RADIUS_2 + BALL_RADIUS_2 * cos(180 - whirl_radius[index]));
        } else if (whirl_radius[index] >= 180 && whirl_radius[index] < 270) {
            x = (center[index] == 1) ? (-BALL_RADIUS_2 + BALL_RADIUS_2 * sin(270 - whirl_radius[index])) :
                                       (BALL_RADIUS_2 + BALL_RADIUS_2 * sin(270 - whirl_radius[index]));
        } else {
            x = (center[index] == 1) ? (-BALL_RADIUS_2 - BALL_RADIUS_2 * cos(360 - whirl_radius[index])) :
                                       (BALL_RADIUS_2 - BALL_RADIUS_2 * cos(360 - whirl_radius[index]));
        }

        loadingPaint.setColor(BALL_COLOR[index]);
        canvas.drawCircle(x, y, BALL_RADIUS, loadingPaint);
    }

    private float sin(int angle) {
        return (float) Math.sin(Math.toRadians(angle));
    }

    private float cos(int angle) {
        return (float) Math.cos(Math.toRadians(angle));
    }

    @Override
    protected void initLoading() {
        setBackgroundColor(BACK_GROUND);

        AnimatorSet animatorSet = new AnimatorSet();

        offset = new int[3];
        offset_radius = new int[3];
        ValueAnimator leftMoveAnim = getLeftMoveAnim();

        center = new int[3];
        whirl_radius = new int[3];
        ValueAnimator rightAnim = getRightAnim();

        animatorSet.playTogether(leftMoveAnim, rightAnim);
        animatorSet.start();
    }

    private ValueAnimator getLeftMoveAnim() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 6 * BALL_RADIUS, 12 * BALL_RADIUS,
                18 * BALL_RADIUS, 24 * BALL_RADIUS);
        valueAnimator.setDuration(2000);
        valueAnimator.setRepeatCount(-1);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                if (value >= 0 && value < 6 * BALL_RADIUS) {
                    offset[0] = value - 6 * BALL_RADIUS;
                    offset[1] = -value;
                    offset[2] = 6 * BALL_RADIUS - value;

                    offset_radius[0] = offset_radius[2] = value / 6;    //[0, 0.5]
                    offset_radius[1] = BALL_RADIUS - value / 6;   //[0.5, 0]

                    leftStep = 0;
                } else if (value >= 6 * BALL_RADIUS && value < 12 * BALL_RADIUS) {
                    offset[0] = value - 6 * BALL_RADIUS;
                    offset[1] = -(12 * BALL_RADIUS - value);
                    offset[2] = 6 * BALL_RADIUS - value;

                    offset_radius[0] = offset_radius[2] = BALL_RADIUS - (value - 6 * BALL_RADIUS) / 6;    //[0.5, 0]
                    offset_radius[1] = (value - 6 * BALL_RADIUS) / 6;   //[0, 0.5]

                    leftStep = 1;
                } else if (value >= 12 * BALL_RADIUS && value < 18 * BALL_RADIUS) {
                    offset[0] = 18 * BALL_RADIUS - value;
                    offset[1] = value - 12 * BALL_RADIUS;
                    offset[2] = -(18 * BALL_RADIUS - value);

                    offset_radius[0] = offset_radius[2] = (value - 12 * BALL_RADIUS) / 6;    //[0, 0.5]
                    offset_radius[1] = BALL_RADIUS - (value - 12 * BALL_RADIUS) / 6;   //[0.5, 0]

                    leftStep = 2;
                } else if (value >= 18 * BALL_RADIUS && value < 24 * BALL_RADIUS) {
                    offset[0] = -(value - 18 * BALL_RADIUS);
                    offset[1] = 24 * BALL_RADIUS - value;
                    offset[2] = value - 18 * BALL_RADIUS;

                    offset_radius[0] = offset_radius[2] = BALL_RADIUS - (value - 18 * BALL_RADIUS) / 6;    //[0.5, 0]
                    offset_radius[1] = (value - 18 * BALL_RADIUS) / 6;   //[0, 0.5]

                    leftStep = 3;
                }
                invalidate();
            }
        });
        return valueAnimator;
    }

    private ValueAnimator getRightAnim() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 90, 180, 270, 360, 450, 540, 630, 720);
        valueAnimator.setDuration(1500);
        valueAnimator.setRepeatCount(-1);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                //计算每个
                if (value >= 90 && value < 450) {
                    center[0] = 2;
                    whirl_radius[0] = value - 90;
                } else {
                    center[0] = 1;
                    if (value >= 450 && value < 630) {
                        whirl_radius[0] = 180 - (value - 450);
                    } else if (value >= 0 && value < 90) {
                        whirl_radius[0] = 270 - value;
                    } else {
                        whirl_radius[0] = 360 - (value - 630);
                    }
                }

                if (value >= 0 && value < 360) {
                    center[1] = 1;
                    if (value >= 0 && value < 180) {
                        whirl_radius[1] = 180 - value;
                    } else {
                        whirl_radius[1] = 360 - (value - 180);
                    }
                } else {
                    center[1] = 2;
                    whirl_radius[1] = value - 360;
                }

                if (value >= 0 && value < 270) {
                    center[2] = 2;
                    whirl_radius[2] = 90 + value;
                } else if (value >= 270 && value < 630) {
                    center[2] = 1;
                    if (value >= 270 && value < 450) {
                        whirl_radius[2] = 180 - (value - 270);
                    } else {
                        whirl_radius[2] = 360 - (value - 450);
                    }
                } else {
                    center[2] = 2;
                    whirl_radius[2] = value - 630;
                }
            }
        });
        return valueAnimator;
    }

    @Override
    protected void initLoadingPaint() {
        loadingPaint.setAntiAlias(true);
    }
}
