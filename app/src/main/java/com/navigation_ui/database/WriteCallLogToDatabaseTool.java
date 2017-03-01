package com.navigation_ui.database;

import android.database.Cursor;
import android.provider.CallLog;
import android.text.TextUtils;

import com.navigation_ui.tools.CallerLocQuery;
import com.navigation_ui.tools.LogUtil;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Yong on 2017/2/1.
 */

/**
 * 输入系统通话记录查询Cursor
 * 返回新增通话记录条目的数量Integer
 */
public class WriteCallLogToDatabaseTool {
    static final String TAG = "WriteCallLogToDatabaseTool";

    private String dateInMilliseconds; //通话发生时间
    private String contactsName;       //联系人名字
    private String phoneNumber;        //电话号码
    private String callerLoc;          //通话时长
    private int callType;              //类型
    private String duration;           //号码归属地
    private String operator;           //运营商

    private static final String UNKOWN_AREA = "未知归属地";
    private static final String UNKOWN_OPERATOR = "未知运营商";

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
                        LogUtil.d(TAG, "记录已存在");
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

        dateInMilliseconds = cursor.getString(cursor
                .getColumnIndex(CallLog.Calls.DATE));

        if (isRecordExistInDatabase(dateInMilliseconds)) {
            //记录已存在
            return true;
        }

        contactsName = cursor.getString(cursor
                .getColumnIndex(CallLog.Calls.CACHED_NAME));

        phoneNumber = cursor.getString(cursor
                .getColumnIndex(CallLog.Calls.NUMBER));

        duration = cursor.getString(cursor
                .getColumnIndex(CallLog.Calls.DURATION));

        callType = cursor.getInt(cursor
                .getColumnIndex(CallLog.Calls.TYPE));

        String[] areaAndOperator = CallerLocQuery.callerLocQuery(phoneNumber);
//        String[] areaAndOperator = CallerLocQuery.callerLocQuery("10086");
        callerLoc = areaAndOperator[0];
        operator = areaAndOperator[1];

        if (TextUtils.isEmpty(callerLoc)) {
            callerLoc = UNKOWN_AREA;
            operator = UNKOWN_OPERATOR;
        }

        //记录不存在
        return false;
    }

    /**
     * 用获取的通话记录创建DBFlow模型对象并添加到模型对象列表
     */
    private void createAndAddToRecordList() {

        CallLogModelDBFlow callLogModelDBFlow = new CallLogModelDBFlow();

        callLogModelDBFlow.setDateInMilliseconds(dateInMilliseconds);

        Boolean isContactsNameEmpty = ( TextUtils.isEmpty(contactsName) ||
                contactsName.equals("null"));
        if (isContactsNameEmpty) {
            //当联系人为空时，将联系人设为电话号码本身存储
            contactsName = phoneNumber;
        }

        callLogModelDBFlow.setContactsName(contactsName);
        callLogModelDBFlow.setPhoneNumber(phoneNumber);
        callLogModelDBFlow.setDuration(duration);
        callLogModelDBFlow.setCallType(callType);
        callLogModelDBFlow.setCallerLoc(callerLoc);
        callLogModelDBFlow.setOperator(operator);

        callLogModelDBFlowList.add(callLogModelDBFlow);

        LogUtil.d(TAG, callLogModelDBFlow.toString());
    }

    /**
     * 根据通话发生时间（毫秒数），查询在数据库中是否存在这条记录。
     * @param dateInMilliseconds
     * @return
     */
    private boolean isRecordExistInDatabase(String dateInMilliseconds) {

        CallLogModelDBFlow callLogModelDBFlow = SQLite.select().from(CallLogModelDBFlow.class)
                .where(CallLogModelDBFlow_Table.dateInMilliseconds.eq(dateInMilliseconds))
                .querySingle();

        return !(callLogModelDBFlow == null);
    }
}
