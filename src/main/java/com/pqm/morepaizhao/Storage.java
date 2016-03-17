package com.pqm.morepaizhao;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera.Size;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * for storage picture
 * 
 * @author swj
 *
 */
public class Storage {
	public static final String DCIM = Environment.
	             getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
	
	public static final String DIRECTORY = DCIM + File.separator+"Camera";


	
	public static boolean savePicture(byte[] jpeg, String fileName,
									  Size pictureSize, int pictureOrientation){


		
		String path = null;
		path = DIRECTORY + File.separator + fileName;
		Log.v("shenwenjian","savePicture DIRECTORY:"+DIRECTORY +" path:"+path);
		/**
		 * test 1
		 */
//		FileOutputStream out = null;
//		try {
//			File dir = new File(DIRECTORY);
//			if(!dir.exists()) {dir.mkdirs();Log.v("shenwenjian","not exits ");}
//			out = new FileOutputStream(path);
//			out.write(jpeg);
//			out.close();
//		} catch (Exception e) {
//			Log.v("shenwenjian","1 return false");
//			return false;
//		} finally{
//			try {
//				out.close();
//			} catch (Exception e2) {
//				// TODO: handle exception
//			}
//		}
//		return true;
		/**
		 * test 2
		 */
//		// picture operate
//		Bitmap bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
//		if(bitmap == null){
//			Log.v("shenwenjian","bitmap == null");
//			return false;
//		}
//		// picture operate
//		BufferedOutputStream out = null;
//		try {
//			File dir = new File(DIRECTORY);
//			if(!dir.exists()) {dir.mkdirs();Log.v("shenwenjian","not exits ");}
//			out = new BufferedOutputStream(new FileOutputStream(path));
//			bitmap.compress(CompressFormat.JPEG, 80, out);
//			out.close();
//			bitmap.recycle();
//		} catch (Exception e) {
//			return false;
//		}
		/**
		 * YUV TO RGB
		 * test 3
		 */
		FileOutputStream out = null;
		try {
			File dir = new File(DIRECTORY);
			if(!dir.exists()) {dir.mkdirs();
				Log.v("shenwenjian","not exits ");}
			YuvImage yuvImage = new YuvImage(jpeg,
					ImageFormat.NV21, pictureSize.width, pictureSize.height, null);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			yuvImage.compressToJpeg(new Rect(0,0,pictureSize.width,pictureSize.height), 100, baos);
			out = new FileOutputStream(path);
			byte[] data = baos.toByteArray();
			out.write(data);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("error",e.toString());
			return false;
		}
		readExifInfo(path,pictureOrientation);
		return true;
	}


	public static void readExifInfo(String path,int pictureOrientation){
		ExifInterface exifInterface = null;
		try {
			exifInterface = new ExifInterface(path);
		} catch (Exception e) {
			// TODO: handle exception
		}
		int tag = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
		Log.i("Storage",tag+"");
		int orientation = 0;
		switch(tag){
		case ExifInterface.ORIENTATION_ROTATE_90:  orientation = 90;  break;
		case ExifInterface.ORIENTATION_ROTATE_180: orientation = 180; break;
		case ExifInterface.ORIENTATION_ROTATE_270: orientation = 270; break;
		}

		Log.i("StorageResult",orientation+"");
		
		try {
			exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, orientation+"");
			exifInterface.saveAttributes();
			//exifInterface.
		} catch (IOException e) {
			Log.v("shenwenjian","saveAttributes fail");
			e.printStackTrace();
		}
		Log.v("shenwenjian","tag:"+tag+" orientation:"+orientation+"get ori:"+exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1));
	}

	public static void rotatePicture(int orientation, String patch, int width, int height){
		//Options option = new Options();
	}
	
}
