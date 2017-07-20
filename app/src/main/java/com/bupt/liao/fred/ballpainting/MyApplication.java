package com.bupt.liao.fred.ballpainting;

import android.app.Application;
import android.content.Context;

/**
 * Created by fred on 2017/7/18.
 */

public class MyApplication extends Application {
    private static Context context;
    @Override
    public void onCreate(){
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getAppContext(){
        return context;
    }
}
