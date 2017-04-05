package com.mayikeji.shoujibaidu.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mayikeji.shoujibaidu.utils.MyLog;
import com.mayikeji.shoujibaidu.R;
import com.mayikeji.shoujibaidu.application.ClientApplication;
import com.mayikeji.shoujibaidu.helper.UiHelper;
import com.mayikeji.shoujibaidu.service.BaseServer;
import com.mayikeji.shoujibaidu.service.FiveEightServer;
import com.mayikeji.shoujibaidu.service.HuoLalaServer;
import com.mayikeji.shoujibaidu.service.YiHaoServer;
import com.mayikeji.shoujibaidu.service.ZJSServer;
import com.mayikeji.shoujibaidu.utils.DataCleanManager;
import com.mayikeji.shoujibaidu.utils.ShareUtils;
import com.zhy.autolayout.AutoLayoutActivity;

/**
 * Created by Everly on 2017/3/29.
 * <p>
 * 主界面
 */

public class MainActivity extends AutoLayoutActivity implements View.OnClickListener {

    private RelativeLayout layout_qdjl, layout_setting, layout_lx, layout_hc,layout_error;
    private Button btn_start;

    public static Context context;
    private TextView server_status;
    private HuoLalaServer huoLalaServer;
    private YiHaoServer yihaoserver;
    private FiveEightServer fiveEightServer ;
    private MyBroadcastReciver myBroadcastReciver ;
    private ZJSServer zjsServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        getSupportActionBar().hide();
        statusBar();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BaseServer.isRunning){
            btn_start.setText("关闭抢单");
            server_status.setText("当前开启中");
        }else {
            server_status.setText("当前未开启");
            btn_start.setText("开启自动抢单");
        }
        synchronized (ClientApplication.getInstance()) {
            if (ShareUtils.getBoolean(this,"sw_hll",false)) {
                if (!BaseServer.servers.contains(huoLalaServer))
                    BaseServer.servers.add(huoLalaServer);
            } else {
                BaseServer.servers.remove(huoLalaServer);
            }
            if (ShareUtils.getBoolean(this,"sw_yhhd",false)) {
                if (!BaseServer.servers.contains(yihaoserver))
                    BaseServer.servers.add(yihaoserver);
            } else {
                BaseServer.servers.remove(yihaoserver);
            }
            if (ShareUtils.getBoolean(this,"sw_58",false)) {
                if (!BaseServer.servers.contains(zjsServer))
                    BaseServer.servers.add(zjsServer);
            } else {
                BaseServer.servers.remove(zjsServer);
            }

            if (ShareUtils.getBoolean(this,"sw_zjs",false)) {
                if (!BaseServer.servers.contains(fiveEightServer))
                    BaseServer.servers.add(fiveEightServer);
            } else {
                BaseServer.servers.remove(fiveEightServer);
            }
        }
        anyMethod();
    }
    // 此方法用来判断当前应用的辅助功能服务是否开启
    public static boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.i("", e.getMessage());
        }

        if (accessibilityEnabled == 1) {
            String services = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (services != null) {
                return services.toLowerCase().contains(context.getPackageName().toLowerCase());
            }
        }

        return false;
    }


    private void anyMethod() {
        // 判断辅助功能是否开启
        if (!isAccessibilitySettingsOn(getApplicationContext())) {
            Toast.makeText(getApplicationContext(),"请先开启服务",Toast.LENGTH_SHORT).show();
            // 引导至辅助功能设置页面
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            // 执行辅助功能服务相关操作
        }
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
        layout_error = (RelativeLayout) findViewById(R.id.layout_error);

        server_status = (TextView) findViewById(R.id.server_status);
        btn_start = (Button) findViewById(R.id.btn_start);

        layout_qdjl.setOnClickListener(this);
        layout_setting.setOnClickListener(this);
        layout_lx.setOnClickListener(this);
        layout_hc.setOnClickListener(this);
        layout_error.setOnClickListener(this);
        btn_start.setOnClickListener(this);

        huoLalaServer = new HuoLalaServer(null);
        yihaoserver = new YiHaoServer(null);
        fiveEightServer = new FiveEightServer(null) ;
        zjsServer = new ZJSServer(null);
        myBroadcastReciver = new MyBroadcastReciver();
        register();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_qdjl:
                startActivity(new Intent(this,OrderActivity.class));
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
            case R.id.layout_error:
                MyLog.uploadLog();
                break;
            case R.id.btn_start:
                if (isFirst()) {
                    startActivity(new Intent(this, SettingActivity.class));
                } else {
                    //开启服务
                    if (BaseServer.isRunning) {
                        BaseServer.isRunning = false;
                        server_status.setText("当前未开启");
                        ((AppCompatButton)v).setText("开启自动抢单");

                    }else{
                        BaseServer.isRunning = true;
                        BaseServer.checkServerRunning();
                        Toast.makeText(getApplicationContext(), "开启服务成功", Toast.LENGTH_SHORT).show();
                        ((Button)v).setText("关闭抢单");
                        server_status.setText("当前开启中");
                    }
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

    private void register() {
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(myBroadcastReciver, new IntentFilter("server_message"));
    }

    private void unrigister() {
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(myBroadcastReciver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unrigister();
    }

    private class MyBroadcastReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("server_message")) {
                Bundle bundle = intent.getExtras() ;
                boolean result = bundle.getBoolean("result") ;
                if (result) {
                    UiHelper.showToast(getApplicationContext() , "抢到订单了");
                    BaseServer.isRunning = false;
                    server_status.setText("运输在途");
                    btn_start.setText("开启自动抢单");
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
