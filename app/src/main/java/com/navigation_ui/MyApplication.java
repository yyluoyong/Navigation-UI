package com.navigation_ui;

import android.app.Application;
import android.content.Context;

import com.navigation_ui.database.CallLogDatabase;
import com.navigation_ui.database.CopyDatabaseToSDCardUtil;
import com.navigation_ui.utils.LogUtil;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by Yong on 2017/2/15.
 */

/**
 * 为了全局获取Context
 */
public class MyApplication extends Application {
    static final String TAG = "MyApplication";

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        /**
         * 对DBFlow进行初始化
         */
        FlowManager.init(new FlowConfig.Builder(this).build());
    }

    public static Context getContext() {
        return context;
    }
}
