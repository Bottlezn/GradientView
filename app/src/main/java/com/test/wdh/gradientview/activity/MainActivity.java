package com.test.wdh.gradientview.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.test.wdh.gradientview.R;
import com.test.wdh.gradientview.component.RoundImageView;

/**
 * Created by wdh on 2016/9/9.
 * 主页。啥用没有
 */
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RoundImageView iv = (RoundImageView) findViewById(R.id.iv);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv.setGray(!iv.getGray());
            }
        });
    }
}
