package com.call.log.infinity.database;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.widget.Toast;
import com.call.log.infinity.R;
import com.call.log.infinity.utils.LogUtil;
import com.call.log.infinity.utils.PermissionUtil;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Yong on 2017/3/11.
 */

public class GetContactsNameUtil {
    static final String TAG = "GetContactsNameUtil";

    private static HashMap<String, String> phoneNumberAndContactsName = new HashMap<>();

    /**
     * 处理查询结果的回调
     */
    public interface OnQueryListener {
        //申请权限成功后进行的操作
        void onQuerySuccess();
        //申请权限失败后进行的操作
        void onQueryFailed();
    }

    public interface OnQueryContactsPhoneNumberListener {
        //申请权限成功后进行的操作
        void onQuerySuccess(ArrayList<String> phoneNumberList);
        //申请权限失败后进行的操作
        void onQueryFailed();
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
                                        phoneNumberList.add(phoneNumber.replace(" ", ""));

                                        LogUtil.d(TAG, phoneNumber);
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
//                            if (mContext != null || mContext instanceof Activity) {
//                                ((Activity) mContext).runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if (listener != null) {
//                                            listener.onQuerySuccess(phoneNumberList);
//                                        }
//                                    }
//                                });
//                            }
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

    public static String queryContactsNameToPhoneNumber(@NonNull final Context mContext,
        @NonNull final String phoneNumber) {

        final ArrayList<String> contactsNameList = new ArrayList<>();
        contactsNameList.add(0, null);

        PermissionUtil.requestPermissions(mContext, PermissionUtil.REQUEST_CODE,
            new String[]{Manifest.permission.READ_CONTACTS},
            new PermissionUtil.OnPermissionListener() {
                @Override
                public void onPermissionGranted() {
                    LogUtil.d(TAG, "onPermissionGranted");
                    Cursor cursor = null;
                    try {
                        cursor = mContext.getContentResolver()
                            .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
                                new String[] {phoneNumber}, null);

                        if (cursor.moveToFirst()) {
                            String name = cursor.getString(cursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            contactsNameList.add(0, name);

                            LogUtil.d(TAG, "onPermissionGranted " + name);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }

                /**
                 * 见PermissionUtils类的“说明一”
                 */
                @Override
                public void onPermissionDenied() {
                    Toast.makeText(mContext, mContext.getString(R.string.refusePermissionMessage),
                        Toast.LENGTH_SHORT).show();
                }
            });

        LogUtil.d(TAG, phoneNumber + " " + contactsNameList.get(0));

        return contactsNameList.get(0);
    }

    public static void getPhoneNumberAndContactsName(@NonNull final Context mContext,
        @NonNull final OnQueryListener listener) {

        phoneNumberAndContactsName.clear();

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

                                        LogUtil.d(TAG, name + " " + phoneNumber);
                                        phoneNumberAndContactsName.put(phoneNumber, name);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                if (cursor != null) {
                                    cursor.close();
                                }
                            }

                            if (mContext != null || mContext instanceof Activity) {
                                ((Activity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (listener != null) {
                                            listener.onQuerySuccess();
                                        }
                                        LogUtil.d(TAG, "UI: phoneNumberAndContactsName.size() = "
                                            + phoneNumberAndContactsName.size());
                                    }
                                });
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
//
//        for (HashMap.Entry<String, String> entry: phoneNumberAndContactsName.entrySet()) {
//            LogUtil.d(TAG, entry.getKey() + " " + entry.getValue());
//        }
//
//        LogUtil.d(TAG, "phoneNumberAndContactsName.size() = " + phoneNumberAndContactsName.size());
    }

}
