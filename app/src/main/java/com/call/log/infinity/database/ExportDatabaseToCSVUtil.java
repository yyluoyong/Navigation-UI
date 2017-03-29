package com.call.log.infinity.database;

import android.os.Environment;
import android.provider.CallLog;
import android.text.TextUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import com.call.log.infinity.MyApplication;
import com.call.log.infinity.R;
import com.call.log.infinity.utils.CallDurationFormatter;
import com.call.log.infinity.utils.CallerLocationAndOperatorQueryUtil;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;

/**
 * Created by Yong on 2017/3/28.
 */

public class ExportDatabaseToCSVUtil {

    private static final String ALL_DATE_INFORMATION = MyApplication.getContext()
        .getString(R.string.allDateInformation);
    private static final String DATE_STRING = MyApplication.getContext()
        .getString(R.string.yearMonthDayHourMinuteSecond);


    private static final String CSV_FILE_TITLE = MyApplication.getContext().getString(R.string.csvFileTitle);
    private static final String CSV_LINE_FORMAT = MyApplication.getContext().getString(R.string.csvLineFormat);
    private static final String MADE_CALL = MyApplication.getContext().getString(R.string.searchCallTypeOut);
    private static final String RECIEVED_CALL = MyApplication.getContext().getString(R.string.searchCallTypeIn);
    private static final String MISSED_CALL = MyApplication.getContext().getString(R.string.searchCallTypeMiss);

    private static final String SEPARATOR = "/";

    /**
     * 将数据库输出到csv文件。
     * @return
     */
    public static final boolean exportDatabaseToCSV() {
        boolean isSuccess = false;

        try {
            String outputDirPath = Environment.getExternalStorageDirectory().getCanonicalPath() + SEPARATOR
                + MyApplication.getContext().getPackageName();

            File outputDir = new File(outputDirPath);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_STRING);
            String outputPath = outputDirPath + SEPARATOR
                + dateFormat.format(new Date(System.currentTimeMillis())) + ".csv";

            List<CallLogModelDBFlow> modelList = SQLite.select().from(CallLogModelDBFlow.class)
                .orderBy(CallLogModelDBFlow_Table.dateInMilliseconds, false).queryList();

            isSuccess = exportDatabaseToCSV(outputPath, modelList);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return isSuccess;
        }
    }

    /**
     * 输出数据库模型列表到指定的文件。
     * @param filePath
     * @param modelList
     * @return
     */
    private static boolean exportDatabaseToCSV(String filePath, List<CallLogModelDBFlow> modelList) {

        boolean isSuccess = false;

        try {
            File csvFile = new File(filePath);

            if (!csvFile.exists()) {
                csvFile.createNewFile();
            }

            FileOutputStream outputStream = new FileOutputStream(csvFile);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

            bufferedWriter.write(CSV_FILE_TITLE);
            bufferedWriter.newLine();

            for (CallLogModelDBFlow model : modelList) {
                SimpleDateFormat dateFormat = new SimpleDateFormat(ALL_DATE_INFORMATION);
                String dateString = dateFormat.format(new Date(model.getDateInMilliseconds()));

                String durationString = CallDurationFormatter.format(model.getDuration());

                String callType;
                if (model.getCallType() == CallLog.Calls.OUTGOING_TYPE) {
                    callType = MADE_CALL;
                } else if (model.getCallType() == CallLog.Calls.MISSED_TYPE ||
                    (model.getDuration() == 0 && model.getCallType() == CallLog.Calls.INCOMING_TYPE)) {
                    callType = MISSED_CALL;
                } else {
                    callType = RECIEVED_CALL;
                }

                String[] areaAndOperator = CallerLocationAndOperatorQueryUtil
                    .callerLocationAndOperatorQuery(model.getPhoneNumber());
                String callerLoc = areaAndOperator[0];
                String operator = areaAndOperator[1];

                if (TextUtils.isEmpty(callerLoc)) {
                    callerLoc = CallerLocationAndOperatorQueryUtil.UNKOWN_AREA;
                    operator = CallerLocationAndOperatorQueryUtil.UNKOWN_OPERATOR;
                }

                String contentLine = String.format(CSV_LINE_FORMAT, model.getContactsName(), model.getContactsName(),
                    dateString, durationString, callType, callerLoc, operator);

                bufferedWriter.write(contentLine);
                bufferedWriter.newLine();
            }

            bufferedWriter.close();

            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return isSuccess;
        }
    }
}
