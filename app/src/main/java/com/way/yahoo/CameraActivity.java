package com.way.yahoo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.way.ui.view.CameraSurfaceView;
import com.way.ui.view.RectOnCamera;


public class CameraActivity extends Activity implements View.OnClickListener, RectOnCamera.IAutoFocus {

    private CameraSurfaceView mCameraSurfaceView;
    private RectOnCamera mRectOnCamera;
    private Button takePicBtn;
    private static final String TAG = "CameraActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);
        mCameraSurfaceView = (CameraSurfaceView) findViewById(R.id.cameraSurfaceView);
        mRectOnCamera = (RectOnCamera) findViewById(R.id.rectOnCamera);
        takePicBtn = (Button) findViewById(R.id.takePic);
        mRectOnCamera.setIAutoFocus(this);
        takePicBtn.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.takePic:
                mCameraSurfaceView.takePicture();
                break;
            default:
                break;
        }
    }

    @Override
    public void autoFocus() {
        Log.i(TAG, "autoFocus");
        mCameraSurfaceView.setAutoFocus();
    }
}
