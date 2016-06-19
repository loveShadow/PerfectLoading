package com.zhaoli.loadings.loadingViews;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.util.AttributeSet;

/**
 * Created by zhaoli on 2016/6/18.
 * 旋转方块加载
 */
public class RotateSquareLoading extends BaseLoading {

    private final static int SQUARE_UP_COLOR = Color.parseColor("#FED74C");
    private final static int SQUARE_LEFT_COLOR = Color.parseColor("#DC9633");
    private final static int SQUARE_RIGHT_COLOR = Color.parseColor("#C77532");
    private final static int SQUARE_SHADOW_COLOR = Color.parseColor("#DADADA");

    private final static int SQUARE_SIDE_LEGTH = 40;    //px

    private Path path = null;
    private int offset = 0;         //[0, SQUARE_SIDE_LEGTH]
    private int orientation = 0;    //0 左右 1 上下

    private final static int angle = 60;        //顶部正方形夹角
    private final static float sin_angle = (float) Math.sin(Math.toRadians(angle / 2));
    private final static float cos_angle = (float) Math.cos(Math.toRadians(angle / 2));

    private int[] xList = new int[7];
    private int[] yList = new int[7];

    private ValueAnimator valueAnimator = null;

    public RotateSquareLoading(Context context) {
        super(context);
    }

    public RotateSquareLoading(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RotateSquareLoading(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //移动坐标，旋转
        canvas.translate(getWidth() / 2, getHeight() / 2);

        //绘制正方体(总共有4个)
        if (path == null) {
            path = new Path();
        }
        
        //需要改变绘制顺序
        if (orientation == 0) {
            drawSquare(0, canvas);
            drawSquare(1, canvas);
            drawSquare(2, canvas);
            drawSquare(3, canvas);
        } else {
            drawSquare(2, canvas);
            drawSquare(0, canvas);
            drawSquare(3, canvas);
            drawSquare(1, canvas);
        }
    }

    @Override
    public void startLoading() {

    }

    @Override
    public void stopLoading() {

    }

    @Override
    protected void initLoading() {
        valueAnimator = ValueAnimator.ofInt(SQUARE_SIDE_LEGTH, 0, -SQUARE_SIDE_LEGTH);
        valueAnimator.setRepeatCount(-1);
        valueAnimator.setDuration(1000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                if (value > 0) {
                    offset = SQUARE_SIDE_LEGTH - value;
                    orientation = 0;
                } else {
                    offset = -value;
                    orientation = 1;
                }
                invalidate();
            }
        });
        valueAnimator.start();
    }

    @Override
    protected void initLoadingPaint() {
        loadingPaint.setAntiAlias(true);
    }

    private void drawPath(int point1, int point2, int point3, int point4, int color, Canvas canvas, boolean isShadow) {
        int shadowLength = 0;
        if (isShadow) {
            shadowLength = 7 * SQUARE_SIDE_LEGTH / 2;
        }
        path.reset();
        path.moveTo(xList[point1], yList[point1] + shadowLength);
        path.lineTo(xList[point2], yList[point2] + shadowLength);
        path.lineTo(xList[point3], yList[point3] + shadowLength);
        path.lineTo(xList[point4], yList[point4] + shadowLength);
        path.close();
        loadingPaint.setColor(color);
        canvas.drawPath(path, loadingPaint);
    }

    private void drawSquare(int index, Canvas canvas) {

        //从左角开始，顺时针
        switch (index) {
            case 0:
                xList[0] = (int) (((orientation == 0) ?
                        (-3 * SQUARE_SIDE_LEGTH / 2 + offset) :
                        (-SQUARE_SIDE_LEGTH / 2)) * cos_angle);
                yList[0] = (int) (((orientation == 0) ?
                        (-3 * SQUARE_SIDE_LEGTH / 2 + offset) :
                        (-SQUARE_SIDE_LEGTH / 2)) * sin_angle);
                break;
            case 1:
                xList[0] = (int) (((orientation == 0) ?
                        (-SQUARE_SIDE_LEGTH / 2 + offset) :
                        (SQUARE_SIDE_LEGTH / 2 - offset)) * cos_angle);
                yList[0] = (int) (((orientation == 0) ?
                        (-SQUARE_SIDE_LEGTH / 2 + offset) :
                        (SQUARE_SIDE_LEGTH / 2 + offset)) * sin_angle);
                break;
            case 2:
                xList[0] = (int) (((orientation == 0) ?
                        (-3 * SQUARE_SIDE_LEGTH / 2 - offset) :
                        (-5 * SQUARE_SIDE_LEGTH / 2 + offset)) * cos_angle);
                yList[0] = (int) (((orientation == 0) ?
                        (SQUARE_SIDE_LEGTH / 2 - offset) :
                        (-SQUARE_SIDE_LEGTH / 2 - offset)) * sin_angle);
                break;
            case 3:
                xList[0] = (int) (((orientation == 0) ?
                        (-SQUARE_SIDE_LEGTH / 2 - offset) :
                        (-3 * SQUARE_SIDE_LEGTH / 2)) * cos_angle);
                yList[0] = (int) (((orientation == 0) ?
                        (3 * SQUARE_SIDE_LEGTH / 2 - offset) :
                        (SQUARE_SIDE_LEGTH / 2)) * sin_angle);
                break;
        }

        xList[6] = xList[0];
        xList[3] = xList[2] = xList[0] + (int) (2 * SQUARE_SIDE_LEGTH * cos_angle);
        xList[4] = xList[5] = xList[1] = xList[0] + (int) (SQUARE_SIDE_LEGTH * cos_angle);

        yList[2] = yList[0];
        yList[1] = yList[0] - SQUARE_SIDE_LEGTH / 2;
        yList[5] = yList[1] + SQUARE_SIDE_LEGTH;
        yList[6] = yList[3] = yList[0] + SQUARE_SIDE_LEGTH;
        yList[4] = yList[5] + SQUARE_SIDE_LEGTH;

        //绘制顶部
        drawPath(0, 1, 2, 5, SQUARE_UP_COLOR, canvas, false);
        //绘制左边
        drawPath(0, 5, 4, 6, SQUARE_LEFT_COLOR, canvas, false);
        //绘制右边
        drawPath(2, 3, 4, 5, SQUARE_RIGHT_COLOR, canvas, false);
        //绘制阴影
        drawPath(0, 1, 2, 5, SQUARE_SHADOW_COLOR, canvas, true);
    }
}
