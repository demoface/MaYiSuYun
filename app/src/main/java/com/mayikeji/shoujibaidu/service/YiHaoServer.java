package com.mayikeji.shoujibaidu.service;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;


import com.mayikeji.shoujibaidu.application.ClientApplication;

import java.util.List;

/**
 * author : solon
 * date: on 16/12/8.
 */

public class YiHaoServer extends QiangDanServer {

    private String splashActivity = "com.midou.tchy.consignee.activity.SplashActivity" ;
    private String loginLoading = "com.midou.tchy.consignee.view.dialog.LoadingDialog" ;
    private boolean splash = false ;


    private String packages = "com.midou.tchy.consignee" ;

    private final String TAG = "SOLON" ;
//    List<AccessibilityNodeInfo> node2 = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ar6");
    //抢单dialog 解析出来的 View id
    private final String dialog_id_qiangdan = "com.midou.tchy.consignee:id/dialog_grab_l" ;//抢单 LinearnLayout
    private final String dialog_id_address_start = "com.midou.tchy.consignee:id/dialog_start" ;//起点
    private final String dialog_id_address_end = "com.midou.tchy.consignee:id/dialog_target" ;//终点
    private final String dialog_id_price = "com.midou.tchy.consignee:id/dialog_price" ;//预估价格
    private final String dialog_id_time = "com.midou.tchy.consignee:id/dialog_distance";//订单时间
    private final String dialog_id_close = "com.midou.tchy.consignee:id/close" ;//右上角取消按钮

    //主界面解析出来的View id
    private final String main_baoming = "com.midou.tchy.consignee:id/morder_grab" ;//报名
    private final String main_time = "com.midou.tchy.consignee:id/morder_grab" ;//时间
    private final String main_start_address = "com.midou.tchy.consignee:id/morder_start_address" ;//主界面起点
    private final String main_end_address = "com.midou.tchy.consignee:id/morder_target_address" ;//主界面终点

    private final String main_start_qiangdan = "com.midou.tchy.consignee:id/btn_start_receive_order" ;//主界面抢单按钮
    private final String main_stop_qiangdan = "com.midou.tchy.consignee:id/btn_receiveing_order" ;//主界面抢单按钮

