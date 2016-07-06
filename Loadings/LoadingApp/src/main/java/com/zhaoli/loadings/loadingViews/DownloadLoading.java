package com.zhaoli.loadings.loadingViews;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.zhaoli.loadings.interpolator.DecelerateAccelerateInterpolator;

/**
 * Created by zhaoli on 2016/6/22.
 * 下载加载动画
 */
public class DownloadLoading extends BaseLoading {
    private final static int BACK_GROUND_COLOR = Color.parseColor("#1C9CF2");
    private final static int UN_DO_LINE_COLOR = Color.parseColor("#2FA4F2");
    private final static int DO_LINE_COLOR = Color.parseColor("#FFFFFF");

    private final static int CIRCULAR_RADIUS = 150;
    private final static int CIRCULAR_WIDTH = 12;

    private final static float P_CIRCULAR = (float) (2 * Math.PI * CIRCULAR_RADIUS);
    private final static RectF RECTF = new RectF(-CIRCULAR_RADIUS, -CIRCULAR_RADIUS, CIRCULAR_RADIUS, CIRCULAR_RADIUS);

    private final static float ARROW_LENGTH = (float) (CIRCULAR_RADIUS / 2 / Math.cos(Math.toRadians(45)));//箭头长度
    private final static float CENTER_TEXT_SIZE = 36;    //中心文字大小


    private final static float WAVE_MAX = 4;    //波浪数量
    private final static float WAVE_RADIUS = ARROW_LENGTH / (2 * WAVE_MAX); //波浪半径

    //-----数据区
    private final static int PROGRESS_MAX = 100;

    private int currentProgress = 0;
    private int maxProgress = PROGRESS_MAX;

    private String centerText = "";

    //-----动画区
    private int currentLineLength = CIRCULAR_RADIUS;    //当前线条长度
    private int currentLineAngle = 90;      //当前箭头夹角
    private int currentLineHeight = 0;      //当前线条高度

    private float currentTextSize = 0;  //当前文字大小
    private float currentWavePosition = ARROW_LENGTH;   //当前波浪位置
    private float currentWaveOffset = 0;    //当前波浪偏移
    private RectF waveBeginRectF = new RectF();

    //对勾两个点的Y变化
    private float currentArrowMidHeight = 0;
    private float currentArrowEndHeight = 0;

    private ValueAnimator waveAnim; //波浪动画

    private int animState = -1;    //0 开始动画 1 中间动画 2 结束动画

    private Path path = new Path();

    public DownloadLoading(Context context) {
        super(context);
        initAnim();
    }

    public DownloadLoading(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAnim();
    }

