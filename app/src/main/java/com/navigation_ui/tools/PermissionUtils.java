package com.navigation_ui.tools;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yong on 2017/2/25.
 */

public class PermissionUtils {

    private final static String TAG = "PermissionUtils";

    public final static int REQUEST_CODE = 1;

//    private static int mRequestCode = -1;

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
    public static void requestPermissions(Context context, int requestCode
        , String[] permissions, OnPermissionListener listener) {

        if (context instanceof Activity) {

            mOnPermissionListener = listener;
            ActivityCompat.requestPermissions((Activity) context, permissions, requestCode);

//            List<String> deniedPermissions = getDeniedPermissions(context, permissions);
//
//            int res = PermissionChecker.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
//
//            LogUtil.d(TAG, "权限检查返回值" + ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE));
//
//            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
//                != PackageManager.PERMISSION_GRANTED) {
//
////            if (deniedPermissions != null && deniedPermissions.size() > 0) {
////                mRequestCode = requestCode;
//
//                ActivityCompat.requestPermissions((Activity) context, deniedPermissions
//                    .toArray(new String[deniedPermissions.size()]), requestCode);
//            } else {
//
//                LogUtil.d(TAG, "requestPermissions  ---> 已获取");
//
//                if (mOnPermissionListener != null) {
//                    mOnPermissionListener.onPermissionGranted();
//                }
//            }
        } else {
            throw new RuntimeException("Context must be an Activity");
        }
    }



    /**
     * 请求权限结果，对应Activity中onRequestPermissionsResult()方法。
     */
    public static void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                  int[] grantResults) {
        if (mOnPermissionListener == null) {
            return;
        }

        LogUtil.d(TAG, "onRequestPermissionsResult " + grantResults);

        switch (requestCode) {
            case REQUEST_CODE:
                if (verifyPermissions(grantResults)) {
                    //回调申请权限成功的处理办法
                    mOnPermissionListener.onPermissionGranted();
                    LogUtil.d(TAG, "权限申请成功!");
                } else {
                    //回调申请权限失败的处理办法
                    mOnPermissionListener.onPermissionDenied();
                    LogUtil.d("CallLogRecyclerViewAdapter", "权限申请失败!");
                }
                break;
            default:
        }
    }

    /**
     * 获取请求权限中需要授权的权限
     */
    private static List<String> getDeniedPermissions(Context context, String... permissions) {

        //Android 6.0之前无需动态申请权限
        if (Build.VERSION.SDK_INT < 23) {
            return null;
        }

        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permission);
            }
        }
        return deniedPermissions;
    }

    /**
     * 验证所有权限是否都已经授权
     */
    private static boolean verifyPermissions(int[] grantResults) {

        //Android 6.0之前无需动态申请权限
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }

        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}