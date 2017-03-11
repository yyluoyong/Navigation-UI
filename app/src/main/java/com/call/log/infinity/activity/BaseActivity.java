package com.call.log.infinity.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.call.log.infinity.utils.PermissionUtil;

/**
 * Created by Yong on 2017/3/11.
 */

public class BaseActivity extends AppCompatActivity {

    /**
     * 权限请求的回调方法。
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        //使用PermissionUtils处理动态权限申请
        PermissionUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
