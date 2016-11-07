package com.way.yahoo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.way.common.util.UploadUtil;
import com.way.ui.view.CameraSurfaceView;
import com.way.ui.view.CircleOnCamera;
import com.way.ui.view.RectOnCamera;


public class CameraActivity extends Activity implements View.OnClickListener, CircleOnCamera.IAutoFocus {

    private CameraSurfaceView mCameraSurfaceView;
    private RectOnCamera mRectOnCamera;
    private CircleOnCamera mCircleOnCamera;
    private ImageView takePicBtn;
    private ImageView uploadBtn;
    private ImageView cancelBtn;
    private boolean mIsTakePic = false;//拍照状态标识
    private static final String TAG = "CameraActivity";
    private AlphaAnimation appearAnimation;
    private AlphaAnimation disappearAnimation;
    private UploadUtil uploadUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"1-------------------onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);
        initAnimations();
        initView();
        initEvents();
    }

    public void initView() {
        mCameraSurfaceView = (CameraSurfaceView) findViewById(R.id.cameraSurfaceView);
        mRectOnCamera = (RectOnCamera) findViewById(R.id.rectOnCamera);
        mCircleOnCamera = (CircleOnCamera) findViewById(R.id.circleOnCamera);
        takePicBtn = (ImageView) findViewById(R.id.takePic);
        uploadBtn = (ImageView) findViewById(R.id.btnUpload);
        cancelBtn = (ImageView) findViewById(R.id.btnCancel);
        mCircleOnCamera.startAnimation(disappearAnimation);
        uploadUtil = UploadUtil.getInstance();
        uploadUtil.setOnUploadProcessListener(new UploadUtil.OnUploadProcessListener(){

            @Override
            public void onUploadDone(int responseCode, String message) {

            }

            @Override
            public void onUploadProcess(int uploadSize) {

            }

            @Override
            public void initUpload(int fileSize) {

            }
        });
    }

    public void initEvents() {
        mCircleOnCamera.setIAutoFocus(this);
        takePicBtn.setOnClickListener(this);
        uploadBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    public void initAnimations() {
        appearAnimation = new AlphaAnimation(0, 1);
        appearAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mCircleOnCamera.setAlpha(0);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mCircleOnCamera.clearAnimation();
                mCircleOnCamera.setAlpha(1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        appearAnimation.setDuration(500);
        disappearAnimation = new AlphaAnimation(1, 0);
        disappearAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mCircleOnCamera.setAlpha(1);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mCircleOnCamera.clearAnimation();
                mCircleOnCamera.setAlpha(0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        disappearAnimation.setDuration(500);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.takePic:
                mCameraSurfaceView.takePicture();
                resetView(true);
                break;
            case R.id.btnUpload:
                String picPath = mCameraSurfaceView.getPicPath();
                String[] picPaths = picPath.split("/");
                String picName = picPaths[picPaths.length-1];
                uploadUtil.uploadFile(picPath, picName, "http://192.168.1.112:8181/pm25/pm25/upload", null);
                Log.i(TAG,picPath);
                break;
            case R.id.btnCancel:
                if (mIsTakePic) {
                    resetView(false);
                } else {
                    this.finish();
                }
                break;
            default:
                break;
        }
    }

    private void resetView(boolean isTakePic) {
        this.mIsTakePic = isTakePic;
        if (isTakePic) {
            takePicBtn.setVisibility(View.GONE);
            uploadBtn.setVisibility(View.VISIBLE);
//            mRectOnCamera.setVisibility(View.GONE);
            mCircleOnCamera.setVisibility(View.GONE);
        } else {
            uploadBtn.setVisibility(View.GONE);
            takePicBtn.setVisibility(View.VISIBLE);
//            mRectOnCamera.setVisibility(View.VISIBLE);
            mCircleOnCamera.setVisibility(View.VISIBLE);
            mCameraSurfaceView.resetCamerParams();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "3-------------onTouchEvent");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                if (mCircleOnCamera.VISIBLE != View.GONE)
                    Log.i(TAG, "circleOnCameraFlashing...........");
                mCircleOnCamera.startAnimation(disappearAnimation);
                return true;
        }
        return true;
    }


    @Override
    protected void onRestart() {
        super.onRestart();
//        this.resetView(false);
//        mCameraSurfaceView.resetCamerParams();
    }

    @Override
    public void autoFocus() {
        Log.i(TAG, "autoFocus");
        mCameraSurfaceView.setAutoFocus();
    }

}
