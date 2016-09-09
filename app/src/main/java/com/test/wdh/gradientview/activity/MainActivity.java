package com.test.wdh.gradientview.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.test.wdh.gradientview.R;

/**
 * Created by wdh on 2016/9/9.
 * 主页。啥用没有
 */
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Shader shader;
        ImageView imageView;
        ColorDrawable colorDrawable=new ColorDrawable();
        Bitmap bitmap=Bitmap.createBitmap(100,100, Bitmap.Config.ALPHA_8);
        Canvas canvas=new Canvas(bitmap);
        colorDrawable.draw(canvas);
    }
}
