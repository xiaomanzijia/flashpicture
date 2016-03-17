package com.pqm.morepaizhao;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by licheng on 15/3/16.
 */
public class SnapPicActivity extends Activity implements SurfaceHolder.Callback {

    final private static String STILL_IMAGE_FILE = "capture.jpg";
    final private static int IMAGE_NUM= 3;
    private String strCaptureFilePath = "/sdcard/camera_snap.jpg";
    private String TAG = "IsiCamera2Activity";
    private Button btn_capture;
    private Button btn_sum;
    private Camera camera = null;
    private SurfaceHolder mSurfaceHolder;
    private SurfaceView mSurfaceView01;
    private FrameLayout mFrameLayout01;
    private boolean bPreviewing =false;
    private int intScreenWidth;
    private int intScreenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* 使应用程序全屏幕运行，不使用title bar */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.takepic_layout);

        /* 取得屏幕解析像素 */
        getDisplayMetrics();
        findViews();
        getSurfaceHolder();
        btn_capture.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
//                jumpOutAToast("test onclick");
                //这里实现模拟连拍逻辑
                for(int i=0; i<IMAGE_NUM; i++){
                    takeAPicture();
                }

            }
        });
    }


    @Override
    protected void onPause() {
        //被交换到后台时执行
        //camera.release();
        //camera = null;
        bPreviewing = false;
//        jumpOutAToast("test onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
//        jumpOutAToast("test onStop");
        resetCamera();
        super.onStop();
    }

    @Override
    protected void onResume() {
        try {
            initCamera();
        } catch(IOException e ) {
            Log.e(TAG,"initCamera() in Resume() erorr!");
        }
//        jumpOutAToast("test onResume");
        super.onResume();
    }

    /* function:
     * 非preview时：实例化Camera,开始preview
     * 非preview时and相机打开时：再设置一次preview
     * preview时：不动作
     */
    private void initCamera() throws IOException
    {
        if(!bPreviewing)
        {
        /* 若相机非在预览模式，则打开相机 */
            camera = Camera.open();
            camera.setDisplayOrientation(90);
        }
        //非预览时and相机打开时，开启preview
        if (camera != null && !bPreviewing)
        {
            Log.i(TAG, "inside the camera");
        /* 创建Camera.Parameters对象 */
            Camera.Parameters parameters = camera.getParameters();
        /* 设置相片格式为JPEG */
            parameters.setPictureFormat(PixelFormat.JPEG);
        /* 指定preview的屏幕大小 */
            parameters.setPreviewSize(intScreenWidth, intScreenHeight);
        /* 设置图片分辨率大小 */
            parameters.setPictureSize(intScreenWidth, intScreenHeight);
        /* 将Camera.Parameters设置予Camera */
            camera.setParameters(parameters);
        /* setPreviewDisplay唯一的参数为SurfaceHolder */
            camera.setPreviewDisplay(mSurfaceHolder);
        /* 立即运行Preview */
            camera.startPreview();
            bPreviewing = true;
        }
    }

    /* func:停止preview,释放Camera对象*/
    private void resetCamera()
    {
        if (camera != null && bPreviewing)
        {
            camera.stopPreview();
        /* 释放Camera对象 */
            camera.release();
            camera = null;
            bPreviewing = false;
        }
    }
    /* func:停止preview*/
    private void stopPreview() {
        if (camera != null && bPreviewing) {
            Log.v(TAG, "stopPreview");
            camera.stopPreview();
        }
    }
    private void takeAPicture() {
        if (camera != null && bPreviewing)
        {
          /* 调用takePicture()方法拍照 */
            camera.takePicture
                    (shutterCallback, rawCallback, jpegCallback);//调用PictureCallback interface的对象作为参数
        }
    }

    /*func:获取屏幕分辨率*/
    private void getDisplayMetrics() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        intScreenWidth = dm.widthPixels;
        intScreenHeight = dm.heightPixels;
        Log.i(TAG, Integer.toString(intScreenWidth));
    }

    private Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback()
    {
        public void onShutter()
        {
            // Shutter has closed
        }
    };

    private Camera.PictureCallback rawCallback = new Camera.PictureCallback()
    {
        public void onPictureTaken(byte[] _data, Camera _camera)
        {
            // TODO Handle RAW image data
        }
    };

    private Camera.PictureCallback jpegCallback = new Camera.PictureCallback()
    {
        public void onPictureTaken(byte[] _data, Camera _camera)
        {
            //保存
            Bitmap bitmap= BitmapFactory.decodeByteArray(_data, 0, _data.length);
            if(bitmap!=null){
                File myCaptureFile=new File("/sdcard/test.jpg");

                BufferedOutputStream bos=null;
                try {
                    bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
                    bos.close();
                    bitmap.recycle();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            resetCamera();
            try {
                initCamera();
            } catch(Exception e) {
                Log.e(TAG, "initCamera Error after snapping");
            }
        }
    };

    /* get a fully initialized SurfaceHolder*/
    private void getSurfaceHolder() {
        mSurfaceHolder = mSurfaceView01.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    /* 把SurfaceView 加到FrameLayout 里*/
    private void addSurfaceViewToFrameLayout() {
        mFrameLayout01.addView(mSurfaceView01);
    }
//    /*func:弹出toast,主要做测试用 */
//    private void jumpOutAToast(String string) {
//        Toast.makeText(IsiCamera2Activity.this, string, Toast.LENGTH_SHORT).show();
//    }

    /* func:宣告界面元件Button capture,FrameLayout frame*/
    private void findViews() {
        btn_capture = (Button) findViewById(R.id.btn_capture);
        btn_sum = (Button) findViewById(R.id.btn_sum);
        /* 以SurfaceView作为相机Preview之用 */
        mSurfaceView01 = (SurfaceView) findViewById(R.id.mSurfaceView01);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if(!bPreviewing) {
            camera = Camera.open();
            camera.setDisplayOrientation(90);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        if(bPreviewing) {
            camera.stopPreview();
        }
        Camera.Parameters params = camera.getParameters();
        params.setPreviewSize(200, 200);
        camera.setParameters(params);
        try {
            camera.setPreviewDisplay(mSurfaceHolder);
        } catch(IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
        bPreviewing = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        camera.stopPreview();
        bPreviewing = false;
        camera.release();
        mSurfaceHolder = null;
    }
}
