package com.navigation_ui.tools;

import android.app.Application;
import android.content.Context;

/**
 * Created by Yong on 2017/2/15.
 */

/**
 * 为了全局获取Context
 */
public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