    public DownloadLoading(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAnim();
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
        centerText = "" + currentProgress;
        if (currentProgress >= maxProgress) {
            onLoadingEnd();
            onStartAnimEnd();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(getWidth() / 2, getHeight() / 2);

        //绘制初始圈
        loadingPaint.setColor(UN_DO_LINE_COLOR);
        canvas.drawCircle(0, 0, CIRCULAR_RADIUS, loadingPaint);

        //绘制进度
        canvas.rotate(-90);
        float angle = ((float) currentProgress / maxProgress) * 360;
        loadingPaint.setColor(DO_LINE_COLOR);
        canvas.drawArc(RECTF, 0, -angle, true, loadingPaint);

        //绘制中心圈
        loadingPaint.setColor(BACK_GROUND_COLOR);
        canvas.drawCircle(0, 0, CIRCULAR_RADIUS - CIRCULAR_WIDTH, loadingPaint);

        if (animState == 0) {
            drawStart(canvas);
        } else if (animState == 1) {
            drawLoading(canvas);
        } else {
            drawEnd(canvas);
        }

        loadingPaint.setStyle(Paint.Style.FILL);
    }

    private void drawStart(Canvas canvas) {
        //绘制开始动画
        //1.绘制竖线
        loadingPaint.setColor(DO_LINE_COLOR);

        //复原画布
        canvas.rotate(90);
        loadingPaint.setStrokeWidth(CIRCULAR_WIDTH);
        canvas.drawLine(0, -currentLineLength / 2 - currentLineHeight, 0, currentLineLength / 2 - currentLineHeight, loadingPaint);
        //2.绘制箭头
        loadingPaint.setStrokeWidth(CIRCULAR_WIDTH / 2);
        float lineX = (float) (-ARROW_LENGTH * Math.sin(Math.toRadians(currentLineAngle / 2)));
        float lineY = (float) (ARROW_LENGTH * Math.cos(Math.toRadians(currentLineAngle / 2))) + CIRCULAR_WIDTH / 2;
        canvas.drawLine(lineX, CIRCULAR_WIDTH / 2, 0, lineY, loadingPaint);
        canvas.drawLine(0, lineY, -lineX, CIRCULAR_WIDTH / 2, loadingPaint);
    }

    private void drawLoading(Canvas canvas) {
        //绘制中间动画
        //1. 绘制文字

        //复原画布
        canvas.rotate(90);
        loadingPaint.setColor(DO_LINE_COLOR);
        if (currentTextSize > 0) {
            loadingPaint.setTextSize(currentTextSize);
            loadingPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(centerText, 0, CIRCULAR_RADIUS / 2, loadingPaint);
        }

        //2. 绘制波浪
        canvas.translate(-ARROW_LENGTH, 0);
        //水平线
        path.reset();
        path.moveTo(0, 0);
        path.lineTo(currentWavePosition, 0);

        if (currentWaveOffset > 0) {
            path.reset();
            float waveBeginAngle;   //波浪开始部分的角度
            float waveBeginRadius = WAVE_RADIUS * 2 / 3;    //波浪开始部分的半径
            if (currentWaveOffset > 0 && currentWaveOffset <= WAVE_RADIUS) {
                waveBeginAngle = (float) Math.toDegrees(Math.acos((WAVE_RADIUS - currentWaveOffset) / WAVE_RADIUS));
                waveBeginRectF.set(-currentWaveOffset, -waveBeginRadius, 2 * WAVE_RADIUS - currentWaveOffset, waveBeginRadius);
                path.addArc(waveBeginRectF, 180 + waveBeginAngle, 180 - waveBeginAngle);
                waveBeginRectF.set(waveBeginRectF.right, waveBeginRectF.top, waveBeginRectF.right + 2 * WAVE_RADIUS, waveBeginRectF.bottom);
                path.addArc(waveBeginRectF, 0, 180);
            } else if (currentWaveOffset > WAVE_RADIUS && currentWaveOffset <= 2 * WAVE_RADIUS) {
                waveBeginAngle = (float) (180 - Math.toDegrees(Math.acos((currentWaveOffset - WAVE_RADIUS) / WAVE_RADIUS)));
                waveBeginRectF.set(-currentWaveOffset, -waveBeginRadius, 2 * WAVE_RADIUS - currentWaveOffset, waveBeginRadius);
                path.addArc(waveBeginRectF, 180 + waveBeginAngle, 180 - waveBeginAngle);
                waveBeginRectF.set(waveBeginRectF.right, waveBeginRectF.top, waveBeginRectF.right + 2 * WAVE_RADIUS, waveBeginRectF.bottom);
                path.addArc(waveBeginRectF, 0, 180);
            } else if (currentWaveOffset > 2 * WAVE_RADIUS && currentWaveOffset <= 3 * WAVE_RADIUS) {
                waveBeginAngle = (float) Math.toDegrees(Math.acos((3 * WAVE_RADIUS - currentWaveOffset) / WAVE_RADIUS));
                waveBeginRectF.set(-currentWaveOffset + 2 * WAVE_RADIUS, -waveBeginRadius, 4 * WAVE_RADIUS - currentWaveOffset, waveBeginRadius);
                path.addArc(waveBeginRectF, 0, 180 - waveBeginAngle);
            } else {
                waveBeginAngle = (float) (180 - Math.toDegrees(Math.acos((currentWaveOffset - 3 * WAVE_RADIUS) / WAVE_RADIUS)));
                waveBeginRectF.set(-currentWaveOffset + 2 * WAVE_RADIUS, -waveBeginRadius, 4 * WAVE_RADIUS - currentWaveOffset, waveBeginRadius);
                path.addArc(waveBeginRectF, 0, 180 - waveBeginAngle);
            }
            path.moveTo(waveBeginRectF.right, 0);
        }

        float laveLength = 2 * ARROW_LENGTH - currentWavePosition - (4 * WAVE_RADIUS - currentWaveOffset);
        while (laveLength >= 4 * WAVE_RADIUS) {
            path.rQuadTo(WAVE_RADIUS, -WAVE_RADIUS, 2 * WAVE_RADIUS, 0);
            path.rQuadTo(WAVE_RADIUS, WAVE_RADIUS, 2 * WAVE_RADIUS, 0);
            laveLength -= 4 * WAVE_RADIUS;
        }

        if (laveLength >= 2 * WAVE_RADIUS) {
            path.rQuadTo(WAVE_RADIUS, -WAVE_RADIUS, 2 * WAVE_RADIUS, 0);
            laveLength -= 2 * WAVE_RADIUS;
            path.rQuadTo(WAVE_RADIUS, WAVE_RADIUS, laveLength, 0);
        }

        loadingPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, loadingPaint);
    }

