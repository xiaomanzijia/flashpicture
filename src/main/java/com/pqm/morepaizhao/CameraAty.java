package com.pqm.morepaizhao;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class CameraAty extends Activity implements SurfaceHolder.Callback {

	private Camera mCamera;
	private ImageView mButton;
	private SurfaceView mSurfaceView;
	private SurfaceHolder holder;
	private AutoFocusCallback mAutoFocusCallback = new AutoFocusCallback();
	private ImageView sendImageIv;

	private String strCaptureFilePath = Environment
			.getExternalStorageDirectory() + "/DCIM/Camera/";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* 隐藏状态栏 */
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		/* 隐藏标题栏 */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		/* 设定屏幕显示为横向 */
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		setContentView(R.layout.photograph_layout);
		/* SurfaceHolder设置 */
		mSurfaceView = (SurfaceView) findViewById(R.id.mSurfaceView);
		holder = mSurfaceView.getHolder();
		holder.addCallback(CameraAty.this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
				// TODO Auto-generated method stub
//				mCamera.autoFocus(mAutoFocusCallback);
		/* 设置拍照Button的OnClick事件处理 */
		mButton = (ImageView) findViewById(R.id.myButton);
		mButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				/* 告动对焦后拍照 */
//				mCamera.autoFocus(mAutoFocusCallback);
//				takePhoto.sendEmptyMessage(0);
				timer.schedule(timerTask,1000,80000);

				System.out.println("完成照相功能！");
			}
		});
	}

	Timer timer = new Timer();
	TimerTask timerTask = new TimerTask() {
		@Override
		public void run() {
			Message message = new Message();
			message.what = 1;
			takePhoto.sendMessage(message);
		}
	};

	Handler takePhoto = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case 1:
					takePicture();
					break;
				default:
					break;
			}
		}
	};

	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			mCamera.autoFocus(mAutoFocusCallback);
			super.handleMessage(msg);
		}
	};

	public void surfaceCreated(SurfaceHolder surfaceholder) {
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

	public void surfaceChanged(SurfaceHolder surfaceholder, int format, int w,
			int h) {
		/* 相机初始化 */
		initCamera();
	}

	public void surfaceDestroyed(SurfaceHolder surfaceholder) {
		stopCamera();
		mCamera.release();
		mCamera = null;
	}

	/* 拍照的method */
	private void takePicture() {
		if (mCamera != null) {
			mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
			System.out.println("this is takePicture()");

		}
	}
	/* 拍照的method */
	private void takePicture2() {
		if (mCamera != null) {
			mCamera.takePicture(shutterCallback, rawCallback, jpegCallback2);
			System.out.println("this is takePicture2()");

		}
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {

		System.out.println("intent=" + intent);
		System.out.println("requestCode=" + requestCode);

		super.startActivityForResult(intent, requestCode);

	}

	private ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			/* 按下快门瞬间会调用这里的程序 */
			System.out.println("this is onShtter");
		}
	};

	private PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] _data, Camera _camera) {
			/* 要处理raw data?写?否 */
			System.out.println("this is onPictureTaken");
		}
	};



	Camera.PreviewCallback prviewCallback = new Camera.PreviewCallback() {
		@Override
		public void onPreviewFrame(byte[] bytes, Camera camera) {

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			YuvImage yuvImage = new YuvImage(bytes, ImageFormat.NV21, 500,500, null);
			yuvImage.compressToJpeg(new Rect(0, 0, 500, 500), 50, out);
			byte[] imageBytes = out.toByteArray();
			Bitmap image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
			System.out.println("-------previewcallback----"+image);
		}
	};

	private PictureCallback jpegCallback2 = new PictureCallback() {
		public void onPictureTaken(byte[] _data, Camera _camera) {

			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) // 判断SD卡是否存在，并且可以可以读写
			{

			} else {
				Toast.makeText(CameraAty.this, "SD卡不存在或写保护",
						Toast.LENGTH_LONG).show();
			}

			try {

				Calendar c = Calendar.getInstance();
				String time = formatTimer(c.get(Calendar.YEAR)) + "-"
						+ formatTimer(c.get(Calendar.MONTH)) + "-"
						+ formatTimer(c.get(Calendar.DAY_OF_MONTH)) + " "
						+ formatTimer(c.get(Calendar.HOUR_OF_DAY)) + "."
						+ formatTimer(c.get(Calendar.MINUTE)) + "."
						+ formatTimer(c.get(Calendar.SECOND));
				System.out.println("现在时间：" + time + "  将此时间当作图片名存储");

				/* 取得相片 */
				Bitmap bm = BitmapFactory.decodeByteArray(_data, 0,
						_data.length);
				System.out.println("-------xiangpian----"+bm);
//				handler.sendEmptyMessage(0);
//				/* 创建文件 */
				File myCaptureFile = new File(strCaptureFilePath, "" + time
						+ ".jpg");

				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(myCaptureFile));
				/* 采用压缩转档方法 */
				bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);

				System.out.println("this is pass");

				/* 调用flush()方法，更新BufferStream */
				bos.flush();
