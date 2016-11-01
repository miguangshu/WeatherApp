package com.way.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by PC19 on 2016/4/7.
 */
public class RectOnCamera extends View {
    private static final String TAG = "RectOnCamera";
    private int mScreenWidth;
    private int mScreenHeight;
    private Paint mPaint;//画笔
    private Path mPath;//天空区域方框
    private Point centerPoint;//聚焦圆圈，中心
    private int radius;//聚焦圆圈，半径

    public RectOnCamera(Context context) {
        this(context, null);
    }

    public RectOnCamera(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RectOnCamera(Context context, AttributeSet attrs, int defStyleAttr) {
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
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);// 空心
        int height = (int) (mScreenHeight * 0.4);//天空区域高度
        int lineLength = (int) (mScreenWidth * 0.08);//线段长度
        //绘制天空区域方框图形
        mPath = new Path();
        //左上角
        mPath.moveTo(0, lineLength);
        mPath.lineTo(0, 0);
        mPath.lineTo(lineLength, 0);
        //右上角
        mPath.moveTo(mScreenWidth - lineLength, 0);
        mPath.lineTo(mScreenWidth, 0);
        mPath.lineTo(mScreenWidth, lineLength);
        //右下角
        mPath.moveTo(mScreenWidth, height - lineLength);
        mPath.lineTo(mScreenWidth, height);
        mPath.lineTo(mScreenWidth - lineLength, height);
        //左下角
        mPath.moveTo(lineLength, height);
        mPath.lineTo(0, height);
        mPath.lineTo(0, height - lineLength);
        //设置聚焦圆圈中心点
        centerPoint = new Point(mScreenWidth / 2, height / 2);
        radius = (int) (mScreenWidth * 0.08);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i(TAG, "onDraw");
        super.onDraw(canvas);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(20);
        canvas.drawPath(mPath, mPaint);
//        new MyThread(canvas).run();
        mPaint.setStrokeWidth(5);
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(centerPoint.x, centerPoint.y, radius, mPaint);// 外圆
        canvas.drawCircle(centerPoint.x, centerPoint.y, radius - 20, mPaint); // 内圆
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onTouchEvent");
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
                return true;
        }
        return true;
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

    //线程内部类
    class MyThread extends Thread {
        public Canvas canvas;
        public boolean isRun;

        public MyThread(Canvas canvas) {
            this.canvas = canvas;
            isRun = true;
        }

        @Override
        public void run() {
            while (isRun) {
                try {
                    mPaint.setStrokeWidth(3);
                    mPaint.setColor(Color.WHITE);
                    Log.i(TAG, "onDraw");
                    canvas.drawCircle(centerPoint.x, centerPoint.y, radius, mPaint);// 外圆
                    canvas.drawCircle(centerPoint.x, centerPoint.y, radius - 20, mPaint); // 内圆
                    Thread.sleep(1000);//睡眠时间为1秒
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        }
    }
}