    private void drawEnd(Canvas canvas) {
        //绘制结束动画

        //复原画布
        canvas.rotate(90);
        loadingPaint.setColor(DO_LINE_COLOR);
        if (currentTextSize > 0) {
            loadingPaint.setTextSize(currentTextSize);
            loadingPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(centerText, 0, CIRCULAR_RADIUS / 2, loadingPaint);
        }

        //2. 绘制直线
        path.reset();
        float startX, startY, midX, midY, endX, endY;

        midX = -(float) (ARROW_LENGTH / 3 - currentArrowMidHeight / Math.tan(Math.toRadians(ARROW_MID_ANGLE)));
        midY = currentArrowMidHeight;

        startX = -(float) (Math.sqrt((ARROW_LENGTH * 2 / 3) * (ARROW_LENGTH * 2 / 3) - currentArrowMidHeight * currentArrowMidHeight)) + midX;
        startY = 0;

        endX = (float) (ARROW_LENGTH - currentArrowEndHeight / Math.tan(Math.toRadians(ARROW_END_ANGLE)));
        endY = -currentArrowEndHeight;

        path.moveTo(startX, startY);
        path.lineTo(midX, midY);
        path.lineTo(endX, endY);
        loadingPaint.setStyle(Paint.Style.STROKE);
        loadingPaint.setStrokeWidth(CIRCULAR_WIDTH / 2);
        canvas.drawPath(path, loadingPaint);
    }

    @Override
    protected void initLoading() {
        setBackgroundColor(BACK_GROUND_COLOR);

        startStartAnim();
    }

    @Override
    protected void initLoadingPaint() {
        loadingPaint.setAntiAlias(true);
    }

