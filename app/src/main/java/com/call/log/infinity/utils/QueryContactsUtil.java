package com.call.log.infinity.utils;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.widget.Toast;
import com.call.log.infinity.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Yong on 2017/3/11.
 */

/**
 * 查询系统中联系人和电话号码的工具。
 */
public class QueryContactsUtil {
    static final String TAG = "QueryContactsUtil";

    //电话号码的分隔符，安卓系统中得到的电话号码数字中间会按规律加入空格，以方便阅读。
    private static final String PHONE_NUMBER_DELIMITER = " ";

    /**
     * 查询指定联系人姓名对应号码列表的回调接口。
     */
    public interface OnQueryContactsPhoneNumberListener {
        //申请权限成功后进行的操作
        void onQuerySuccess(ArrayList<String> phoneNumberList);
        //申请权限失败后进行的操作
        void onQueryFailed();
    }

    /**
     * 查询电话号码和联系人姓名Map的回调接口。
     */
    public interface OnQueryPhoneNumberAndContactsNameMapListener {
        //申请权限成功后进行的操作
        void onQuerySuccess(HashMap<String, String> phoneNumberAndContactsName);
        //申请权限失败后进行的操作
        void onQueryFailed();
    }

    /**
     * 查询指定号码对应联系人姓名的回调接口。
     */
    public interface OnQueryPhoneNumberBelongToListener {
        //申请权限成功后进行的操作
        void onQuerySuccess(String contactsName);
        //申请权限失败后进行的操作
        void onQueryFailed();
    }

    /**
     * 获得指定号码对应的联系人姓名，并在完成之后执行回调方法。
     * @param mContext
     * @param phoneNumber
     * @param listener
     */
    public static void queryPhoneNumberBelongTo(@NonNull final Context mContext,
        @NonNull final String phoneNumber, @NonNull final OnQueryPhoneNumberBelongToListener listener) {

        LogUtil.d(TAG, "queryPhoneNumberBelongTo " + phoneNumber);

        PermissionUtil.requestPermissions(mContext, PermissionUtil.REQUEST_CODE,
            new String[]{Manifest.permission.READ_CONTACTS},
            new PermissionUtil.OnPermissionListener() {
                @Override
                public void onPermissionGranted() {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Cursor cursor = null;
                            String contactsName = null;
                            try {
                                LogUtil.d(TAG, "onPermissionGranted " + phoneNumber);
                                cursor = mContext.getContentResolver()
                                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                        new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                                        ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
                                        new String[]{phoneNumber}, null);

                                if (cursor != null) {
                                    if (cursor.moveToFirst()) {
                                        contactsName = cursor.getString(cursor
                                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                if (cursor != null) {
                                    cursor.close();
                                }
                            }

                            LogUtil.d(TAG, "onPermissionGranted " + phoneNumber + " " + contactsName);

                            if (listener != null) {
                                listener.onQuerySuccess(contactsName);
                            }


                        }
                    }).start();

                }

                /**
                 * 见PermissionUtils类的“说明一”
                 */
                @Override
                public void onPermissionDenied() {
                    Toast.makeText(mContext, mContext.getString(R.string.refusePermissionMessage),
                        Toast.LENGTH_SHORT).show();
                }
            }
        );
    }

    /**
     * 获得指定联系人的电话号码列表，并在完成之后执行回调方法。
     * @param mContext
     * @param contactsName
     * @param listener
     */
    public static void queryContactsPhoneNumber(@NonNull final Context mContext,
        @NonNull final String contactsName, @NonNull final OnQueryContactsPhoneNumberListener listener) {

        final ArrayList<String> phoneNumberList = new ArrayList<>();

        PermissionUtil.requestPermissions(mContext, PermissionUtil.REQUEST_CODE,
            new String[]{Manifest.permission.READ_CONTACTS},
            new PermissionUtil.OnPermissionListener() {
                @Override
                public void onPermissionGranted() {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Cursor cursor = null;
                            try {
                                cursor = mContext.getContentResolver()
                                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                        null,
                                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?",
                                        new String[]{contactsName}, null);

                                if (cursor != null) {
                                    while (cursor.moveToNext()) {
                                        String phoneNumber = cursor.getString(cursor
                                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                        //去掉空格
                                        phoneNumberList.add(phoneNumber.replace(PHONE_NUMBER_DELIMITER, ""));
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                if (cursor != null) {
                                    cursor.close();
                                }
                            }

                            if (listener != null) {
                                listener.onQuerySuccess(phoneNumberList);
                            }
                        }
                    }).start();
                }

                /**
                 * 见PermissionUtils类的“说明一”
                 */
                @Override
                public void onPermissionDenied() {
                    Toast.makeText(mContext, mContext.getString(R.string.refusePermissionMessage),
                        Toast.LENGTH_SHORT).show();
                }
            }
        );
    }

    /**
     * 获得所有号码和联系人姓名对应关系的Map，并在完成之后执行回调。
     * @param mContext
     * @param listener
     */
    public static void queryPhoneNumberAndContactsNameMap(@NonNull final Context mContext,
        @NonNull final OnQueryPhoneNumberAndContactsNameMapListener listener) {

        final HashMap<String, String> phoneNumberAndContactsName = new HashMap<>();

        PermissionUtil.requestPermissions(mContext, PermissionUtil.REQUEST_CODE,
            new String[]{Manifest.permission.READ_CONTACTS},
            new PermissionUtil.OnPermissionListener() {
                @Override
                public void onPermissionGranted() {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Cursor cursor = null;
                            try {
                                cursor = mContext.getContentResolver()
                                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                        null, null, null, null);
                                if (cursor != null) {
                                    while (cursor.moveToNext()) {
                                        String name = cursor.getString(cursor.getColumnIndex(
                                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                                        String phoneNumber = cursor.getString(cursor.getColumnIndex(
                                            ContactsContract.CommonDataKinds.Phone.NUMBER));

                                        phoneNumberAndContactsName.put(phoneNumber.replace(PHONE_NUMBER_DELIMITER, ""),
                                            name);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                if (cursor != null) {
                                    cursor.close();
                                }
                            }

                            if (listener != null) {
                                listener.onQuerySuccess(phoneNumberAndContactsName);
                            }
                        }
                    }).start();
                }

                /**
                 * 见PermissionUtils类的“说明一”
                 */
                @Override
                public void onPermissionDenied() {
                    Toast.makeText(mContext, mContext.getString(R.string.refusePermissionMessage),
                        Toast.LENGTH_SHORT).show();
                }
            }
        );
    }

}
