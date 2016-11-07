package com.way.ui.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import com.way.common.util.BitmapUtils;
import com.way.common.util.FileUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

@SuppressWarnings("deprecation")
public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Camera.AutoFocusCallback {

    private static final String TAG = "CameraSurfaceView";
    public static final String SD_IMAGES_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator + "PM25" + File.separator;
    public static final String DATA_IMAGES_PATH = "/data/data/PM25/images/";

    private Context mContext;
    private SurfaceHolder holder;
    private Camera mCamera;

    private int mScreenWidth;
    private int mScreenHeight;
    private int viewWidth;
    private int viewHeight;
    //所拍摄图片的路径
    private String picPath;
    public CameraSurfaceView(Context context) {
        this(context, null);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        getScreenMetrix(context);
        initView();
    }

    private void getScreenMetrix(Context context) {
        WindowManager WM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        WM.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;
    }

    private void initView() {
        holder = getHolder();//获得surfaceHolder引用
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//设置类型
        viewHeight = holder.getSurfaceFrame().height();
        viewWidth = holder.getSurfaceFrame().width();
        Log.i(TAG, "viewHeight:" + viewHeight + ",viewWidth:" + viewWidth);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated");
        if (mCamera == null) {
            mCamera = Camera.open();//开启相机
            try {
                mCamera.setPreviewDisplay(holder);//摄像头画面显示在Surface上
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged");
        //设置参数并开始预览
        updateCameraParameters(mCamera);
//        setCameraParams(mCamera, mScreenWidth, mScreenHeight);
        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed");
        mCamera.stopPreview();//停止预览
        mCamera.release();//释放相机资源
        mCamera = null;
        holder = null;
    }

    @Override
    public void onAutoFocus(boolean success, Camera Camera) {
        if (success) {
            Camera.cancelAutoFocus();
            Log.i(TAG, "onAutoFocus success=" + success);
        }
    }

    // 拍照瞬间调用
    private Camera.ShutterCallback shutter = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            Log.i(TAG, "shutter");
        }
    };

    // 获得没有压缩过的图片数据
    private Camera.PictureCallback raw = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera Camera) {
            Log.i(TAG, "raw");
        }
    };

    //创建jpeg图片回调数据对象
    private Camera.PictureCallback jpeg = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera Camera) {
            BufferedOutputStream bos = null;
            Bitmap bm = null;
            try {
                // 获得图片
                bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                bm = BitmapUtils.resizeImage(bm, 255, 255);
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    Log.i(TAG, "Environment.getExternalStorageDirectory()=" + Environment.getExternalStorageDirectory());
                    String imageDir = getImageDir();
                    String filePath = imageDir + "/OMG_" + System.currentTimeMillis() + ".jpg";//照片保存路径
                    File file = FileUtils.createFile(filePath);
                    Log.i(TAG, filePath);
                    bos = new BufferedOutputStream(new FileOutputStream(file));
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);//将图片压缩到流中
                    picPath = filePath;
                } else {
                    Toast.makeText(mContext, "没有检测到内存卡", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    bos.flush();//输出
                    bos.close();//关闭
                    bm.recycle();// 回收bitmap空间
                    mCamera.stopPreview();// 关闭预览
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * 获得拍摄图片路径
     * @return
     */
    public String getPicPath(){
        return this.picPath;
    }
    public Camera getCamera() {
        return mCamera;
    }

    public void setAutoFocus() {
        mCamera.autoFocus(this);
    }

    public void takePicture() {
        //设置参数,并拍照
        updateCameraParameters(mCamera);
        // 当调用camera.takePiture方法后，camera关闭了预览
        mCamera.takePicture(null, null, jpeg);
    }

    /**
     * 给外部调用者开放一个重新设置相机参数的方法
     */
    public void resetCamerParams() {
        updateCameraParameters(mCamera);
        mCamera.startPreview();
    }

    /**
     * 更新相机参数
     *
     * @param camera
     */
    private void updateCameraParameters(Camera camera) {
        if (camera != null) {
            Camera.Parameters p = camera.getParameters();
            long time = new Date().getTime();
            p.setGpsTimestamp(time);
            Camera.Size previewSize = findBestPreviewSize(p);
            p.setPreviewSize(previewSize.width, previewSize.height);
            //这里设置图片的分辨率为预览图像的分辨率大小，这样拍照后的照片就与看到的一样（主要是清晰度），实际上可以根据需求自定定义
            p.setPictureSize(previewSize.width, previewSize.height);
            if (mContext.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                camera.setDisplayOrientation(90);
                p.setRotation(90);
            }
            if (p.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                Log.i(TAG, "SET FOCUS_MODE_CONTINUOUS_PICTURE");
                p.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 连续对焦模式
            }
            p.setJpegQuality(100);
            camera.cancelAutoFocus();
            camera.setParameters(p);
        }
    }

    /**
     * 找到最合适的显示分辨率 （防止预览图像变形）
     *
     * @param parameters
     * @return
     */
    private Camera.Size findBestPreviewSize(Camera.Parameters parameters) {
        //系统支持的所有预览分辨率
        String previewSizeValueString = parameters.get("preview-size-values");
        if (previewSizeValueString == null) {
            previewSizeValueString = parameters.get("preview-size-value");
        }
        Log.i(TAG, previewSizeValueString);
        if (previewSizeValueString == null) { // 有些手机例如m9获取不到支持的预览大小 就直接返回屏幕大小
            return mCamera.new Size(mScreenWidth, mScreenHeight);
        }
        float bestX = 0;
        float bestY = 0;

        float tmpRatio = 0;
        float viewRatio = 0;
        if (viewWidth != 0 && viewHeight != 0) {
            viewRatio = Math.min((float) viewWidth, (float) viewHeight) / Math.max((float) viewWidth, (float) viewHeight);
        }
        String[] COMMA_PATTERN = previewSizeValueString.split(",");
        Log.i(TAG, "previewSizeValueString:" + previewSizeValueString);
        for (String prewsizeString : COMMA_PATTERN) {
            prewsizeString = prewsizeString.trim();
            int dimPosition = prewsizeString.indexOf('x');
            if (dimPosition == -1) {
                continue;
            }
            float newX = 0;
            float newY = 0;
            try {
                newX = Float.parseFloat(prewsizeString.substring(0, dimPosition));
                newY = Float.parseFloat(prewsizeString.substring(dimPosition + 1));
            } catch (NumberFormatException e) {
                continue;
            }
            float ratio = Math.min(newX, newY) / Math.max(newX, newY);
            if (viewRatio == 0) {
                viewRatio = ratio;
                bestX = newX;
                bestY = newY;
            } else if (viewRatio != 0 && (Math.abs(ratio - viewRatio)) < (Math.abs(ratio - viewRatio))) {
                tmpRatio = ratio;
                bestX = newX;
                bestY = newY;
            }
        }
        if (bestX > 0 && bestY > 0) {
            return mCamera.new Size((int) bestX, (int) bestY);
        }
        return null;
    }

    /**
     * 获取拍摄图片存储路径
     *
     * @return
     */
    private String getImageDir() {
        String storagePath = null;
        String sdStatus = Environment.getExternalStorageState();
        if (sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            storagePath = SD_IMAGES_PATH;
        } else {
            storagePath = DATA_IMAGES_PATH;
        }
        return storagePath;
    }
}
