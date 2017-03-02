package com.navigation_ui.database;

import android.database.Cursor;

import com.navigation_ui.model.CallLogItemModel;
import com.navigation_ui.utils.LogUtil;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yong on 2017/2/2.
 */

/**
 * 从DBFlow存储的通话记录数据库中获得最近通话列表。
 */
public class RecentCallLogListUtil {
    static final String TAG = "RecentCallLogListUtil";

    private static String dateInMilliseconds; //通话发生时间
    private static String contactsName;       //联系人名字
    private static String phoneNumber;        //电话号码
    private static String duration;           //通话时长
    private static int callType;              //类型
    private static String callerLoc;          //归属地
    private static int callCounts;            //通话次数
    private static String operator;           //运营商

    /**
     * 注意：以下数据与DBFlow使用的模型类CallLogModelDBFlow一致。
     */
    //数据库表名
    private static final String DATABASE_TABLE_NAME = "CallLogModelDBFlow";
    //数据库中通话时间列的列名
    private static final String TIME_COLUMN_NAME = "dateInMilliseconds";
    //数据库中联系人列的列名
    private static final String CONTACTS_NAME_COLUMN_NAME = "contactsName";
    //数据库中电话号码列的列名
    private static final String PHONE_NUMBER_COLUMN_NAME = "phoneNumber";
    //数据库中通话时长列的列名
    private static final String DURATION_COLUMN_NAME = "duration";
    //数据库中通话类型列的列名
    private static final String TYPE_COLUMN_NAME = "callType";
    //数据库中归属地列的列名
    private static final String LOCATION_COLUMN_NAME = "callerLoc";
    //数据库中通话次数的列名
    private static final String CALL_COUNTS_COLUMN_NAME = "counts";
    //数据库中运营商列的列名
    private static final String OPERATOR_COLUMN_NAME = "operator";

    //查询每个联系人的最近一次通话的SQL语句
    private static final String RECENT_CALL_SQL = "select tb_1.*, tb_2." + CALL_COUNTS_COLUMN_NAME
        + " from (select a.* from " + DATABASE_TABLE_NAME + " as a where " + TIME_COLUMN_NAME
        + " = (select max(" + TIME_COLUMN_NAME + ") from " + DATABASE_TABLE_NAME
        + " where " + CONTACTS_NAME_COLUMN_NAME + " = a." + CONTACTS_NAME_COLUMN_NAME
        + ") order by a." + TIME_COLUMN_NAME + ") "
        + "as tb_1 "
        + "join "
        + "(select " + CONTACTS_NAME_COLUMN_NAME + ", count(" + CONTACTS_NAME_COLUMN_NAME
        + ") as " + CALL_COUNTS_COLUMN_NAME + " from " + DATABASE_TABLE_NAME + " group by "
        + CONTACTS_NAME_COLUMN_NAME + ") as tb_2 "
        + "on tb_1." + CONTACTS_NAME_COLUMN_NAME + " = tb_2." + CONTACTS_NAME_COLUMN_NAME
        + " order by tb_1." + TIME_COLUMN_NAME + " desc";

    /**
     * 从数据库中得到最近的通话列表。
     * @return
     */
    public static List<CallLogItemModel> getRecentCallLogItemList() {
        List<CallLogItemModel> callLogList = new ArrayList<>();

        Cursor cursor = null;

        try{
            cursor = FlowManager.getDatabase(CallLogDatabase.class).getWritableDatabase()
                .rawQuery(RECENT_CALL_SQL, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    getRecord(cursor);
                    callLogList.add(newCallLogModel());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return callLogList;
    }

    /**
     * 从给定的cursor读取一行数据。
     * @param cursor
     */
    private static void getRecord(Cursor cursor) {
        /**
         * LitePal：注意getColumnIndex传入字符串使用小写，否则会找不到列名。
         * DBFlow： 注意getColumnIndex与表定义一样，区分大小写。
         */
        dateInMilliseconds = cursor.getString(cursor.getColumnIndex(TIME_COLUMN_NAME));
        contactsName = cursor.getString(cursor.getColumnIndex(CONTACTS_NAME_COLUMN_NAME));
        phoneNumber = cursor.getString(cursor.getColumnIndex(PHONE_NUMBER_COLUMN_NAME));
        duration = cursor.getString(cursor.getColumnIndex(DURATION_COLUMN_NAME));
        callType = cursor.getInt(cursor.getColumnIndex(TYPE_COLUMN_NAME));
        callerLoc = cursor.getString(cursor.getColumnIndex(LOCATION_COLUMN_NAME));
        callCounts = cursor.getInt(cursor.getColumnIndex(CALL_COUNTS_COLUMN_NAME));
        operator = cursor.getString(cursor.getColumnIndex(OPERATOR_COLUMN_NAME));
    }

    /**
     * 根据读取的一行数据创建一个模型对象。
     * @return
     */
    private static CallLogItemModel newCallLogModel() {
        CallLogItemModel model = new CallLogItemModel();

        model.setDateInMilliseconds(dateInMilliseconds);
        model.setContactsName(contactsName);
        model.setPhoneNumber(phoneNumber);
        model.setDuration(duration);
        model.setCallType(callType);
        model.setCallerLoc(callerLoc);
        model.setCallCounts(callCounts);
        model.setOperator(operator);

        return model;
    }

}
