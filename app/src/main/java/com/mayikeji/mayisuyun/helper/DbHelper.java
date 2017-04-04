package com.mayikeji.mayisuyun.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/12/6.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static DbHelper mInstance;

    public static void initDb(Context context){
        if (mInstance == null)
            mInstance = new DbHelper(context);
    }
    public static DbHelper getInstance(){return mInstance;}

    public DbHelper(Context context) {
        //第三个参数CursorFactory指定在执行查询时获得一个游标实例的工厂类,设置为null,代表使用系统默认的工厂类
        super(context, "", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS record (time varchar(40), content varchar(40), price 10)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
