package com.call.log.infinity;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;

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

    public static int getThemeColorPrimary() {
        SharedPreferences sharedPreferences = context
            .getSharedPreferences(context.getString(R.string.theme), Context.MODE_PRIVATE);

        return sharedPreferences.getInt(context.getString(R.string.themePrimaryColor),
            ContextCompat.getColor(context, R.color.blue_primary));
    }

    public static int getThemeColorPrimaryDark() {
        SharedPreferences sharedPreferences = context
            .getSharedPreferences(context.getString(R.string.theme), Context.MODE_PRIVATE);

        return sharedPreferences.getInt(context.getString(R.string.themePrimaryDarkColor),
            ContextCompat.getColor(context, R.color.blue_primary_dark));

    }

    public static int getThemeColorPrimaryLight() {
        SharedPreferences sharedPreferences = context
            .getSharedPreferences(context.getString(R.string.theme), Context.MODE_PRIVATE);

        return sharedPreferences.getInt(context.getString(R.string.themePrimaryLightColor),
            ContextCompat.getColor(context, R.color.blue_primary_light));
    }

    public static int getThemeColorAccent() {
        SharedPreferences sharedPreferences = context
            .getSharedPreferences(context.getString(R.string.theme), Context.MODE_PRIVATE);

        return sharedPreferences.getInt(context.getString(R.string.themeAccentColor),
            ContextCompat.getColor(context, R.color.blue_accent));
    }

    public static Context getContext() {
        return context;
    }
}
