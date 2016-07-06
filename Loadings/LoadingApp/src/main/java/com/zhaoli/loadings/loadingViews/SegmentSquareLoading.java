package com.zhaoli.loadings.loadingViews;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

/**
 * Created by zhaoli on 2016/6/19.
 * 分割方体加载
 */
public class SegmentSquareLoading extends BaseLoading {

    private final static int SQUARE_UP_COLOR = Color.parseColor("#1E909A");
    private final static int SQUARE_LEFT_COLOR = Color.parseColor("#D53B33");
    private final static int SQUARE_RIGHT_COLOR = Color.parseColor("#E79C0F");
    private final static int BACKGROUND_COLOR = Color.parseColor("#262626");

    private final static int SQUARE_LENGTH = 40;    //px

    private final static int angle = 60;        //顶部正方形夹角
    private final static float sin_angle = (float) Math.sin(Math.toRadians(angle / 2));
    private final static float cos_angle = (float) Math.cos(Math.toRadians(angle / 2));

    private Path path = null;

    private int orientation = 0;    //方向 0 左右 1 前后 2 上下
    private int offset = 0;         //距离 [0, SQUARE_LENGTH / 3]

    //中心方体的坐标（是不变的）
    private int[] centerXList;
    private int[] centerYList;

    private int[] xList = new int[7];
    private int[] yList = new int[7];

    private ValueAnimator valueAnimator;

    public SegmentSquareLoading(Context context) {
        super(context);
        initAnim();
    }

    public SegmentSquareLoading(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAnim();
    }

    public SegmentSquareLoading(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAnim();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (path == null) {
            path = new Path();
        }

        canvas.translate(getWidth() / 2, getHeight() / 2);

        for (int i = 0; i < 9; i ++) {
            for (int j = 2; j >= 0; j --) {
                drawSquare(i, j, canvas);
            }
        }
    }

    /**
     * 获取偏移量
     * @param index [0,9]
     * @return 偏移量
     */
    private int getOffsetX(int index) {
        if (orientation == 0) {
            if (index % 3 == 1) {
                return 0;
            } else if (index % 3 == 0) {
                return (int) (-offset * cos_angle);
            } else {
                return (int) (offset * cos_angle);
            }
        } else if (orientation == 1) {
            if (index / 3 == 1) {
                return 0;
            } else if (index / 3 == 0){
                return (int) (offset * cos_angle);
            } else {
                return (int) (-offset * cos_angle);
            }
        } else {
            return 0;
        }
    }

    private int getOffsetY(int index, int layout) {
        if (orientation == 0) {
            if (index % 3 == 1) {
                return 0;
            } else if (index % 3 == 0) {
                return (int) (-offset * sin_angle);
            } else {
                return (int) (offset * sin_angle);
            }
        } else if (orientation == 1) {
            if (index / 3 == 1) {
                return 0;
            } else if (index / 3 == 0){
                return (int) (-offset * sin_angle);
            } else {
                return (int) (offset * sin_angle);
            }
        } else {
            if (layout % 3 == 1) {
                return 0;
            } else if (layout % 3 == 0) {
                return -offset;
            } else {
                return offset;
            }
        }
    }

    /**
     * 获取起始点X坐标
     * @param index 顺序
     * @return X坐标
     */
    private int getStartX(int index) {
        int index_3 = index % 3;
        int index3_ = index / 3;
        if (orientation == 0) {
            if (offset >= 0) {
                //正向
                return (int) ((index_3 - (index3_ + 1)) * SQUARE_LENGTH * cos_angle);
            } else {
                //反向
                return (int) ((index_3 - (index3_ + 1) +
                        (index_3 == 0 ? -1 : (index_3 == 1 ? 0 : 1)) +
                        (index3_ == 0 ? 1 : (index3_ == 1) ? 0 : -1)) * SQUARE_LENGTH * cos_angle);
            }
        } else if (orientation == 1) {
            if (offset >= 0) {
                return (int) ((index_3 - (index3_ + 1) +
                        (index_3 == 0 ? -1 : (index_3 == 1 ? 0 : 1))) * SQUARE_LENGTH * cos_angle);
            } else {
                return (int) ((index_3 - (index3_ + 1) +
                        (index3_ == 0 ? 1 : (index3_ == 1) ? 0 : -1)) * SQUARE_LENGTH * cos_angle);
            }
        } else {
            if (offset >= 0) {
                return (int) ((index_3 - (index3_ + 1) +
                        (index_3 == 0 ? -1 : (index_3 == 1 ? 0 : 1)) +
                        (index3_ == 0 ? 1 : (index3_ == 1) ? 0 : -1)) * SQUARE_LENGTH * cos_angle);
            } else {
                return (int) ((index_3 - (index3_ + 1)) * SQUARE_LENGTH * cos_angle);
            }
        }
    }

