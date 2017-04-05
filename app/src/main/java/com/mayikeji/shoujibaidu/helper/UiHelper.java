package com.mayikeji.shoujibaidu.helper;

import android.content.Context;
import android.os.Handler;
import android.util.TypedValue;
import android.widget.Toast;

import com.mayikeji.shoujibaidu.application.ClientApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 豪杰 on 2016/11/29.
 */
public class UiHelper {

    private Handler handler = new Handler();
    private static UiHelper mInstance = new UiHelper();
    public static UiHelper getInstance(){
        return mInstance;
    }
    private UiHelper(){}
    public void postThreadOnMain(Runnable runnable,int d){
        handler.postDelayed(runnable,d);
    }
    public void postThreadOnMain(Runnable runnable){
        handler.postDelayed(runnable,0);
    }
    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    public void runOnThread(Runnable runnable){
        cachedThreadPool.execute(runnable);
    }
    public float dpToPx(int dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,getContext().getResources().getDisplayMetrics());
    }
    public Context getContext(){
        return ClientApplication.getInstance();
    }


    public static void showToast(final Context context , final String string) {
        UiHelper.getInstance().postThreadOnMain(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context , string , Toast.LENGTH_SHORT).show();
            }
        }) ;
    }
}