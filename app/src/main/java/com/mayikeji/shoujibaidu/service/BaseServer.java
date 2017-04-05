package com.mayikeji.shoujibaidu.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.view.accessibility.AccessibilityEvent;

import com.mayikeji.shoujibaidu.application.ClientApplication;
import com.mayikeji.shoujibaidu.helper.UiHelper;
import com.mayikeji.shoujibaidu.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/13.
 */

public class BaseServer extends AccessibilityService implements OnQiangDanListener {

    public static BaseServer mInstance;

    public static List<QiangDanServer> servers = new ArrayList<>(4);

    public static boolean isRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo info = getServiceInfo();
        //这里可以设置多个包名，监听多个应用|com.cxyw.suyun.ui|com.midou.tchy.consignee
        info.packageNames = new String[]{"com.cxyw.suyun.ui", "com.midou.tchy.consignee", "com.lalamove.huolala.driver","com.zallfuhui.driver"};
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED|AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED|AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ;
        setServiceInfo(info);
        super.onServiceConnected();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (isRunning) {
            synchronized (ClientApplication.getInstance()) {
                for (QiangDanServer server : servers) {
                    server.onAccessibilityEvent(event, getRootInActiveWindow());
                }
            }
        }
    }

    static int index = 0 ;

    public static void checkServerRunning() {
        UiHelper.getInstance().runOnThread(new Runnable() {
            @Override
            public void run() {
                int size;
                synchronized (ClientApplication.getInstance()) {
                    size = servers.size();
                }
                for (int i = 0; i < size; i++) {
                    QiangDanServer server = null;
                    synchronized (ClientApplication.getInstance()) {
                        try {
                            server = servers.get(i);
                        } catch (Exception e) {
                        }
                    }
                    if (isRunning) {
                        synchronized (this) {
                            try {
                                if (server.isTop() || index >= 300){
                                    index = 0;
                                    server.startActivity();
                                    SystemClock.sleep(1000); //当只有一个应用抢单的时候 时间为60秒
                                }else{
                                    index++;
                                    SystemClock.sleep(1000);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if (isRunning)
                    UiHelper.getInstance().runOnThread(this);
            }
        });
    }

    @Override
    public void onInterrupt() {

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackView() {
        performGlobalAction(GLOBAL_ACTION_BACK);
    }

    @Override
    public void openNotification(AccessibilityEvent event) {
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

    @Override
    public void onGetOrder() {
        //抢到一个订单了
        Intent intent = new Intent("server_message");
        Bundle bundle = new Bundle();
        bundle.putBoolean("result", true);
        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(MainActivity.context).sendBroadcast(intent);
    }
}
