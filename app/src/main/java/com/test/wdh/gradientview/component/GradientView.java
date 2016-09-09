package com.test.wdh.gradientview.component;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.io.IOException;

/**
 * Created by wdh on 2016/9/9.
 * 测试图片着色器
 */
public class GradientView extends View {

    private Paint mPaint;
    private Bitmap mBitmap;
    private Rect mRect;

    /**
     * 初始化画笔
     */
    private void initPaint(Context context) {
        mPaint = new Paint();
        Rect rect = new Rect(0, 0, 100, 100);
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ALPHA_8;
            mBitmap = BitmapFactory.decodeStream(context.getResources().getAssets().open("delta.png", AssetManager.ACCESS_BUFFER), rect, options);


        } catch (IOException e) {
            Log.e("WTF", e.toString());
        }
        if (mBitmap != null) {
            int width = mBitmap.getWidth();
            int height = mBitmap.getHeight();
            Matrix matrix = new Matrix();
            float scaleX = 100.0f / width;
            float scaleY = 100.0f / height;
            matrix.postScale(scaleX, scaleY, 0, 0);
            BitmapShader shader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            shader.setLocalMatrix(matrix);
            mPaint.setShader(shader);
        } else {
            throw new IllegalArgumentException("传入的图片资源不行啊！");
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getMeasuredWidth();
        mRect = new Rect(0, 0, width, 100);
        canvas.drawRect(mRect, mPaint);
    }

    public GradientView(Context context) {
        super(context);
        initPaint(context);
    }

    public GradientView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint(context);
    }

    public GradientView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint(context);
    }
}
