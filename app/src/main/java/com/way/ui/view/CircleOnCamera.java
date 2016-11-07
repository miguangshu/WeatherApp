package com.way.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by PC19 on 2016/4/7.
 */
public class CircleOnCamera extends View {
    private static final String TAG = "CircleOnCamera";
    private int mScreenWidth;
    private int mScreenHeight;
    private Paint mPaint;//画笔
    private Point centerPoint;//聚焦圆圈，中心
    private int radius;//聚焦圆圈，半径

    public CircleOnCamera(Context context) {
        this(context, null);
    }

    public CircleOnCamera(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleOnCamera(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getScreenMetrix(context);
        initView(context);
    }

    private void getScreenMetrix(Context context) {
        WindowManager WM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        WM.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;
    }

    private void initView(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);// 抗锯齿
        mPaint.setDither(true);// 防抖动
        mPaint.setStyle(Paint.Style.STROKE);// 空心
        int height = (int) (mScreenHeight * 0.4);//天空区域高度
        //设置聚焦圆圈中心点
        centerPoint = new Point(mScreenWidth / 2, height / 2);
        radius = (int) (mScreenWidth * 0.08);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i(TAG, "onDraw");
        super.onDraw(canvas);
        mPaint.setStrokeWidth(2);
        mPaint.setColor(Color.rgb(0,255,255));
        canvas.drawCircle(centerPoint.x, centerPoint.y, radius, mPaint);// 外圆
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "1--------onTouchEvent");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                int x = (int) event.getX();
                int y = (int) event.getY();
                centerPoint = new Point(x, y);
                invalidate();
                if (mIAutoFocus != null) {
                    Log.i(TAG, "autoFocus");
                    mIAutoFocus.autoFocus();
                }
                return false;
        }
        return false;
    }

    private IAutoFocus mIAutoFocus;

    /**
     * 聚焦的回调接口
     */
    public interface IAutoFocus {
        void autoFocus();
    }

    public void setIAutoFocus(IAutoFocus mIAutoFocus) {
        this.mIAutoFocus = mIAutoFocus;
    }


}
