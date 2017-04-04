package com.mayikeji.mayisuyun.service;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.mayikeji.mayisuyun.application.ClientApplication;
import com.mayikeji.mayisuyun.helper.UiHelper;

import java.util.List;

/**
 * 58速运
 * author : solon
 * date: on 16/12/3.
 */

public class FiveEightServer extends QiangDanServer {

    private String packages = "com.cxyw.suyun.ui";

    //主界面
    private final String main_start_qiangdan = "com.cxyw.suyun.ui:id/btnStartWork" ;//开始抢单 停止抢单 button
    private final String main_money_number = "com.cxyw.suyun.ui:id/tv_estimate_price" ;//总价 textview
    private final String main_scan_layout = "com.cxyw.suyun.ui:id/layout_count_down" ;//查看外部的linearnLayout
    private final String main_scan_daojishi_time = "com.cxyw.suyun.ui:id/tv_count_down" ; //倒计时 TextView
    private final String main_address_start = "com.cxyw.suyun.ui:id/tv_start" ;//起点 textView
    private final String main_address_end = "com.cxyw.suyun.ui:id/tv_destination" ;//终点 textView

    private final String main_time_dis = "com.cxyw.suyun.ui:id/tv_time_or_distance" ;//距离 textView  （距您 84km or 距您 100m）

    //订单详情
    private final String order_bukan = "com.cxyw.suyun.ui:id/btn_skip_order" ;//不看这个订单 button
    private final String order_daojishi_time = "com.cxyw.suyun.ui:id/tvRobTime" ;//倒计时 textView
    private final String order_qiangdan_layout = "com.cxyw.suyun.ui:id/lineLay_can" ;//抢单 linearLayout
    private final String order_address_start_title = "com.cxyw.suyun.ui:id/tvStart" ;//起点 抬头 TextVuew
    private final String order_address_start_content = "com.cxyw.suyun.ui:id/tvStart2" ;//起点内容 TextView
    private final String order_address_end_title = "com.cxyw.suyun.ui:id/tv_pushOrder_End" ;//终点 抬头 TextView
    private final String order_address_end_content = "com.cxyw.suyun.ui:id/tv_pushOrder_End2" ; //终点 内容 TextView
    private final String order_price = "com.cxyw.suyun.ui:id/tv_price" ;//预估价格 TextView
    private final String order_send_time = "com.cxyw.suyun.ui:id/tv_reservation_date" ;//发货时间 TextView
    private final String order_no_touch_layout = "com.cxyw.suyun.ui:id/lineLay_cannot";//不能点击按钮 linearLayout
    private final String order_time_dis = "com.cxyw.suyun.ui:id/tv_distance" ; //订单距离 textView

    private final String TAG = "SOLON" ;

    public FiveEightServer(OnQiangDanListener listener) {
        super(listener);
    }