    public YiHaoServer(OnQiangDanListener listener) {
        super(listener);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void dialogOrder(AccessibilityNodeInfo rootInActiveWindow , float realPrice) {
        try {
            List<AccessibilityNodeInfo> qiangdans = rootInActiveWindow.findAccessibilityNodeInfosByViewId(dialog_id_qiangdan);
            if (qiangdans.size() == 0) {
                Log.d(TAG , "没有找到抢单按钮") ;
                BaseServer.mInstance.onBackView();
//                mListener.onBackView();
                return;
            }

            qiangdans.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);

            List<AccessibilityNodeInfo> start_adds = rootInActiveWindow.findAccessibilityNodeInfosByViewId(dialog_id_address_start);
            String start_add = "" ;
            if (start_adds.size() > 0) {
                AccessibilityNodeInfo info = start_adds.get(0) ;
                start_add = info.getText().toString() ;
            }

            List<AccessibilityNodeInfo> endt_adds = rootInActiveWindow.findAccessibilityNodeInfosByViewId(dialog_id_address_end);
            String endt_add = "" ;
            if (endt_adds.size() > 0) {
                AccessibilityNodeInfo info = endt_adds.get(0) ;
                endt_add = info.getText().toString() ;
            }

            List<AccessibilityNodeInfo> order_times = rootInActiveWindow.findAccessibilityNodeInfosByViewId(dialog_id_time);
            String order_time = "" ;
            if (order_times.size() > 0) {
                AccessibilityNodeInfo info = order_times.get(0) ;
                order_time = info.getText().toString() ;
            }

            Log.d(TAG , "起点:" + start_add + "\n" + "终点:" + endt_add + "\n" + "time:" + order_time) ;

            commit(realPrice + "" , "一号货的");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void selectMoney(AccessibilityNodeInfo rootInActiveWindow) {
        try {
            List<AccessibilityNodeInfo> infoprices = rootInActiveWindow.findAccessibilityNodeInfosByViewId(dialog_id_price);
            if (infoprices.size() == 0) {
                Log.d(TAG , "没有获取到价格") ;
                return;
            }
            String moneyText = infoprices.get(0).getText().toString() ;
            String moneyFloatText = moneyText.substring(2 , moneyText.length()) ;
            float realMoney = Float.parseFloat(moneyFloatText) ;
            int[] setMoney = getPrice() ;

            int minMoney = setMoney[0] ;
            int maxMoney = setMoney[1] ;

            if (realMoney >= minMoney && realMoney <= maxMoney) {
                Log.e(TAG ,"进入抢单") ;
                dialogOrder(rootInActiveWindow , realMoney) ;
            } else {
                Log.e(TAG ,"不再查看此订单") ;
                BaseServer.mInstance.onBackView();
//                mListener.onBackView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void startQiangdan(AccessibilityNodeInfo rootInActiveWindow) {
        try {
            List<AccessibilityNodeInfo> infoprices = rootInActiveWindow.findAccessibilityNodeInfosByViewId(main_start_qiangdan);
            if (infoprices.size() == 0) {
                Log.d(TAG , "没有获取到开始工作") ;
                return;
            }

            infoprices.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent, AccessibilityNodeInfo rootInActiveWindow) {
        int type = accessibilityEvent.getEventType();
        switch (type){
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                Log.d(TAG,"TYPE_NOTIFICATION_STATE_CHANGED..........");
                List<CharSequence> texts = accessibilityEvent.getText();
                for (CharSequence cs:texts) {
                    Log.d(TAG,"TYPE_NOTIFICATION_STATE_CHANGED................................." + cs);
                }

                BaseServer.mInstance.openNotification(accessibilityEvent);
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String className = accessibilityEvent.getClassName().toString();
                Log.d(TAG,"TYPE_WINDOW_STATE_CHANGED................................." + className);
                if (className.equals("com.midou.tchy.consignee.view.dialog.NewOrderDialog")) {
                    selectMoney(rootInActiveWindow) ;
                } else if (className.equals("com.midou.tchy.consignee.activity.MainActivity")) {
                    splash = true ;
                    startQiangdan(rootInActiveWindow) ;
                } else if (className.equals(splashActivity)) {
                    if (splash) {
                        BaseServer.mInstance.onBackView();
//                        mListener.onBackView();
                    } else {
                        splash = true ;
                    }
                } else if (className.equals(loginLoading)) {
                    if (splash) {
                        BaseServer.mInstance.onBackView();
                    }
//                    mListener.onBackView();
                } else if (className.equals("com.midou.tchy.consignee.activity.LoginActivity")) {
                    if (splash) {
                        BaseServer.mInstance.onBackView();
                    }
                }
                break;
        }
    }

    @Override
    public void onEvent(AccessibilityNodeInfo rootInActiveWindow) {
        selectMoney(rootInActiveWindow) ;
    }

    @Override
    public void startActivity() {
        if (!isTopActivity(packages)) {
            PackageManager packageManager = ClientApplication.getInstance().getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(packages);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
//            intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED) ;
            ClientApplication.getInstance().startActivity(intent);

        }else{
            AccessibilityNodeInfo rootInActiveWindow = BaseServer.mInstance.getRootInActiveWindow();
            selectMoney(rootInActiveWindow);
        }
    }

    @Override
    public boolean isTop() {
        return isTopActivity(packages);
    }

    private void openApp(String packageName1) {
        PackageInfo pi = null;
        try {
            pi = ClientApplication.getInstance().getPackageManager().getPackageInfo(packageName1, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(pi.packageName);

        List<ResolveInfo> apps = ClientApplication.getInstance().getPackageManager().queryIntentActivities(resolveIntent, 0);

        ResolveInfo ri = apps.iterator().next();
        if (ri != null ) {
            String packageName = ri.activityInfo.packageName;
            String className = ri.activityInfo.name;

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);
            ClientApplication.getInstance().startActivity(intent);
        }
    }

    @Override
    public String getPackageName() {
        return packages;
    }
}
