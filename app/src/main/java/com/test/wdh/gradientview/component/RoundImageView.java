package com.test.wdh.gradientview.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.test.wdh.gradientview.R;

/**
 * Created by wdh on 2016/9/9.
 * 圆形ImageView实现,建议View的宽高像素相等
 * 该视图总会将ImageView的最短边作为内部圆形图像的直径，用以处理图像缩放，移动，
 * 然后绘制到视图中心
 */
public class RoundImageView extends ImageView implements ViewTreeObserver.OnGlobalLayoutListener {

    private boolean isInitPaint = false;

    private static final String TAG = "RoundImageView";

    private int mBorderColor = Color.WHITE;

    private float mBorderWidth = 0;

    private static final Bitmap.Config CONFIG = Bitmap.Config.ARGB_4444;

    private final static int COLOR_DRAWABLE_PX = 1;
    private ColorMatrix mColorMatrix = new ColorMatrix();

    public boolean getGray() {
        return isGray;
    }

    /**
     * 填充模式
     */
    private boolean fillType = false;

    /**
     * 圆形模式
     */
    public static final int ROUND_STYLE = 0;


    /**
     * 圆角模式
     */
    public static final int ROUND_RECT_STYLE = 1;

    /**
     * 绘制风格
     */
    private int drawStyle = 0;

    private int mWidth = 0;
    private int mHeight = 0;
    private PointF centerPoint = null;
    private Bitmap mBitmap;

    private BitmapShader mBitmapShader;

    private Paint mBitmapPaint;

    private Paint mBorderPaint;

    private float mBitmapRadius;

    private float mCorner = 0;
    private RectF mRoundRect;

    private RectF mBorderRect;

    /**
     * 是否是灰色的
     */
    private boolean isGray = false;

    /**
     * 设置灰度信息
     *
     * @param gray true设置为灰色
     */
    public void setIsGray(boolean gray) {
        if (isGray != gray) {
            isReset = true;
            isGray = gray;
            resetDrawProperties();
            invalidate();
        }
    }

    /**
     * @param fillType 正方形图片这个值可以不设置。长方形图片设置为true，那么会
     *                 将图片压缩为一张正方形图片，展示压缩后的图形。设置为false
     *                 展示对图片宽高比不做压缩，将图片缩放到最短边等于ImageView
     *                 的直径，然后截取中间圆形图像展示。
     */
    public void setFillType(boolean fillType) {
        if (this.fillType == fillType) {
            return;
        }
        this.fillType = fillType;
    }

    /**
     * 是否重新设置图片，如果重新设置那就设置为true
     */
    private boolean isReset = false;

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
            d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
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
        isReset = true;
        resetDrawProperties();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        isReset = true;
        if (isInitPaint) { //不做此步处理会报错，使用xml加载的RoundImageView，会在构造方法中调用该方法，如果不做处理的话就不可以在xml中设置默认src文件了。
            resetDrawProperties();
        }
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        isReset = true;
        resetDrawProperties();
    }

    private void resetDrawProperties() {
        mBitmap = getBitmapFromDrawable(getDrawable());
        if (mBitmap == null) {
            return;
        }
        setupPaint();
    }

    /**
     * 设置绘制
     *
     * @param drawStyle 只能是ROUND_STYLE或者ROUND_RECT_STYLE的一种，前者是圆形头像，后者是圆角头像。
     */
    public void setDrawStyle(@IntRange(from = 0, to = 1) int drawStyle) {
        if (this.drawStyle == drawStyle) {
            return;
        }
        this.drawStyle = drawStyle;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (null == getDrawable()) {
            return;
        }
        if (centerPoint != null) {
            drawContent(canvas);
        }

    }

    private void drawContent(Canvas canvas) {
        if (drawStyle == 0) {
            canvas.drawCircle(centerPoint.x, centerPoint.y, mBitmapRadius, mBitmapPaint);
            canvas.drawCircle(centerPoint.x, centerPoint.y, mBitmapRadius, mBorderPaint);
        } else {
            canvas.drawRoundRect(mRoundRect, mCorner, mCorner, mBitmapPaint);
            canvas.drawRoundRect(mRoundRect, mCorner, mCorner, mBorderPaint);
        }
    }


    /**
     * 获取位图信息以后，设置画笔内容
     */
    private void setupPaint() {
        if (!isReset) {
            return;
        }
        isReset = false;
        float w = mBitmap.getWidth();
        float h = mBitmap.getHeight();
        float diameter = Math.min(mWidth, mHeight);
        mBitmapRadius = diameter / 2.0f - mBorderWidth;//头像画圆，取最小的边为半径或者正方形边长
        //计算图像着色器范围
        Matrix matrix = new Matrix();
        float sX = diameter / w;
        float sY = diameter / h;
        float sMax = Math.max(sX, sY);
        matrix.postTranslate((mWidth - w) / 2.0f, (mHeight - h) / 2.0f);
        //根据填充类型调整位图着色器图像内容
        if (!fillType) {
            matrix.postScale(sMax, sMax, mWidth / 2.0f, mHeight / 2.0f);
        } else {
            matrix.postScale(sX, sY, mWidth / 2.0f, mHeight / 2.0f);
        }
        if (drawStyle == 1 && centerPoint != null) {//圆角模式，更改图像的矩阵和边缘的矩阵
            mRoundRect = new RectF(centerPoint.x - mBitmapRadius, centerPoint.y - mBitmapRadius, centerPoint.x + mBitmapRadius, centerPoint.y + mBitmapRadius);
        }
        //设置位图的画笔
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mBitmapShader.setLocalMatrix(matrix);
        mBitmapPaint.setShader(mBitmapShader);
        if (isGray) {
            mColorMatrix.setSaturation(0f);
        } else {
            mColorMatrix.setSaturation(1f);
        }
        mBitmapPaint.setColorFilter(new ColorMatrixColorFilter(mColorMatrix));
        //设置边框的画笔
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setColor(mBorderColor);

    }

    /**
     * 初始化画笔
     */
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
        } else {
            getViewTreeObserver().addOnGlobalLayoutListener(null);
        }
    }

    private void setProperties(TypedArray array) {
        mBorderColor = array.getColor(R.styleable.RoundImageView_borderColor, mBorderColor);
        mBorderWidth = array.getDimension(R.styleable.RoundImageView_borderWidth, mBorderColor);
        fillType = array.getBoolean(R.styleable.RoundImageView_fillType, false);
        mCorner = array.getDimension(R.styleable.RoundImageView_corner, 0);
        drawStyle = array.getInt(R.styleable.RoundImageView_drawStyle, 0);
        drawStyle = drawStyle == 0 || drawStyle == 1 ? drawStyle : 0;
        isGray = array.getBoolean(R.styleable.RoundImageView_isGray, false);
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
        isReset = true;
        resetDrawProperties();
    }
}
