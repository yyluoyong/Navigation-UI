package com.navigation_ui.tools;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

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
public class CallerLocQuery {

    //归属地数据库名字。
    private static final String DB_NAME = "caller_loc_simple.db";
    //APP包名
    private static final String PACKAGE_NAME = MyApplication.getContext().getPackageName();
    //数据库路径
    private static final String DB_PATH = "/data" + Environment.getDataDirectory().
            getAbsolutePath() + "/" + PACKAGE_NAME;

    private static final String DB_NAME_ABS = DB_PATH + "/" + DB_NAME;

    //undo：这里只考虑了手机号码的归属地数据库，没有座机数据库。
    //数据库号码长度
    private static final int DB_PHONE_NUMBER_LEN = 7;

    private static SQLiteDatabase callerLocDB = null;

    /**
     * 查询指定号码的归属地。
     * @param phoneNumber: String
     * @return
     */
    public static String callerLocQuery(String phoneNumber) {

        SQLiteDatabase db = getDataBase();

        if (phoneNumber.length() < DB_PHONE_NUMBER_LEN) {
            return null;
        }

        String phoneNumberSub = phoneNumber.substring(0, DB_PHONE_NUMBER_LEN);
        String callerLoc = null;

        Cursor cursor = db.rawQuery("select location from caller_loc_simple " +
                "where phonenumber=?", new String[]{phoneNumberSub});

        if (cursor.moveToNext()) {
            callerLoc = cursor.getString(0);
        }

        return callerLoc;
    }

    /**
     * 查询指定号码的归属地。
     * @param phoneNumber: int
     * @return
     */
    public static String callerLocQuery(int phoneNumber) {
        return callerLocQuery(String.valueOf(phoneNumber));
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
