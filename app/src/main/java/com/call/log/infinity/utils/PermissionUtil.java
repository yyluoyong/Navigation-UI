package com.call.log.infinity.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

/**
 * Created by Yong on 2017/2/25.
 */

/**
 * 动态权限申请工具类。注意：该类不是线程安全的。
 */
public class PermissionUtil {

    /**
     * 说明一：
     * 在H2OS 2.0(Android 6.0)中
     * ContextCompat.checkSelfPermission始终返回PackageManager.PERMISSION_GRANTED
     */
    private final static String TAG = "PermissionUtils";

    public final static int REQUEST_CODE = 1;

    private static OnPermissionListener mOnPermissionListener;

    /**
     * 申请权限后可以执行的回调方法
     */
    public interface OnPermissionListener {
        //申请权限成功后进行的操作
        void onPermissionGranted();
        //申请权限失败后进行的操作
        void onPermissionDenied();
    }

    /**
     * 在该方法中调用ActivityCompat.requestPermissions，
     * 会回调context中实现的onRequestPermissionsResult方法。
     * @param context：Activity
     * @param requestCode
     * @param permissions
     * @param listener
     */
    public synchronized static void requestPermissions(@NonNull Context context, int requestCode,
        @NonNull String[] permissions, @NonNull OnPermissionListener listener) {

        //listener 不能为空，否则会出现不同的权限处理回调同一个listener的问题
        if (listener == null) {
            throw new RuntimeException("OnPermissionListener must not be null");
        }

        mOnPermissionListener = listener;

        //Android 6.0之前无需动态申请权限，直接执行
        if (Build.VERSION.SDK_INT < 23) {
            mOnPermissionListener.onPermissionGranted();
            return;
        }

        if (context instanceof Activity) {
            ActivityCompat.requestPermissions((Activity) context, permissions, requestCode);
        } else {
            throw new RuntimeException("Context must be an Activity");
        }
    }

     /**
     * 在目标Activity中onRequestPermissionsResult()方法中调用该静态方法，
     * 实现对请求结果的回调处理。
     */
    public synchronized static void onRequestPermissionsResult(int requestCode,
        @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (mOnPermissionListener == null) {
            return;
        }

        /**
         * 在“说明一”情形下，失败处理不会被执行。
         */
        switch (requestCode) {
            case REQUEST_CODE:
                if (verifyPermissions(grantResults)) {
                    //回调申请权限成功的处理办法
                    mOnPermissionListener.onPermissionGranted();
                } else {
                    //回调申请权限失败的处理办法
                    mOnPermissionListener.onPermissionDenied();
                }
                break;
            default:
        }
    }

    /**
     * 在“说明一”情形下失效
     * 验证所有权限是否都已经授权
     */
    private static boolean verifyPermissions(int[] grantResults) {
        //Android 6.0之前无需动态申请权限
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }

        if (grantResults.length <= 0) {
            return false;
        }

        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }
}