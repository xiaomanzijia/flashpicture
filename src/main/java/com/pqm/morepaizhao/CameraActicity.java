package com.pqm.morepaizhao;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by licheng on 15/3/16.
 */
public class CameraActicity extends Activity implements SurfaceHolder.Callback {

    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private SurfaceHolder holder;
    private ImageView mButton;
    private Button btnTestPic;

    public int pictureOrientation = 0;

    private int intScreenWidth;
    private int intScreenHeight;

    File secondFile;

    private String strCaptureFilePath = Environment
            .getExternalStorageDirectory() + "/DCIM/Camera/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* 隐藏状态栏 */
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		/* 隐藏标题栏 */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		/* 设定屏幕显示为横向 */
//        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.photograph_layout);

        getDisplayMetrics();

		/* SurfaceHolder设置 */
        mSurfaceView = (SurfaceView) findViewById(R.id.mSurfaceView);
        btnTestPic = (Button) findViewById(R.id.btnTestPic);
        holder = mSurfaceView.getHolder();
        holder.addCallback(CameraActicity.this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // TODO Auto-generated method stub
//				mCamera.autoFocus(mAutoFocusCallback);
		/* 设置拍照Button的OnClick事件处理 */
        mButton = (ImageView) findViewById(R.id.myButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                mCamera.setPreviewCallback(prviewCallback);
                secondFile = new File(strCaptureFilePath + "pictest" + "/");
                if(!secondFile.exists()){
                    secondFile.mkdir();
                }
            }
        });

        btnTestPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CameraActicity.this,GestureActivity.class);
                startActivity(intent);
            }
        });
    }

    Camera.PreviewCallback prviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] bytes, Camera camera) {

            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) // 判断SD卡是否存在，并且可以可以读写
            {

            } else {
                Toast.makeText(CameraActicity.this, "SD卡不存在或写保护",
                        Toast.LENGTH_LONG).show();
            }


            final byte[] yuvdata = bytes;

//            Thread save = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.v("Camera","save jpeg size"+yuvdata.length);
//                    String fileName = System.currentTimeMillis() + ".jpeg";
//                    Log.i("Camera-Orientation",pictureOrientation+"");
//                    boolean mIsSave = Storage.savePicture(yuvdata, fileName,
//                            mCamera.getParameters().getPreviewSize(),
//                            pictureOrientation);
//                    if(!mIsSave){
//                        Log.v("Camema","save fail");
//                    }
//                }
//            });
//            save.start();

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Calendar c = Calendar.getInstance();
            String time = formatTimer(c.get(Calendar.YEAR)) + "-"
                    + formatTimer(c.get(Calendar.MONTH)) + "-"
                    + formatTimer(c.get(Calendar.DAY_OF_MONTH)) + " "
                    + formatTimer(c.get(Calendar.HOUR_OF_DAY)) + "."
                    + formatTimer(c.get(Calendar.MINUTE)) + "."
                    + formatTimer(c.get(Calendar.SECOND));
            System.out.println("现在时间：" + time + "  将此时间当作图片名存储");


            YuvImage yuvImage = new YuvImage(bytes, ImageFormat.NV21, mCamera.getParameters().getPreviewSize().width,mCamera.getParameters().getPreviewSize().height, null);
            yuvImage.compressToJpeg(new Rect(0, 0, mCamera.getParameters().getPreviewSize().width, mCamera.getParameters().getPreviewSize().height), 50, out);
            byte[] imageBytes = out.toByteArray();
            Bitmap image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            System.out.println("-------previewcallbacktest----"+image);
            Log.i("CameraActivity","-------previewcallbacktest----"+image);

            //图片旋转方向
            Bitmap bMapRotate;
            Matrix matrix = new Matrix();
            matrix.reset();
            matrix.postRotate(90);
            bMapRotate = Bitmap.createBitmap(image, 0, 0, image.getWidth(),
                    image.getHeight(), matrix, true);
            image = bMapRotate;

            File second = new File(secondFile,""+time+".jpg");

            BufferedOutputStream bos = null;
            try {
                bos = new BufferedOutputStream(
                        new FileOutputStream(second));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            /* 采用压缩转档方法 */
            image.compress(Bitmap.CompressFormat.JPEG, 100, bos);

            System.out.println("this is pass");

            try {
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };

    private void setDisplayOrientation(Context context){
        int rotation = ((Activity) context).getWindowManager()
                .getDefaultDisplay().getRotation();
        int degree = 0;
        switch (rotation) {
            case Surface.ROTATION_0:	degree = 0; break;
            case Surface.ROTATION_90:	degree = 90; break;
            case Surface.ROTATION_180:	degree = 180; break;
            case Surface.ROTATION_270:	degree = 270; break;
        }
        int result;
        Camera.CameraInfo info = new Camera.CameraInfo();
        //Camera.getCameraInfo(mBackCameraId, info);
        Camera.getCameraInfo(0, info);
        if(info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
            result = (info.orientation + degree) % 360;
            result = (360 - result) % 360;
        }else{
            result =(info.orientation - degree + 360 ) % 360;
        }
        mCamera.setDisplayOrientation(result);
        Log.v("Camera","result:"+result);
        pictureOrientation = result;
    }

    /* 相机初始化的method */
    private void initCamera() {
        if (mCamera != null) {
            setDisplayOrientation(this);
            Log.i("CameraAty","相机初始化");
            try {
               Camera.Parameters parameters = mCamera.getParameters();
				/*
				 * 设定相片大小为1024*768， 格式为JPG
				 */
               parameters.setPictureFormat(PixelFormat.JPEG);
//               parameters.setPictureSize(intScreenWidth, intScreenHeight);
//               parameters.setPreviewSize(intScreenWidth,intScreenHeight);
               mCamera.setParameters(parameters);
				/* 打开预览画面 */mCamera.startPreview();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* 停止相机的method */
    private void stopCamera() {
        if (mCamera != null) {
            try {
				/* 停止预览 */
                mCamera.stopPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
			/* 打开相机， */
            System.out.println("打开照相功能！");
            mCamera = Camera.open();
            mCamera.setPreviewDisplay(holder);
        } catch (IOException exception) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        initCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        stopCamera();
        mCamera.release();
        mCamera = null;
    }

    public String formatTimer(int d) {
        return d >= 10 ? "" + d : "0" + d;
    }

    private void getDisplayMetrics() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        intScreenWidth = dm.widthPixels;
        intScreenHeight = dm.heightPixels;
    }
}
