package com.mayikeji.shoujibaidu.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.mayikeji.shoujibaidu.R;
import com.mayikeji.shoujibaidu.utils.StaticClass;
import com.zhy.autolayout.AutoLayoutActivity;

/**
 * Created by Everly on 2017/3/29.
 *
 * 闪屏界面
 */

public class SplashActivity extends AutoLayoutActivity {

    //延时3秒进入主页
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case StaticClass.HANDLER_SPLASH:
                    startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                    finish();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initView();
    }

    private void initView() {
        handler.sendEmptyMessageDelayed(StaticClass.HANDLER_SPLASH,2000);
    }

    //禁用返回键
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
