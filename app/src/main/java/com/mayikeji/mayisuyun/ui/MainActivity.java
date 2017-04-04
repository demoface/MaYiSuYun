package com.mayikeji.mayisuyun.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mayikeji.mayisuyun.R;
import com.mayikeji.mayisuyun.utils.DataCleanManager;
import com.mayikeji.mayisuyun.utils.ShareUtils;
import com.zhy.autolayout.AutoLayoutActivity;

/**
 * Created by Everly on 2017/3/29.
 * <p>
 * 主界面
 */

public class MainActivity extends AutoLayoutActivity implements View.OnClickListener {

    private RelativeLayout layout_qdjl, layout_setting, layout_lx, layout_hc;
    private Button btn_start;
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        getSupportActionBar().hide();
        statusBar();
        initView();
    }

    private void statusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    private void initView() {
        layout_qdjl = (RelativeLayout) findViewById(R.id.layout_qdjl);
        layout_setting = (RelativeLayout) findViewById(R.id.layout_setting);
        layout_lx = (RelativeLayout) findViewById(R.id.layout_lx);
        layout_hc = (RelativeLayout) findViewById(R.id.layout_hc);

        btn_start = (Button) findViewById(R.id.btn_start);

        layout_qdjl.setOnClickListener(this);
        layout_setting.setOnClickListener(this);
        layout_lx.setOnClickListener(this);
        layout_hc.setOnClickListener(this);
        btn_start.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_qdjl:

                break;
            case R.id.layout_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.layout_lx:

                break;
            case R.id.layout_hc:
                DataCleanManager.clearAllCache(this);
                Toast.makeText(this,"缓存清理完成！",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_start:
                if (isFirst()) {
                    startActivity(new Intent(this, SettingActivity.class));
                } else {
                    //开启服务
                    String min_price = ShareUtils.getString(getApplicationContext(), "min_price", "");

                    String sw_hll = ShareUtils.getString(this, "check_dhString", "");
                    Log.i("@@@", "======" + sw_hll);
                }

                break;
        }
    }

    public Boolean isFirst() {
        Boolean setting = ShareUtils.getBoolean(this, "setting", true);
        if (setting) {
            ShareUtils.putBoolean(this, "setting", false);
            return true;
        }
        return false;
    }
}
