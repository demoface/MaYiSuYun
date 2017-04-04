package com.mayikeji.mayisuyun.service;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import com.mayikeji.mayisuyun.application.ClientApplication;
import com.mayikeji.mayisuyun.helper.DbHelper;
import com.mayikeji.mayisuyun.ui.MainActivity;

/**
 * Created by Administrator on 2016/11/30.
 */

public abstract class BaseAccessibilityService extends AccessibilityService {

    private static String TAG = "AccessibilityService";

    protected String package_name = "";
    static int isRunning = 0;

    public BaseAccessibilityService() {
        package_name = getAppPackageName();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                startServer();
            }
        }, new IntentFilter("start_server"));
        anyMethod();
    }

    public static synchronized void running() {
        if (!isRunning()) {
            LocalBroadcastManager.getInstance(MainActivity.context).sendBroadcast(new Intent("start_server"));
        }
        isRunning++;
    }

    public static synchronized void stop() {
        isRunning--;
    }

    public static synchronized boolean isRunning() {
        return isRunning == 0 ? false : true;
    }

    @Override
    protected void onServiceConnected() {
//        AccessibilityServiceInfo info = getServiceInfo();
//        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
//        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;
//        info.notificationTimeout = 100;
//        setServiceInfo(info);
//        info.packageNames = new String[]{package_name};
//        setServiceInfo(info);
        super.onServiceConnected();
    }

    private void stopServer() {
        Toast.makeText(getApplicationContext(), "关闭服务成功", Toast.LENGTH_SHORT).show();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

    }

    protected abstract String getAppPackageName();

    @Override
    public void onInterrupt() {

    }

    protected abstract void startServer();

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    void openNotification(AccessibilityEvent event) {
        //模拟打开通知栏消息
        if (event.getParcelableData() != null
                &&
                event.getParcelableData() instanceof Notification) {
            Notification notification = (Notification) event.getParcelableData();
            PendingIntent pendingIntent = notification.contentIntent;
            try {
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }

    public int[] getPrice() {
        return new int[]{ClientApplication.sp.getInt("min_price", 0), ClientApplication.sp.getInt("man_price", Integer.MAX_VALUE)};
    }

    // 此方法用来判断当前应用的辅助功能服务是否开启
    public static boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.i(TAG, e.getMessage());
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
            Toast.makeText(getApplicationContext(), "请先开启服务", Toast.LENGTH_SHORT).show();
            // 引导至辅助功能设置页面
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            // 执行辅助功能服务相关操作
        }
    }

    /**
     * 判断app是否在最前端
     *
     * @param packageName APP的包名
     * @return
     */
    public boolean isForegroundPkgViaDetectionService(String packageName) {
        return packageName.equals(package_name);
    }

    protected void putRecord(String time, String content, String price) {
        DbHelper.getInstance().getReadableDatabase().beginTransaction();
    }
}
