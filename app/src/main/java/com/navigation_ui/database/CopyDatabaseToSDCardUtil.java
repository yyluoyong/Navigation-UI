package com.navigation_ui.database;

/**
 * Created by Yong on 2017/3/6.
 */

import android.os.Environment;

import com.navigation_ui.MyApplication;
import com.navigation_ui.R;
import com.navigation_ui.utils.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 将指定数据库复制到SDCard中。
 */
public final class CopyDatabaseToSDCardUtil {
    static final String TAG = "CopyDatabaseToSDCardUtil";

    private static final String DATE_STRING = MyApplication.getContext()
        .getString(R.string.yearMonthDayHourMinuteSecond);

    private static final String SEPARATOR = "/";

    public static boolean copyDatabaseToSDCard(String databasePath) {

        boolean isSuccess = false;

        try {
            File databaseFile = new File(databasePath);

            if (databaseFile.exists()) {
                InputStream is = new FileInputStream(databasePath);

                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_STRING);

                String outputDirPath = Environment.getExternalStorageDirectory().getCanonicalPath() + SEPARATOR
                    + MyApplication.getContext().getPackageName();

                File outputDir = new File(outputDirPath);
                if (!outputDir.exists()) {
                    outputDir.mkdirs();
                }

                String outputPath = outputDirPath + SEPARATOR
                    + dateFormat.format(new Date(System.currentTimeMillis())) + "_"
                    + databaseFile.getName();

                OutputStream os = new FileOutputStream(outputPath);

                // 文件写入
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }

                os.flush();
                os.close();
                is.close();

                isSuccess = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return isSuccess;
        }
    }
}
