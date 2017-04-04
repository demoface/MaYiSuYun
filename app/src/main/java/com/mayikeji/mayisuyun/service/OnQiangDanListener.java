package com.mayikeji.mayisuyun.service;

import android.view.accessibility.AccessibilityEvent;

/**
 * author : solon
 * date: on 16/12/14.
 */

public interface OnQiangDanListener {
    void onBackView() ;
    void openNotification(AccessibilityEvent event) ;
    void onGetOrder() ;
}
