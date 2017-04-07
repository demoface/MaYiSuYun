package com.mayikeji.shoujibaidu.helper;

import android.util.Log;

import com.mayikeji.shoujibaidu.application.ClientApplication;
import com.mayikeji.shoujibaidu.bean.LoginBean;
import com.mayikeji.shoujibaidu.bean.OrderBean;
import com.mayikeji.shoujibaidu.bean.Result;
import com.mayikeji.shoujibaidu.bean.UpdateBean;
import com.mayikeji.shoujibaidu.utils.OkHttpUtils;
import com.mayikeji.shoujibaidu.utils.DesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.FormBody;

/**
 * Created by Administrator on 2016/12/11.
 */

public class HttpHelper {

    public static String BaseUrl = "http://120.77.180.233/" ;//http://mayi.utuiwu.com/index.php/api/app/record_log_new/content/

    private static HttpHelper mInstance = new HttpHelper();

    public static HttpHelper getInstance() {
        return mInstance;
    }

    private HttpHelper() {

    }

    //网络工具类
    private OkHttpUtils mOkHttpClient = OkHttpUtils.getInstance();

    public void login(final String username , final String password , final String imei , final CallBack<Result<LoginBean>> callBack) {
        UiHelper.getInstance().runOnThread(new Runnable() {
            @Override
            public void run() {

                try {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("username",username);
                        jsonObject.put("password",password);
                        jsonObject.put("imei",imei);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    final String result = OkHttpUtils.getInstance().post(BaseUrl+"index.php/api/app/login" , getBuilder(jsonObject.toString())) ;
                    Log.e("TAG",result);
                    final Result<LoginBean> bean = JsonHelper.getInstance().jsonToLogin(result) ;

                    UiHelper.getInstance().postThreadOnMain(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onResult(bean);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    UiHelper.getInstance().postThreadOnMain(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onResult(null);
                        }
                    });
                }
            }
        });
    }
    public void log(final String msg){
        //请求地址
        //http://mayi.utuiwu.com/index.php/api/app/record_log
        //提交方式：post
        //参数：
        //content=你要提交的东西（你怎么提交我怎么保存，不要加密）

        UiHelper.getInstance().runOnThread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpUtils.getInstance().get(BaseUrl+"index.php/api/app/record_log_new/content/"+ ClientApplication.sp.getString("username","")+ "   "+ msg) ;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }



    public void update(final String version ,  final CallBack<Result<UpdateBean>> callBack) {
        UiHelper.getInstance().runOnThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("version",version);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    final String result = OkHttpUtils.getInstance().post(BaseUrl+"index.php/api/app/update" , getBuilder(jsonObject.toString())) ;

                    final Result<UpdateBean> bean = JsonHelper.getInstance().jsonToUpdate(result) ;

                    UiHelper.getInstance().postThreadOnMain(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onResult(bean);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    UiHelper.getInstance().postThreadOnMain(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onResult(null);
                        }
                    });
                }
            }
        });
    }

    public void commit(final String price ,final String source ,final String uid ,  final CallBack<Result<String>> callBack) {
        UiHelper.getInstance().runOnThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("price",price);
                        jsonObject.put("source",source);
                        jsonObject.put("uid",uid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    final String result = OkHttpUtils.getInstance().post(BaseUrl+"index.php/api/app/uporder" , getBuilder(jsonObject.toString())) ;

                    final Result<String> bean = JsonHelper.getInstance().jsonToBase(result) ;

                    UiHelper.getInstance().postThreadOnMain(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onResult(bean);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    UiHelper.getInstance().postThreadOnMain(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onResult(null);
                        }
                    });
                }
            }
        });
    }

    public void getOrderList(final String p ,final String uid ,  final CallBack<Result<ArrayList<OrderBean>>> callBack) {
        UiHelper.getInstance().runOnThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("p",p);
                        jsonObject.put("uid",uid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    final String result = OkHttpUtils.getInstance().post(BaseUrl+"index.php/api/app/get_order" , getBuilder(jsonObject.toString())) ;

                    final Result<ArrayList<OrderBean>> bean = JsonHelper.getInstance().jsonToOrderList(result) ;

                    UiHelper.getInstance().postThreadOnMain(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onResult(bean);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    UiHelper.getInstance().postThreadOnMain(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onResult(null);
                        }
                    });
                }
            }
        });
    }

    private FormBody.Builder getBuilder(String jsonObject) {
        String encrypt = DesUtils.encrypt(jsonObject.toString());
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("data" , encrypt) ;
        return builder;
    }

    public interface CallBack<T> {
        void onResult(T result);
    }
}
