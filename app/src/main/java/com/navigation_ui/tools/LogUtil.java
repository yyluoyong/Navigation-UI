package com.navigation_ui.tools;

import android.util.Log;
/**
 * Created by Yong on 2017/1/31.
 */

/**
 * 定制日志工具类，控制日志输出级别。
 */
public class LogUtil {

    public static final short VERBOSE = 1;

    public static final short DEBUG = 2;

    public static final short INFO = 3;

    public static final short WARN = 4;

    public static final short ERROR = 5;

    public static final short NOTHING = 6;

    public static short level = VERBOSE; //调节日志级别

    public static void v(String tag, String msg) {
        if (level <= VERBOSE) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (level <= VERBOSE) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (level <= VERBOSE) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (level <= VERBOSE) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (level <= VERBOSE) {
            Log.e(tag, msg);
        }
    }

}
