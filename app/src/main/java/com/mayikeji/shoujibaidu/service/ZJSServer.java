package com.mayikeji.shoujibaidu.service;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;


import com.mayikeji.shoujibaidu.application.ClientApplication;

import java.util.List;

/**
 * Created by Administrator on 2016/12/21.
 */

public class ZJSServer extends QiangDanServer {

    private String packages = "com.zallfuhui.driver";
    private String prive_id = "com.zallfuhui.driver:id/tv_order_total_fee";
    private String from_km = "com.zallfuhui.driver:id/tv_away_from_km";

    public ZJSServer(OnQiangDanListener listener) {
        super(listener);
    }

    @Override
    public void onEvent(AccessibilityNodeInfo rootInActiveWindow) {

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event, AccessibilityNodeInfo info) {
        switch (event.getEventType()){
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                BaseServer.mInstance.openNotification(event);
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String className = event.getClassName().toString();
                Log.e("TAG",className);
                if (className.equals("com.zallfuhui.driver.chauffeur.structure.BottomNavigationActivity")) {
                    hasOrder(info);
                } else if (className.equals("com.zallfuhui.driver.chauffeur.activity.GrabDialogActivity")) {
                    hasOrder(info);
                } else if (className.equals("com.lalamove.huolala.OrderDetailActivity")) {
//                    openPacket("抢单",info);
                } else if (className.equals("android.app.Dialog")) {
//                    openPacket("确定",info);
                }
                
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void startActivity() {
        if (!isTopActivity(packages)) {
            PackageManager packageManager = ClientApplication.getInstance().getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(packages);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
            ClientApplication.getInstance().startActivity(intent);
        }else{
            AccessibilityNodeInfo rootInActiveWindow = BaseServer.mInstance.getRootInActiveWindow();
            hasOrder(rootInActiveWindow);
        }
    }

    @Override
    public boolean isTop() {
        return isTopActivity(packages);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void hasOrder(AccessibilityNodeInfo info){
        List<AccessibilityNodeInfo> infos = info.findAccessibilityNodeInfosByText("马上抢");
        for (AccessibilityNodeInfo i:infos) {
            AccessibilityNodeInfo parent = i.getParent().getParent();
            List<AccessibilityNodeInfo> price = parent.findAccessibilityNodeInfosByViewId(prive_id);
            List<AccessibilityNodeInfo> from = parent.findAccessibilityNodeInfosByViewId(from_km);
            if (price.size() == 0 || from.size() == 0){
                continue;
            }
            int tprice = 0;
            float f;
            String s = from.get(0).getText().toString();
            try {
                tprice = Integer.valueOf(price.get(0).getText().toString());
                if (s.indexOf("公里") != -1 || s.indexOf("千米") != -1){
                    s = s.replace("公里","").replace("千米","");
                    f = Float.valueOf(s);
                }else{
                    s.replace("米","");
                    f = Float.valueOf(s) / 1000;
                }
//                f = Float.valueOf(s.replace("公里","").replace("米","").replace("千米",""));
            }catch (Exception e){
                continue;
            }

            int[] price1 = getPrice();
            int[] dis = getDis();
            boolean d = s.indexOf("米") != -1? true : f > dis[0] && f < dis[1];
            if (tprice > price1[0] && tprice < price1[1] && d);
                i.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    @Override
    public String getPackageName() {
        return packages;
    }
}
