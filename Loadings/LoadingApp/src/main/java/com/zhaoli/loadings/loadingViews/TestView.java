package com.zhaoli.loadings.loadingViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.zhaoli.loadings.R;

/**
 * Created by zhaoli on 2016/6/17.
 */
public class TestView extends View {

    private Path path = new Path();
    private Paint paint = new Paint();

    public TestView(Context context) {
        super(context);
    }

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(0, getHeight() / 2);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6);

        paint.setColor(Color.parseColor("#FF0000"));
        canvas.drawLine(0, 0, getWidth(), 0, paint);

        canvas.drawLine(200, -20, 200, 0, paint);
        canvas.drawLine(400, -20, 400, 0, paint);
        canvas.drawLine(600, -20, 600, 0, paint);

        path.reset();
        path.moveTo(0, 0);
        path.lineTo(200, 200); //直线 下一个点坐标(x, y)

        paint.setColor(Color.parseColor("#000000"));
        canvas.drawPath(path, paint);

        rQuadTo(200, 0, 400, -200, "#0000FF", canvas);
        rQuadTo(0, 200, 400, 0, "#00FF00", canvas);
        rQuadTo(100, -200, 200, 0, "#00FFFF", canvas);
    }

    private void startAnim() {

    }

    /**
     * 贝塞尔曲线
     * @param dx1   贝塞尔曲线控制点坐标(相差)
     * @param dy1   贝塞尔曲线控制点坐标(相差)
     * @param dx2   贝塞尔曲线终点坐标(相差)
     * @param dy2   贝塞尔曲线终点坐标(相差)
     * @param color 颜色
     * @param canvas 画布
     */
    private void rQuadTo(int dx1, int dy1, int dx2, int dy2, String color, Canvas canvas) {
        path.reset();
        path.moveTo(0, 0);  //起点坐标
        path.rQuadTo(dx1, dy1, dx2, dy2);
        paint.setColor(Color.parseColor(color));
        canvas.drawPath(path, paint);
    }
}