    private void dontScanOrder(AccessibilityNodeInfo rootInActiveWindow) {
        try{
            List<AccessibilityNodeInfo> infoprices = rootInActiveWindow.findAccessibilityNodeInfosByViewId(order_bukan);
            if (infoprices.size() == 0) {
                Log.d(TAG , "不看按钮为0") ;
                BaseServer.mInstance.onBackView();
                return;
            }

            AccessibilityNodeInfo info = infoprices.get(0) ;
            info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isNeedCheck = false ;

    private void checkOrder(final float realMoney) {

        UiHelper.getInstance().postThreadOnMain(new Runnable() {
            @Override
            public void run() {
                AccessibilityNodeInfo rootInActiveWindow = BaseServer.mInstance.getRootInActiveWindow();
                startOrder(rootInActiveWindow , realMoney) ;
            }
        } , 1000);
    }

    private void startOrder(AccessibilityNodeInfo rootInActiveWindow,float realMoney) {
        try{
            List<AccessibilityNodeInfo> infoprices = rootInActiveWindow.findAccessibilityNodeInfosByViewId(order_qiangdan_layout);
            if (infoprices.size() == 0) {
                Log.d(TAG , "抢单按钮为0") ;
                isNeedCheck = true ;
                checkOrder(realMoney) ;
                return;
            }

            isNeedCheck = false ;
            AccessibilityNodeInfo info = infoprices.get(0) ;
            info.performAction(AccessibilityNodeInfo.ACTION_CLICK);

            commit(realMoney+ "" , "58速运");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectOrderMoney(AccessibilityNodeInfo rootInActiveWindow) {
        try {
            List<AccessibilityNodeInfo> infoprices = rootInActiveWindow.findAccessibilityNodeInfosByViewId(order_price);
            if (infoprices.size() == 0) {
                Log.d(TAG , "没有获取到价格") ;
                return;
            }
            String moneyText = infoprices.get(0).getText().toString() ;
            Log.d(TAG , "订单获取到价格:" + moneyText) ;
            float realMoney = Float.parseFloat(moneyText) ;
            int[] setMoney = getPrice() ;

            int minMoney = setMoney[0] ;
            int maxMoney = setMoney[1] ;

            if (realMoney >= minMoney && realMoney <= maxMoney) {
                Log.e(TAG ,"开始抢单抢单") ;
                startOrder(rootInActiveWindow , realMoney) ;
            } else {
                Log.e(TAG ,"不再查看此订单") ;
                dontScanOrder(rootInActiveWindow) ;
//                BaseServer.mInstance.onBackView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectOrderDis(AccessibilityNodeInfo rootInActiveWindow) {
        try {
            //判断最小值有没有大于0
            int[] diss = getDis() ;
            int minDis = diss[0] ;
            int maxDis = diss[1] ;
            if (minDis == 0) {
                selectOrderMoney(rootInActiveWindow) ;
            } else {
                List<AccessibilityNodeInfo> infoDis = rootInActiveWindow.findAccessibilityNodeInfosByViewId(order_time_dis);
                if (infoDis.size() == 0) {
                    Log.d(TAG , "没有获取到距离" + infoDis.size()) ;
                    dontScanOrder(rootInActiveWindow) ; //不看这个订单
                    return;
                }

                int i = 0 ;

                for (AccessibilityNodeInfo info : infoDis) {
                    String disText = info.getText().toString() ;
                    Log.d(TAG , "订单获取到距离:" + disText) ;
                    String disFloatText = disText.substring(0 , disText.length()) ;
                    Log.d(TAG , "订单获取到距离2:" + disFloatText) ;

                    if (disFloatText.endsWith("km")) {
                        float realDis = Float.parseFloat(disFloatText.substring(0 , disFloatText.length() - 2));
                        Log.d(TAG , "真实距离:" + realDis) ;
                        if (realDis > minDis && realDis < maxDis) {
                            selectOrderMoney(rootInActiveWindow) ;
                            break;
                        } else {
                            dontScanOrder(rootInActiveWindow) ; //不看这个订单
                            break;
                        }

                    } else if (disFloatText.endsWith("m")) {
                        selectOrderMoney(rootInActiveWindow) ;
                        Log.d(TAG , "真实距离1:" + disFloatText) ;
                        break;
                    }

                    i++ ;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
            BaseServer.mInstance.onBackView();
        }
    }

    private void scanOrder(AccessibilityNodeInfo rootInActiveWindow , int i ) {
        try {
            List<AccessibilityNodeInfo> infoprices = rootInActiveWindow.findAccessibilityNodeInfosByViewId(main_scan_layout);
            if (infoprices.size() == 0) {
                Log.d(TAG , "主页查看layout长度为0") ;
                return;
            }

            if (infoprices.size() <= i) {
                Log.d(TAG , "主页查看layout长度不正确") ;
                return;
            }

            AccessibilityNodeInfo info = infoprices.get(i) ;
            info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectMainMoney(AccessibilityNodeInfo rootInActiveWindow) {
        try {
                List<AccessibilityNodeInfo> infoprices = rootInActiveWindow.findAccessibilityNodeInfosByViewId(main_money_number);
                if (infoprices.size() == 0) {
                    Log.d(TAG , "没有获取到价格") ;
                    return;
                }

                int[] setMoney = getPrice() ;

                int minMoney = setMoney[0] ;
                int maxMoney = setMoney[1] ;
                int i = 0 ;
                for (AccessibilityNodeInfo info : infoprices) {
                    String moneyText = infoprices.get(0).getText().toString() ;
                    Log.d(TAG , "主页获取到价格:" + moneyText) ;
                    String moneyFloatText = moneyText.substring(1 , moneyText.length() - 5) ;
                    Log.d(TAG , "主页获取到价格2:" + moneyFloatText) ;
                    float realMoney = Float.parseFloat(moneyFloatText) ;
                    if (realMoney >= minMoney && realMoney <= maxMoney) {
                        Log.e(TAG ,"查看订单") ;
                        scanOrder(rootInActiveWindow , i) ;
                        break;
                    }

                    i++ ;
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectMainDis(AccessibilityNodeInfo rootInActiveWindow) {
        try {
            //判断最小值有没有大于0
            int[] diss = getDis() ;
            int minDis = diss[0] ;
            int maxDis = diss[1] ;
            if (maxDis == 0) {
                selectMainMoney(rootInActiveWindow) ;
            } else {
                List<AccessibilityNodeInfo> infoDis = rootInActiveWindow.findAccessibilityNodeInfosByViewId(main_time_dis);
                if (infoDis.size() == 0) {
                    Log.d(TAG , "没有获取到距离" + infoDis.size()) ;
                    return;
                }

                int i = 0 ;

                for (AccessibilityNodeInfo info : infoDis) {
                    String disText = info.getText().toString() ;
                    Log.d(TAG , "主页获取到距离:" + disText) ;
                    String disFloatText = disText.substring(2 , disText.length()) ;
                    Log.d(TAG , "主页获取到距离2:" + disFloatText) ;

                    if (disFloatText.endsWith("km")) {
                        float realDis = Float.parseFloat(disFloatText.substring(0 , disFloatText.length() - 2));
                        Log.d(TAG , "真实距离:" + realDis) ;
                        if (realDis > minDis && realDis < maxDis) {
                            selectMainMoney(rootInActiveWindow) ;
                            break;
                        }

                    } else if (disFloatText.endsWith("m")) {
                        selectMainMoney(rootInActiveWindow) ;
                        Log.d(TAG , "真实距离1:" + disFloatText) ;
                        break;
                    }

                    i++ ;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkStart(AccessibilityNodeInfo rootInActiveWindow) {
        try {
            List<AccessibilityNodeInfo> infoprices = rootInActiveWindow.findAccessibilityNodeInfosByViewId(main_start_qiangdan);
            if (infoprices.size() == 0) {
                Log.d(TAG , "没有获取到开始按钮") ;
                selectMainDis(rootInActiveWindow) ;
                return;
            }

            AccessibilityNodeInfo info = infoprices.get(0) ;
            if (info.getText().equals("开始接单")) {
                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }

            selectMainDis(rootInActiveWindow) ;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent, AccessibilityNodeInfo rootInActiveWindow) {
        int type = accessibilityEvent.getEventType();
        switch (type){
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                List<CharSequence> texts = accessibilityEvent.getText();
                for (CharSequence cs:texts) {
                    Log.d(TAG,"TYPE_NOTIFICATION_STATE_CHANGED................................." + cs);
                }
                BaseServer.mInstance.openNotification(accessibilityEvent);
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String className = accessibilityEvent.getClassName().toString();
                Log.d(TAG,"TYPE_WINDOW_STATE_CHANGED................................." + className);

                if (className.equals("com.wuba.medusa.activity.PushActivity")) {
                    BaseServer.mInstance.onBackView(); //每次进到PushActivity 就返回
                } else if (className.equals("com.cxyw.suyun.ui.activity.FragmentBottomTab")) {
                    checkStart(rootInActiveWindow);
                } else if (className.equals("com.cxyw.suyun.ui.activity.PushOrderActivity")) {
                    //进入订单详情
                    selectOrderDis(rootInActiveWindow);
                }

                break;
        }
    }

    @Override
    public void onEvent(AccessibilityNodeInfo rootInActiveWindow) {
        selectMainDis(rootInActiveWindow);
    }

    @Override
    public void startActivity() {
        if (!isTopActivity(packages)) {
            PackageManager packageManager = ClientApplication.getInstance().getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(packages);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
            ClientApplication.getInstance().startActivity(intent);
        }else{
            Log.d(TAG , "startActivity") ;
            AccessibilityNodeInfo rootInActiveWindow = BaseServer.mInstance.getRootInActiveWindow();
            checkStart(rootInActiveWindow);
        }
    }

    @Override
    public boolean isTop() {
        return isTopActivity(packages);
    }

    @Override
    public String getPackageName() {
        return packages;
    }
}