    private AnimatorSet getDownloadStartAnim() {
        AnimatorSet animatorSet = new AnimatorSet();
        AnimatorSet animatorSet1 = new AnimatorSet();
        AnimatorSet animatorSet2 = new AnimatorSet();

        ValueAnimator valueAnimator1 = ValueAnimator.ofInt(CIRCULAR_RADIUS, CIRCULAR_WIDTH);
        valueAnimator1.setInterpolator(new AccelerateInterpolator());
        valueAnimator1.setDuration(500);
        valueAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentLineLength = (int) animation.getAnimatedValue();
                animState = 0;
                invalidate();
            }
        });

        ValueAnimator valueAnimator2 = ValueAnimator.ofInt(90, 180);
        valueAnimator2.setInterpolator(new AccelerateInterpolator());
        valueAnimator2.setDuration(500);
        valueAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentLineAngle = (int) animation.getAnimatedValue();
                animState = 0;
                invalidate();
            }
        });

        ValueAnimator valueAnimator3 = ValueAnimator.ofInt(0, CIRCULAR_RADIUS + CIRCULAR_WIDTH, CIRCULAR_WIDTH);
        valueAnimator3.setDuration(500);
        valueAnimator3.setInterpolator(new DecelerateAccelerateInterpolator());
        valueAnimator3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentLineHeight = (int) animation.getAnimatedValue();
                animState = 0;
                invalidate();
            }
        });

        ValueAnimator valueAnimator4 = ValueAnimator.ofInt(180, 190, 170, 180);
        valueAnimator4.setDuration(500);
        valueAnimator4.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentLineAngle = (int) animation.getAnimatedValue();
                animState = 0;
                invalidate();
            }
        });

        animatorSet1.playTogether(valueAnimator1, valueAnimator2);
        animatorSet2.playTogether(valueAnimator3, valueAnimator4);
        animatorSet.playSequentially(animatorSet1, animatorSet2);
        return animatorSet;
    }

    private AnimatorSet getDownloadingAnim() {
        AnimatorSet animatorSet = new AnimatorSet();

        AnimatorSet animatorSet1 = new AnimatorSet();

        ValueAnimator textSizeAnim = ValueAnimator.ofFloat(0, CENTER_TEXT_SIZE);
        textSizeAnim.setDuration(500);
        textSizeAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentTextSize = (float) animation.getAnimatedValue();
                animState = 1;
                invalidate();
            }
        });

        ValueAnimator lineAnim = ValueAnimator.ofFloat(2 * ARROW_LENGTH, 0);
        lineAnim.setDuration(500);
        lineAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentWavePosition = (float) animation.getAnimatedValue();
                animState = 1;
                invalidate();
            }
        });

        animatorSet1.playTogether(textSizeAnim, lineAnim);

        waveAnim = ValueAnimator.ofFloat(0, 4 * WAVE_RADIUS);
        waveAnim.setDuration(300);
        waveAnim.setRepeatCount(-1);
        waveAnim.setInterpolator(new LinearInterpolator());
        waveAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentWaveOffset = (float) animation.getAnimatedValue();
                animState = 1;
                invalidate();
            }
        });

        animatorSet.playSequentially(animatorSet1, waveAnim);

        return animatorSet;
    }

    private final static float ARROW_MID_Y_MAX = (float) (ARROW_LENGTH * 2 / 3 * Math.cos(Math.toRadians(45)));  // 最终30度
    private final static float ARROW_END_Y_MAX = -(float) (ARROW_LENGTH * 4 / 3 * Math.cos(Math.toRadians(30)) - ARROW_MID_Y_MAX);
    private final static float ARROW_END_X_MAX = (float) (ARROW_LENGTH * 4 / 3 * Math.sin(Math.toRadians(30)));
    private final static float ARROW_MID_ANGLE = (float) Math.toDegrees(Math.atan(ARROW_MID_Y_MAX / (ARROW_LENGTH / 3)));
    private final static float ARROW_END_ANGLE = (float) Math.toDegrees(Math.atan(-ARROW_END_Y_MAX / (ARROW_LENGTH - ARROW_END_X_MAX)));
    private AnimatorSet getDownloadEndAnim() {
        AnimatorSet animatorSet = new AnimatorSet();

        ValueAnimator textSizeAnim = ValueAnimator.ofFloat(CENTER_TEXT_SIZE, 0);
        textSizeAnim.setDuration(500);
        textSizeAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentTextSize = (float) animation.getAnimatedValue();
                animState = 2;
                invalidate();
            }
        });

        AnimatorSet animatorSet1 = new AnimatorSet();
        ValueAnimator arrowAnim1 = ValueAnimator.ofFloat(0, ARROW_MID_Y_MAX);
        arrowAnim1.setDuration(1000);
        arrowAnim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentArrowMidHeight = (float) animation.getAnimatedValue();
                animState = 2;
                invalidate();
            }
        });

        ValueAnimator arrowAnim2 = ValueAnimator.ofFloat(0, -ARROW_END_Y_MAX);
        arrowAnim2.setDuration(1000);
        arrowAnim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentArrowEndHeight = (float) animation.getAnimatedValue();
                animState = 2;
                invalidate();
            }
        });

        animatorSet1.playTogether(arrowAnim1, arrowAnim2);

        animatorSet.playSequentially(textSizeAnim, animatorSet1);

        return animatorSet;
    }

    /**
     * 进度测试
     * @return
     */
    public DownloadLoading test() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                currentProgress = 0;
                while (true) {
                    post(new Runnable() {
                             @Override
                             public void run() {
                                 setCurrentProgress(++currentProgress);
                             }
                         });
                    post(new Runnable() {
                        @Override
                        public void run() {
                            invalidate();
                        }
                    });
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        return this;
    }

    /**
     * 启动加载中动画
     */
    private void startLoadingAnim() {
        AnimatorSet animatorSet = getDownloadingAnim();
        animatorSet.start();
    }

    /**
     * 启动开始动画
     */
    private void startStartAnim() {
        AnimatorSet animatorSet = getDownloadStartAnim();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animState = -1;
                onStartAnimEnd();
                startLoadingAnim();
            }
        });
        animatorSet.start();
    }

    private void startEndAnim() {
        waveAnim.cancel();
        AnimatorSet animatorSet = getDownloadEndAnim();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animState = -1;
                onEndAnimEnd();
            }
        });
        animatorSet.start();
    }

    /**
     * 起始动画结束 回调
     */
    protected void onStartAnimEnd() {
        test();
    }

    /**
     * 加载结束 回调
     */
    protected void onLoadingEnd() {

    }

    /**
     * 结束动画结束 回调
     */
    protected void onEndAnimEnd() {
        //TODO
        //2秒后 重新开始
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                }
                post(new Runnable() {
                    @Override
                    public void run() {
                        currentProgress = 0;
                        invalidate();
                        startStartAnim();
                    }
                });
            }
        }).start();
    }
}
