package com.mayikeji.mayisuyun.helper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mayikeji.mayisuyun.bean.LoginBean;
import com.mayikeji.mayisuyun.bean.OrderBean;
import com.mayikeji.mayisuyun.bean.Result;
import com.mayikeji.mayisuyun.bean.UpdateBean;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * author : solon
 * date: on 16/12/15.
 */

public class JsonHelper {

    private static JsonHelper mInstance =new JsonHelper();
    public static JsonHelper getInstance(){
        return  mInstance;
    }
    private JsonHelper(){};

    public static Gson gson;

    static {
        gson = new Gson();
    }

    /**
     * json字符串转单个简单对象
     *
     * @param jsonStr
     * @param t       Order.class
     * @return
     * @throws Exception
     */
    public static <T> T getObject(String jsonStr, Class<T> t) throws Exception {
        return gson.fromJson(jsonStr, t);
    }

    /**
     * json字符串转单个复杂对象
     *
     * @param jsonStr
     * @param type    new TypeToken<Result<Order>>() { }.getType()
     * @return
     * @throws Exception
     */
    public static <T> T getObject(String jsonStr, Type type) {
        return gson.fromJson(jsonStr, type);
    }

    public static String toString(Object t) {
        return gson.toJson(t);
    }

    public Result<String> jsonToBase(String json) {
        Result<String> result = null;
        try {
            result = JsonHelper.getObject(json, new TypeToken<Result<String>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return result;
        }
    }

    public Result<LoginBean> jsonToLogin(String json) {
        Result<LoginBean> result = null;
        try {
            result = JsonHelper.getObject(json, new TypeToken<Result<LoginBean>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return result;
        }
    }

    public Result<UpdateBean> jsonToUpdate(String json) {
        Result<UpdateBean> result = null;
        try {
            result = JsonHelper.getObject(json, new TypeToken<Result<UpdateBean>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return result;
        }
    }

    public Result<ArrayList<OrderBean>> jsonToOrderList(String json) {
        Result<ArrayList<OrderBean>> result = null;
        try {
            result = JsonHelper.getObject(json, new TypeToken<Result<ArrayList<OrderBean>>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return result;
        }
    }
}
