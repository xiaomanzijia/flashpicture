package com.pqm.morepaizhao;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by licheng on 15/3/16.
 */
public class GestureActivity extends Activity implements View.OnTouchListener,GestureDetector.OnGestureListener {

    private GestureDetector gestureDetector = new GestureDetector(this);

    private ImageView imageView;

    private int imgLength;

    private String strCaptureFilePath = Environment
            .getExternalStorageDirectory() + "/DCIM/Camera/";

    File[] pics;

    //图片索引位置
    private int picindex = 0;

    private float downX;

    //每滑动20像素切换一张图片
    private float distance = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gesture_layout);

        imageView = (ImageView) findViewById(R.id.imageView);

        File file = new File(strCaptureFilePath+"pictest/");
        if(file.exists()){
            pics = file.listFiles();
            imgLength = pics.length;
        }

        imageView.setOnTouchListener(this);

        imageView.setLongClickable(true);

        gestureDetector.setIsLongpressEnabled(true);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        //滑动屏幕的横坐标
        float xx = e2.getX();

        Log.i("XX","downX:"+downX+"  xx:"+xx);

        if((Math.abs(xx - downX)) > distance){

            //向右滑动
            if(distanceX < 0){
                picindex ++;
            }
            //向左滑动
            else {
                picindex --;
                if(picindex < 0){
                    picindex = imgLength;
                }
            }

            Log.i("index",picindex+"");

            File file =  pics[Math.abs(picindex) % imgLength];

            Bitmap bitmap= BitmapFactory.decodeFile(file.getAbsolutePath());

            imageView.setImageBitmap(bitmap);

            //把滑动结束后抬手的横坐标赋值给downX
            downX = xx;
        }


        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_DOWN:
                //记录每次手指按下时候的屏幕横坐标
                downX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:

                break;
        }
        return true;
    }
}
