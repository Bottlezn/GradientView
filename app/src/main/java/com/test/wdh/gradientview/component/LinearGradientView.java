package com.test.wdh.gradientview.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.FloatRange;
import android.util.AttributeSet;
import android.widget.TextView;

import com.test.wdh.gradientview.R;


/**
 * Created by wdh on 2016/9/11.
 * 文字和纯色图片线性变化的View,继承自TextView，
 * 文字只在中间出现，不会出现在别的位置
 */
public class LinearGradientView extends TextView {

    private Paint mPaint;

    private int mTargetColor = Color.RED;
    private int mDefaultColor = Color.BLACK;
    private static final String TAG = "LinearGradientView";


    /**
     * 设置目标颜色
     *
     * @param targetColor 目标颜色
     */
    public void setTargetColor(int targetColor) {
        mTargetColor = targetColor;
    }

    /**
     * 设置默认颜色
     *
     * @param defaultColor 默认颜色
     */
    public void setDefaultColor(int defaultColor) {
        mTargetColor = defaultColor;
    }

    public LinearGradientView(Context context) {
        super(context);
        initPaint(context);
    }

    public LinearGradientView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinearGradientView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint(context);
        TypedArray array = context.obtainStyledAttributes(attrs,
                R.styleable.LinearGradientView, defStyleAttr, 0);
        setProperties(array);
        array.recycle();
    }

    private void setProperties(TypedArray array) {
        mTargetColor = array.getColor(R.styleable.LinearGradientView_targetColor, mTargetColor);
        mDefaultColor = array.getColor(R.styleable.LinearGradientView_defaultColor, mDefaultColor);
    }


    private void initPaint(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mDefaultColor);

    }

    /**
     * 设置颜色变换的百分比
     */
    private float mPercentage = 0;

    /**
     * 设置内容百分比
     *
     * @param percentage 从0~1，可以设置底版颜色值
     */
    public void setColorPercentage(@FloatRange(from = 0, to = 1) float percentage) {
        String s = getText().toString();
        if (s.length() == 0) {
            return;
        }
        this.mPercentage = percentage;
        invalidate();
    }


    @Override
    public void setTextSize(float size) {
        super.setTextSize(size);

    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        String s = getText().toString();
        if (s.length() == 0) {
            return;
        }
        drawContent(canvas, s);
    }

    private int mWidth = 0;
    private int mHeight = 0;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    private void drawContent(Canvas canvas, String s) {
        if (mPercentage == 1) {
            mPaint.setTextSize(getTextSize() + 2);
        } else {
            mPaint.setTextSize(getTextSize());
        }
        float contentWidth = mPaint.measureText(s);
        float contentHeight = mPaint.ascent() + mPaint.descent();
        PointF pointF = new PointF(mWidth / 2.0f, mHeight / 2.0f);
        float contentLeft = pointF.x - (contentWidth / 2.0f);
        float contentRight = pointF.x + (contentWidth / 2.0f);
        float contentTop = pointF.y - (contentHeight / 2.0f);
        float contentBottom = pointF.y + (contentHeight / 2.0f);
        mPaint.setShader(produceBitmapShader(mPercentage));
        canvas.drawText(s, contentLeft, contentTop, mPaint);
    }

    private BitmapShader produceBitmapShader(float percentage) {
        Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        ColorDrawable d1 = new ColorDrawable(mDefaultColor);
        ColorDrawable d2 = new ColorDrawable(mTargetColor);
        d1.setBounds(0, 0, (int) (mWidth * percentage), mHeight);
        d2.setBounds((int) (mWidth * percentage), 0, mWidth, mHeight);
        d1.draw(canvas);
        d2.draw(canvas);
        return new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    }


}
