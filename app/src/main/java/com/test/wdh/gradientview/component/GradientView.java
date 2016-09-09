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
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import java.io.IOException;

/**
 * Created by wdh on 2016/9/9.
 * 测试图片着色器
 */
public class GradientView extends View implements ViewTreeObserver.OnGlobalLayoutListener {

    private Paint mPaint;
    private Bitmap mBitmap;
    private Rect clampRect, repeatRect, mirrorRect;
    private BitmapShader mClamp, mRepeat, mMirror;
    private boolean once = false;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


    }

    /**
     * 初始化画笔
     */
    private void initPaint(Context context) {
        mPaint = new Paint();

        Rect rect = new Rect(0, 0, 100, 100);
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ALPHA_8;
            mBitmap = BitmapFactory.decodeStream(context.getResources().getAssets().open("a.png", AssetManager.ACCESS_BUFFER), rect, options);


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
            mClamp = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mClamp.setLocalMatrix(matrix);
            mRepeat = new BitmapShader(mBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            mRepeat.setLocalMatrix(matrix);
            mMirror = new BitmapShader(mBitmap, Shader.TileMode.MIRROR, Shader.TileMode.MIRROR);
            mMirror.setLocalMatrix(matrix);
        } else {
            throw new IllegalArgumentException("传入的图片资源不行啊！");
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setShader(mClamp);
        canvas.drawRect(clampRect, mPaint);
        mPaint.setShader(mRepeat);
        canvas.drawRect(repeatRect, mPaint);
        mPaint.setShader(mMirror);
        canvas.drawRect(mirrorRect, mPaint);
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


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!once) {
            getViewTreeObserver().addOnGlobalLayoutListener(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getViewTreeObserver().removeOnGlobalLayoutListener(this);
        } else {
            getViewTreeObserver().addOnGlobalLayoutListener(null);
        }
    }

    @Override
    public void onGlobalLayout() {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        clampRect = new Rect(0, 0, width, 100);
        repeatRect = new Rect(0, 200, width, 300);
        mirrorRect = new Rect(0, 400, width, 500);

    }
}
