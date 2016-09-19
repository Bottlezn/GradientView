package com.test.wdh.gradientview.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.test.wdh.gradientview.R;
import com.test.wdh.gradientview.component.LinearGradientView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by wdh on 2016/9/9.
 * 主页。啥用没有
 */
public class MainActivity extends Activity {

    private HorizontalScrollView mHSV;
    private ViewPager mVP;
    private LinearLayout container;
    private ArrayList<String> strings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_linear_gradient);
        initView();
    }

    private void initView() {
        for (int i = 0; i < 4; i++) {
            strings.add("【" + i + "】页");
        }
        container = (LinearLayout) findViewById(R.id.container);
        mVP = (ViewPager) findViewById(R.id.vp);
        for (String s : strings) {
            LinearGradientView view = new LinearGradientView(this);
            view.setText(s);
            view.setDefaultColor(Color.BLUE);
            view.setTargetColor(Color.RED);
            view.setPadding(10, 10, 10, 10);
            container.addView(view);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
            params.gravity = Gravity.CENTER;
            params.weight = 1;
            view.requestLayout();
        }
        MyPageAdapter myPageAdapter = new MyPageAdapter(this, strings);
        mVP.setAdapter(myPageAdapter);

        mVP.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int currrentIndex = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                scrollOne(positionOffset);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void scrollOne(float value) {
        View view = container.getChildAt(0);
        if (view != null && view instanceof LinearGradientView) {
            LinearGradientView linearGradientView = (LinearGradientView) view;
            linearGradientView.setColorPercentage(value);
        }
    }

    private void scroll(int from, int to, float positionOffset) {

    }


    private static class MyPageAdapter extends PagerAdapter {

        private HashMap<Integer, ImageView> mMap;
        private Context mContext;
        private ArrayList<String> mList;

        public MyPageAdapter(Context context, ArrayList<String> list) {
            mMap = new HashMap<>();
            mContext = context;
            mList = list;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView;
            if (mMap.get(position) == null) {
                imageView = new ImageView(mContext);
                imageView.setImageResource(R.drawable.ic_launcher);
                container.addView(imageView);
                mMap.put(position, imageView);
            } else {
                imageView = mMap.get(position);
                container.addView(imageView);
            }

            return imageView;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mMap.get(position));
            mMap.remove(position);
        }
    }

}
