package com.mayikeji.mayisuyun.utils;


import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * author : solon
 * date: on 16/aa/3.
 */

public class OkHttpUtils {

    OkHttpClient client = new OkHttpClient();

    public static final MediaType JSON
            = MediaType.parse("application/json;charset=UTF-8");//application/json;charset=UTF-8

    private static OkHttpUtils mInstance = new OkHttpUtils();

    public static OkHttpUtils getInstance() {
        return mInstance;
    }

    private OkHttpUtils() {
    }

    public String get(String url, Map map) {
        int i = 0;
        for (Object o : map.keySet()) {
            String s;
            if (i == 0)
                s = "?";
            else
                s = "&";
            i = 1;
            url += s + o.toString() + "=" + map.get(o).toString();
        }
        url.replace(" ", "%20");

        return get(url);
    }

    /**
     * get 请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public String get(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

    }

    /**
     * post请求
     *
     * @param url
     * @param json
     * @return
     * @throws IOException
     */
    public String post(String url, String json) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

    }
    public static String sendPost(String url, String data) {
        Log.e("URL",url);
        PrintWriter out = null;
        BufferedReader in = null;

        String result = null;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数

            out.print(data);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }
    /**
     * post 表单提交
     *
     * @param url
     * @param builder
     * @return
     */
    public String post(String url, FormBody.Builder builder) {
        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

    }
}
