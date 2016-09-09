package com.test.wdh.gradientview.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.test.wdh.gradientview.R;

/**
 * Created by wdh on 2016/9/9.
 * 圆形头像实现,建议View的宽高像素相等
 */
public class RoundImageView extends ImageView implements ViewTreeObserver.OnGlobalLayoutListener {

    private boolean isInitPaint = false;

    private static final String TAG = "RoundImageView";

    private int mBorderColor = Color.WHITE;

    private float mBorderWidth = 0;

    private static final Bitmap.Config CONFIG = Bitmap.Config.ALPHA_8;

    private static int COLOR_DRAWABLE_PX = 1;

    private int mWidth = 0;
    private int mHeight = 0;
    private PointF centerPoint = null;
    private Bitmap mBitmap;

    private BitmapShader mBitmapShader;

    private Paint mBitmapPaint;

    private Paint mBorderPaint;

    private float bitmapRadius;

    /**
     * 通过Drawable对象获取位图
     *
     * @param d 使用getDrawable()方法获取出来的d
     * @return 返回的位图对象
     */
    private Bitmap getBitmapFromDrawable(@NonNull Drawable d) {
        if (d instanceof BitmapDrawable) {
            return ((BitmapDrawable) d).getBitmap();
        }
        try {
            Bitmap bitmap;
            if (d instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLOR_DRAWABLE_PX, COLOR_DRAWABLE_PX, CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), CONFIG);
            }
            Canvas canvas = new Canvas(bitmap);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            d.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = getBitmapFromDrawable(getDrawable());
        if (mBitmap == null) {
            return;
        }
        setupPaint();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        if (isInitPaint) { //不做此步处理会报错，使用xml加载的RoundImageView，会在构造方法中调用该方法，如果不做处理的话就不可以在xml中设置默认src文件了。
            mBitmap = getBitmapFromDrawable(getDrawable());
            if (mBitmap == null) {
                return;
            }
            setupPaint();
        }
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        mBitmap = getBitmapFromDrawable(getDrawable());
        if (mBitmap == null) {
            return;
        }
        setupPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (null == getDrawable()) {
            return;
        }
        if (centerPoint != null) {
            canvas.drawCircle(centerPoint.x, centerPoint.y, bitmapRadius, mBitmapPaint);
            canvas.drawCircle(centerPoint.x, centerPoint.y, bitmapRadius, mBorderPaint);
        }
    }


    /**
     * 获取位图信息以后，设置画笔内容
     */
    private void setupPaint() {
        float w = mBitmap.getWidth();
        float h = mBitmap.getHeight();
        float diameter = Math.min(mWidth, mHeight);
        bitmapRadius = diameter / 2.0f - mBorderWidth;//头像画圆，取最小的边为半径
        //计算图像着色器范围
        Matrix matrix = new Matrix();
        float sX = 1.0f;
        float sY = 1.0f;
        if (w < diameter * 2.0f) {//图像宽度小于直径
            sX = diameter / w;
        }
        if (h < diameter * 2.0f) {//图像高度小于直径
            sY = diameter / h;
        }
        sX = Math.max(sX, sY);
        matrix.postScale(sX, sY);
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mBitmapShader.setLocalMatrix(matrix);
        mBitmapPaint.setShader(mBitmapShader);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setColor(mBorderColor);

    }

    private void initPaint() {
        mBitmapPaint = new Paint();
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBitmapPaint.setAntiAlias(true);
        isInitPaint = true;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }else {
            getViewTreeObserver().addOnGlobalLayoutListener(null);
        }
    }

    private void setProperties(TypedArray array) {
        mBorderColor = array.getColor(R.styleable.RoundImageView_borderColor, mBorderColor);
        mBorderWidth = array.getDimension(R.styleable.RoundImageView_borderWidth, mBorderColor);
    }

    @Override
    public void setScaleType(ScaleType scaleType) {

    }

    public RoundImageView(Context context) {
        super(context);
        initPaint();
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView, defStyleAttr, 0);
        setProperties(array);
        array.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        centerPoint = new PointF(w / 2.0f, h / 2.0f);
    }

    @Override
    public void onGlobalLayout() {
        mBitmap = getBitmapFromDrawable(getDrawable());
        if (mBitmap == null) {
            return;
        }
        setupPaint();
    }
}
