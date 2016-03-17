package com.pqm.morepaizhao;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by licheng on 17/3/16.
 */
public class ImageActivity extends Activity {

    private MatrixImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gesture_layout);

        imageView = (MatrixImageView) findViewById(R.id.imageView);

        imageView.setImageResource(R.drawable.ic_launcher);

    }
}
