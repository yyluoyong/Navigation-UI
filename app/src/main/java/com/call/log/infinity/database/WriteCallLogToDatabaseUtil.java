package com.call.log.infinity.database;

import android.database.Cursor;
import android.provider.CallLog;
import android.text.TextUtils;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Yong on 2017/2/1.
 */

/**
 * 输入系统通话记录查询Cursor
 * 返回新增通话记录条目的数量Integer
 */
public class WriteCallLogToDatabaseUtil {
    static final String TAG = "WriteCallLogToDatabaseTool";

    private long dateInMilliseconds; //通话发生时间
    private String contactsName;     //联系人名字
    private String phoneNumber;      //电话号码
    private int callType;            //类型
    private int duration;            //通话时长

    private List<CallLogModelDBFlow> callLogModelDBFlowList = new ArrayList<>();

    /**
     * 数据库异步存储得到结果后回调接口
     */
    public interface DBFlowDatabaseSaveCallback {
        void success();
    }

    /**
     * 得到新的通话记录的数量
     */
    public int getNewCallLogCount(Cursor cursor) {

        int countNewRecords = 0;

        try {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    if (getRecordAndExistence(cursor)) {
                        continue;
                    }
                    countNewRecords++; //新的记录
                    createAndAddToRecordList();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return countNewRecords;
    }

    /**
     * 异步存储到数据库。
     */
    public void asyncSaveToDatabase(final DBFlowDatabaseSaveCallback mCallBack) {
        DatabaseDefinition database = FlowManager.getDatabase(CallLogDatabase.class);
        Transaction transaction = database.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                if (!callLogModelDBFlowList.isEmpty()) {
                    for (CallLogModelDBFlow callLogModelDBFlow : callLogModelDBFlowList) {
                        callLogModelDBFlow.save();
                    }
                }
            }
        }).success(new Transaction.Success() {
            @Override
            public void onSuccess(Transaction transaction) {
                if (mCallBack != null) {
                    mCallBack.success();
                }
            }
        }).build();
        transaction.execute(); // execute
    }

    /**
     * 获取一条通话记录，同时查询该条记录是否已经存储到数据库中。
     * @param cursor
     * @return
     */
    private boolean getRecordAndExistence(Cursor cursor) {

        dateInMilliseconds = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));

        if (isRecordExistInDatabase(dateInMilliseconds)) {
            //记录已存在
            return true;
        }

        contactsName = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
        phoneNumber = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
        duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
        callType = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));

        //记录不存在
        return false;
    }

    /**
     * 用获取的通话记录创建DBFlow模型对象并添加到模型对象列表
     */
    private void createAndAddToRecordList() {

        CallLogModelDBFlow callLogModelDBFlow = new CallLogModelDBFlow();

        callLogModelDBFlow.setDateInMilliseconds(dateInMilliseconds);



        Boolean isContactsNameEmpty = ( TextUtils.isEmpty(contactsName) || contactsName.equals("null"));
        if (isContactsNameEmpty) {
            //有些号码不能显示
            if (isPhoneNumber(phoneNumber) == false) {
                phoneNumber = DatabaseConstant.UNKOWN_PHONE_NUMBER;
            }

            //当联系人为空时，将联系人设为电话号码本身存储
            contactsName = phoneNumber;
        }

        callLogModelDBFlow.setContactsName(contactsName);
        callLogModelDBFlow.setPhoneNumber(phoneNumber);
        callLogModelDBFlow.setDuration(duration);
        callLogModelDBFlow.setCallType(callType);

        callLogModelDBFlowList.add(callLogModelDBFlow);
    }

    /**
     * 根据通话发生时间（毫秒数），查询在数据库中是否存在这条记录。
     * @param dateInMilliseconds
     * @return
     */
    private boolean isRecordExistInDatabase(long dateInMilliseconds) {

        CallLogModelDBFlow callLogModelDBFlow = SQLite.select().from(CallLogModelDBFlow.class)
                .where(CallLogModelDBFlow_Table.dateInMilliseconds.eq(dateInMilliseconds))
                .querySingle();

        return !(callLogModelDBFlow == null);
    }

    /**
     * 判断号码是否为电话号码
     * @param phoneNumber
     * @return
     */
    public static boolean isPhoneNumber(String phoneNumber) {
        boolean flag = false;
        try {
            Pattern pattern = Pattern.compile("[0-9]+");
            Matcher matcher = pattern.matcher(phoneNumber);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }

        return flag;
    }
}
