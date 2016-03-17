package com.pqm.morepaizhao;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by licheng on 17/3/16.
 */
public class StoragePicure {

    public static void savePicture(byte[] data, Camera.Size size,File file){
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Calendar c = Calendar.getInstance();
        String time = formatTimer(c.get(Calendar.YEAR)) + "-"
                + formatTimer(c.get(Calendar.MONTH)) + "-"
                + formatTimer(c.get(Calendar.DAY_OF_MONTH)) + " "
                + formatTimer(c.get(Calendar.HOUR_OF_DAY)) + "."
                + formatTimer(c.get(Calendar.MINUTE)) + "."
                + formatTimer(c.get(Calendar.SECOND));
        System.out.println("现在时间：" + time + "  将此时间当作图片名存储");

        YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, size.width,size.height, null);
        yuvImage.compressToJpeg(new Rect(0, 0, size.width, size.height), 50, out);
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

        File second = new File(file,""+time+".jpg");

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

    private static String formatTimer(int d) {
        return d >= 10 ? "" + d : "0" + d;
    }
}