//
//				/* 结束OutputStream */
				bos.close();

				/* 让相片显示3秒后圳重设相机 */
				// Thread.sleep(2000);
				/* 重新设定Camera */
				stopCamera();
				initCamera();

//				takePicture2();takePicture2
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};


	private PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] _data, Camera _camera) {

			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) // 判断SD卡是否存在，并且可以可以读写
			{

			} else {
				Toast.makeText(CameraAty.this, "SD卡不存在或写保护",
						Toast.LENGTH_LONG).show();
			}

			try {

				Calendar c = Calendar.getInstance();
				String time = formatTimer(c.get(Calendar.YEAR)) + "-"
						+ formatTimer(c.get(Calendar.MONTH)) + "-"
						+ formatTimer(c.get(Calendar.DAY_OF_MONTH)) + " "
						+ formatTimer(c.get(Calendar.HOUR_OF_DAY)) + "."
						+ formatTimer(c.get(Calendar.MINUTE)) + "."
						+ formatTimer(c.get(Calendar.SECOND));
				System.out.println("现在时间：" + time + "  将此时间当作图片名存储");

				/* 取得相片 */
				Bitmap bm = BitmapFactory.decodeByteArray(_data, 0,
						_data.length);
				System.out.println("-------xiangpian----"+bm);
//				handler.sendEmptyMessage(0);
//				/* 创建文件 */
			File myCaptureFile = new File(strCaptureFilePath, "" + time
					+ ".jpg");

				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(myCaptureFile));
				/* 采用压缩转档方法 */
				bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);

				System.out.println("this is pass");

				/* 调用flush()方法，更新BufferStream */
				bos.flush();
//
//				/* 结束OutputStream */
				bos.close();

				/* 让相片显示3秒后圳重设相机 */
				// Thread.sleep(2000);
				/* 重新设定Camera */
//				stopCamera();
				initCamera();

				takePicture2();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	AlertDialog.Builder b;

	/**
	 * 转换时间
	 * 
	 * @param d
	 * @return
	 */
	public String formatTimer(int d) {
		return d >= 10 ? "" + d : "0" + d;
	}

	/* 告定义class AutoFocusCallback */
	public final class AutoFocusCallback implements
			android.hardware.Camera.AutoFocusCallback {
		public void onAutoFocus(boolean focused, Camera camera) {
			/* 对到焦点拍照 */
			if (focused) {
				takePicture();
			}
		}
	};

	/* 相机初始化的method */
	private void initCamera() {
		if (mCamera != null) {
			Log.i("CameraAty","相机初始化");
			try {
				Camera.Parameters parameters = mCamera.getParameters();
				/*
				 * 设定相片大小为1024*768， 格式为JPG
				 */
				parameters.setPictureFormat(PixelFormat.JPEG);
				parameters.setPictureSize(500, 500);
				mCamera.setParameters(parameters);
				/* 打开预览画面 */
				mCamera.startPreview();
				mCamera.setPreviewCallback(prviewCallback);
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

	public final static int ID_USER = 0;

	Runnable r = new Runnable() {

		public void run() {
			// TODO Auto-generated method stub
			Message msg = new Message();
			msg.what = ID_USER;
			mHandler.sendMessage(msg);
		}
	};

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case ID_USER:
				System.out.println("tanchu s");
				Toast.makeText(CameraAty.this, "���һС��Ŷ...", Toast.LENGTH_SHORT)
						.show();

				break;
			}
		};
	};

}