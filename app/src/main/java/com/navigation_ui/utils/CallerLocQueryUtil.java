package com.navigation_ui.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.text.TextUtils;

import com.navigation_ui.MyApplication;
import com.navigation_ui.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Yong on 2017/2/5.
 */

/**
 * 用于查询电话号码的归属地。
 */
public class CallerLocQueryUtil {
    static final String TAG = "CallerLocQuery";

    //归属地数据库名字。
    private static final String DB_NAME = MyApplication.getContext()
        .getString(R.string.CallerLocationDatabaseName);
    //APP包名
    private static final String PACKAGE_NAME = MyApplication.getContext().getPackageName();
    //数据库路径
    private static final String DB_PATH = "/data" + Environment.getDataDirectory().
            getAbsolutePath() + "/" + PACKAGE_NAME;

    private static final String DB_NAME_ABS = DB_PATH + "/" + DB_NAME;

    //手机号码归属地数据库的表名
    private static final String CELLPHONE_AREA_DB_TABLE = MyApplication.getContext()
        .getString(R.string.CallerLocationDatabaseTable);
    //手机号码归属地数据库表的列名：手机号前缀
    private static final String CELLPHONE_AREA_DB_TABLE_COLUMN_NUMBER = MyApplication.getContext()
        .getString(R.string.CallerLocationDatabaseTableColumnNumber);
    //手机号码归属地数据库表的列名：归属地
    private static final String CELLPHONE_AREA_DB_TABLE_COLUMN_AREA = MyApplication.getContext()
        .getString(R.string.CallerLocationDatabaseTableColumnArea);
    //手机号码归属地数据库表的列名：运营商
    private static final String CELLPHONE_AREA_DB_TABLE_COLUMN_OPERATOR = MyApplication.getContext()
        .getString(R.string.CallerLocationDatabaseTableColumnOperator);

    //手机号码数据库号码长度
    private static final int DB_CELLPHONE_NUMBER_LEN = 7;

    private static final String UNKOWN_OPERATOR = MyApplication.getContext()
        .getString(R.string.UnkownOperator);

    private static final SQLiteDatabase CELLPHONE_AREA_DB = getDataBase();

    /**
     * 查询指定号码的归属地。
     * @param phoneNumber: int
     * @return
     */
    public static String[] callerLocQuery(int phoneNumber) {
        return callerLocQuery(String.valueOf(phoneNumber));
    }

    /**
     * 查询指定号码的归属地。
     * @param phoneNumber: String
     * @return
     */
    public static String[] callerLocQuery(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return null;
        }

        //手机号码
        if (phoneNumber.startsWith("1")) {
            return cellPhoneNumberAreaQuery(phoneNumber);
        } else { //座机号码
            return new String[]{TelephoneAreaCodeUtil
                .getTelephoneAreaByPhoneNumber(phoneNumber), UNKOWN_OPERATOR};
        }
    }

    /**
     * 查询手机号码归属地和运营商。
     * @param phoneNumber: int
     * @return
     */
    public static String[] cellPhoneNumberAreaQuery(int phoneNumber) {
        return cellPhoneNumberAreaQuery(String.valueOf(phoneNumber));
    }

    /**
     * 查询手机号码归属地和运营商。
     * @param phoneNumber: String
     */
    public static String[] cellPhoneNumberAreaQuery(String phoneNumber) {
        if (phoneNumber.length() < DB_CELLPHONE_NUMBER_LEN) {
            return new String[]{null, UNKOWN_OPERATOR};
        }

        String phoneNumberSub = phoneNumber.substring(0, DB_CELLPHONE_NUMBER_LEN);
        String callerLoc = null;
        String operator = UNKOWN_OPERATOR;

        Cursor cursor = CELLPHONE_AREA_DB.rawQuery("select " + CELLPHONE_AREA_DB_TABLE_COLUMN_AREA
            + ", " + CELLPHONE_AREA_DB_TABLE_COLUMN_OPERATOR
            + " from " + CELLPHONE_AREA_DB_TABLE + " where "
            + CELLPHONE_AREA_DB_TABLE_COLUMN_NUMBER + "=? limit 1", new String[]{phoneNumberSub});

        if (cursor.moveToNext()) {
            callerLoc = cursor.getString(0);
            operator = cursor.getString(1);
        }

        return new String[]{callerLoc, operator};
    }


    /**
     * 获得归属地SQLiteDatabase对象。如果数据库不存在，就从APP资源中提取，写到存储中。
     * @return
     */
    private static SQLiteDatabase getDataBase() {

        try {
            if ( !(new File(DB_NAME_ABS).exists()) ) {

                File dirFile = new File(DB_PATH);

                if (!dirFile.exists()) {
                    dirFile.mkdirs();
                }

                InputStream is = MyApplication.getContext().getAssets().open(DB_NAME);
                OutputStream os = new FileOutputStream(DB_NAME_ABS);

                // 文件写入
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }

                os.flush();
                os.close();
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_NAME_ABS, null,
            SQLiteDatabase.OPEN_READONLY);

        return db;
    }
}