    /**
     * 获取起始点Y坐标
     * @param index 顺序
     * @param layout 层
     * @return Y坐标
     */
    private int getStartY(int index, int layout) {
        int index_3 = index % 3;
        int index3_ = index / 3;
        int y;
        if (orientation == 0) {
            if (offset >= 0) {
                y = (int) ((index3_ + index_3 - 2) * SQUARE_LENGTH * sin_angle);
            } else {
                y = (int) ((index3_ + index_3 - 2 +
                        (index_3 == 0 ? -1 : (index_3 == 1 ? 0 : 1)) +
                        (index3_ == 0 ? -1 : (index3_ == 1) ? 0 : 1)) * SQUARE_LENGTH * sin_angle) +
                        (layout == 0 ? -1 : (layout == 1) ? 0 : 1) * SQUARE_LENGTH;
            }
        } else if (orientation == 1) {
            if (offset >= 0) {
                y = (int) ((index3_ + index_3 - 2 +
                        (index_3 == 0 ? -1 : (index_3 == 1 ? 0 : 1))) * SQUARE_LENGTH * sin_angle);
            } else {
                y = (int) ((index3_ + index_3 - 2 +
                        (index3_ == 0 ? -1 : (index3_ == 1) ? 0 : 1)) * SQUARE_LENGTH * sin_angle) +
                        (layout == 0 ? -1 : (layout == 1) ? 0 : 1) * SQUARE_LENGTH;
            }
        } else {
            if (offset >= 0) {
                y = (int) ((index3_ + index_3 - 2 +
                        (index_3 == 0 ? -1 : (index_3 == 1 ? 0 : 1)) +
                        (index3_ == 0 ? -1 : (index3_ == 1) ? 0 : 1)) * SQUARE_LENGTH * sin_angle);
            } else {
                y = (int) ((index3_ + index_3 - 2) * SQUARE_LENGTH * sin_angle) +
                        (layout == 0 ? -1 : (layout == 1) ? 0 : 1) * SQUARE_LENGTH;
            }
        }
        y += (layout == 0 ? -1 : (layout == 1) ? 0 : 1) * SQUARE_LENGTH;
        return y;
    }

    private void drawSquare(int index, int layer, Canvas canvas) {
        //先计算最中心的方体坐标，是一直不变的
        if (index == 4 && layer == 1) {
            drawSquare(centerXList, centerYList, canvas);
            return;
        }

        xList[0] = getStartX(index) + getOffsetX(index);
        yList[0] = getStartY(index, layer) + getOffsetY(index, layer);

        xList[6] = xList[0];
        xList[5] = xList[4] = xList[1] = xList[0] + (int) (SQUARE_LENGTH * cos_angle);
        xList[3] = xList[2] = xList[0] + (int) (SQUARE_LENGTH * 2 * cos_angle);

        yList[2] = yList[0];
        yList[1] = yList[0] - SQUARE_LENGTH / 2;
        yList[5] = yList[0] + SQUARE_LENGTH / 2;
        yList[4] = yList[5] + SQUARE_LENGTH;
        yList[6] = yList[3] = yList[0] + SQUARE_LENGTH;

        drawSquare(xList, yList, canvas);
    }

    private void drawSquare(int x[], int y[], Canvas canvas) {
        //绘制顶部
        drawPath(x, y, 0, 1, 2, 5, SQUARE_UP_COLOR, canvas);
        //绘制左边
        drawPath(x, y, 0, 5, 4, 6, SQUARE_LEFT_COLOR, canvas);
        //绘制右边
        drawPath(x, y, 2, 3, 4, 5, SQUARE_RIGHT_COLOR, canvas);
    }

    private void drawPath(int x[], int y[], int point1, int point2, int point3, int point4, int color, Canvas canvas) {
        path.reset();
        path.moveTo(x[point1], y[point1]);
        path.lineTo(x[point2], y[point2]);
        path.lineTo(x[point3], y[point3]);
        path.lineTo(x[point4], y[point4]);
        path.close();
        loadingPaint.setColor(color);
        canvas.drawPath(path, loadingPaint);
    }

    @Override
    protected void initLoading() {
        setBackgroundColor(BACKGROUND_COLOR);

        centerXList = new int[7];
        centerYList = new int[7];

        //计算中心方体的坐标
        centerXList[6] = centerXList[0] = (int) (-SQUARE_LENGTH * cos_angle);
        centerXList[5] = centerXList[4] = centerXList[1] = 0;
        centerXList[3] = centerXList[2] = -centerXList[0];

        centerYList[2] = centerYList[0] = 0;
        centerYList[1] = (int) (centerYList[0] - SQUARE_LENGTH * sin_angle);
        centerYList[5] = SQUARE_LENGTH / 2;
        centerYList[4] = SQUARE_LENGTH;
        centerYList[6] = centerYList[3] = SQUARE_LENGTH / 2;


        valueAnimator = ValueAnimator.ofInt(3 * SQUARE_LENGTH, 2 * SQUARE_LENGTH, SQUARE_LENGTH, 0,
                - SQUARE_LENGTH, -2 * SQUARE_LENGTH, -3 * SQUARE_LENGTH);
        valueAnimator.setDuration(3000);
        valueAnimator.setRepeatCount(-1);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) valueAnimator.getAnimatedValue();
                if (value >= 2 * SQUARE_LENGTH || (value < 0 && value >= -SQUARE_LENGTH)) {
                    orientation = 0;
                    offset = (value > 0) ? (3 * SQUARE_LENGTH - value) : value;
                } else if ((value >= SQUARE_LENGTH && value < 2 * SQUARE_LENGTH) ||
                        (value < -SQUARE_LENGTH & value >= -2 * SQUARE_LENGTH)) {
                    orientation = 1;
                    offset = (value > 0) ? (2 * SQUARE_LENGTH - value) : (value + SQUARE_LENGTH);
                } else {
                    orientation = 2;
                    offset = (value >= 0 ) ? (SQUARE_LENGTH - value) : (value + 2 * SQUARE_LENGTH);
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
}
