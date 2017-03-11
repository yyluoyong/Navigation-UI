package com.call.log.infinity.database;

import android.database.Cursor;
import android.provider.CallLog;
import android.text.TextUtils;

import com.call.log.infinity.MyApplication;
import com.call.log.infinity.R;
import com.call.log.infinity.model.CallLogItemModel;
import com.call.log.infinity.utils.CallerAreaAndOperatorQueryUtil;
import com.call.log.infinity.utils.LogUtil;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.AndroidDatabase;
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

    //查询所有类型的通话
    public static final int TYPE_ALL = 10;

    private String dateInMilliseconds; //通话发生时间
    private String contactsName;       //联系人名字
    private String phoneNumber;        //电话号码
    private String duration;           //通话时长
    private int callType;              //类型
    private String callerLoc;          //归属地
    private int callCounts;            //通话次数
    private String operator;           //运营商

    /**
     * 注意：以下数据与DBFlow使用的模型类CallLogModelDBFlow一致。
     */
    //数据库表名
    private static final String DATABASE_TABLE_NAME = MyApplication.getContext()
        .getString(R.string.CallLogDatabaseTable);
    //数据库中通话时间列的列名
    private static final String TIME_COLUMN_NAME = MyApplication.getContext()
        .getString(R.string.CallLogDatabaseTableColumnTime);
    //数据库中联系人列的列名
    private static final String CONTACTS_NAME_COLUMN_NAME = MyApplication.getContext()
        .getString(R.string.CallLogDatabaseTableColumnContactsName);
    //数据库中电话号码列的列名
    private static final String PHONE_NUMBER_COLUMN_NAME = MyApplication.getContext()
        .getString(R.string.CallLogDatabaseTableColumnPhoneNumber);
    //数据库中通话时长列的列名
    private static final String DURATION_COLUMN_NAME = MyApplication.getContext()
        .getString(R.string.CallLogDatabaseTableColumnDuration);
    //数据库中通话类型列的列名
    private static final String TYPE_COLUMN_NAME = MyApplication.getContext()
        .getString(R.string.CallLogDatabaseTableColumnCallType);
    //数据库中通话次数的列名
    private static final String CALL_COUNTS_COLUMN_NAME = MyApplication.getContext()
        .getString(R.string.CallLogDatabaseTableColumnCounts);

    private static final String CONDITION_POSITION_1 = "%1$s";
    private static final String CONDITION_POSITION_2 = "%2$s";
    private static final String CONDITION_POSITION_3 = "%3$s";

    //和每个联系人符合某种条件的最近一次通话
    private static final String CONTACTS_RECENT_TIME_FORMAT = "select a.* from %1$s as a where %2$s and " +
        "%3$s = (select max(%4$s) from %5$s where %6$s and %7$s = a.%8$s) order by a.%9$s";

    private static final String CONTACTS_RECENT_TIME_CONDITION = String.format(CONTACTS_RECENT_TIME_FORMAT,
        DATABASE_TABLE_NAME, CONDITION_POSITION_1, TIME_COLUMN_NAME, TIME_COLUMN_NAME, DATABASE_TABLE_NAME,
        CONDITION_POSITION_2, CONTACTS_NAME_COLUMN_NAME, CONTACTS_NAME_COLUMN_NAME, TIME_COLUMN_NAME);

    //和每个联系人符合某种条件的通话次数
    private static final String CONTACTS_CALL_COUNTS_FORMAT = "select %1$s, count(%2$s) as %3$s " +
        "from %4$s where %5$s group by %6$s";

    private static final String CONTACTS_CALL_COUNTS_CONDITION = String.format(CONTACTS_CALL_COUNTS_FORMAT,
        CONTACTS_NAME_COLUMN_NAME, CONTACTS_NAME_COLUMN_NAME, CALL_COUNTS_COLUMN_NAME,
        DATABASE_TABLE_NAME, CONDITION_POSITION_3, CONTACTS_NAME_COLUMN_NAME);

    /*
    查询每个联系人某种条件的通话（统计出符合该条件的通话次数和最近一次符合该条件的通话时间），
    通过组合 CONTACTS_RECENT_TIME_FORMAT 和 CONTACTS_CALL_COUNTS_FORMAT 进行联结查询完成。
     */
    private static final String RECENT_CALL_TYPE_FORMAT = "select tb_1.*, tb_2.%1$s from (%2$s) as tb_1 " +
        "join (%3$s) as tb_2 on tb_1.%4$s = tb_2.%5$s order by tb_1.%6$s desc";

    private static String RECENT_CALL_TYPE_CONDITION = String.format(RECENT_CALL_TYPE_FORMAT,
        CALL_COUNTS_COLUMN_NAME, CONTACTS_RECENT_TIME_CONDITION, CONTACTS_CALL_COUNTS_CONDITION,
        CONTACTS_NAME_COLUMN_NAME, CONTACTS_NAME_COLUMN_NAME, TIME_COLUMN_NAME);

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

    //********************************
    //判断通话类型为未接电话
    private static final String MISSED_CONDITION_FORMAT = "(%1$s = %2$d or (%3$s = %4$d and %5$s = 0))";
    private static final String MISSED_CONDITION = String.format(MISSED_CONDITION_FORMAT,
        TYPE_COLUMN_NAME, CallLog.Calls.MISSED_TYPE, TYPE_COLUMN_NAME, CallLog.Calls.INCOMING_TYPE,
        DURATION_COLUMN_NAME);

    //未接电话
    private static String RECENT_CALL_MISSED_SQL = String.format(RECENT_CALL_TYPE_CONDITION,
        MISSED_CONDITION, MISSED_CONDITION, MISSED_CONDITION);
    //================================

    //********************************
    //拨出电话
    private static final String OUTGOING_CONDITION = TYPE_COLUMN_NAME + " = "
        + CallLog.Calls.OUTGOING_TYPE;

    private static String RECENT_CALL_MADE_SQL = String.format(RECENT_CALL_TYPE_CONDITION,
        OUTGOING_CONDITION, OUTGOING_CONDITION, OUTGOING_CONDITION);
    //================================

    //********************************
    //接入电话
    private static final String INCOMING_CONDITION_FORMAT = "(%1$s = %2$d or %3$s = %4$d )";

    private static final String INCOMING_CONDITION = String.format(INCOMING_CONDITION_FORMAT,
        TYPE_COLUMN_NAME, CallLog.Calls.INCOMING_TYPE, TYPE_COLUMN_NAME, CallLog.Calls.MISSED_TYPE);

    private static String RECENT_CALL_RECEIVED_SQL = String.format(RECENT_CALL_TYPE_CONDITION,
        INCOMING_CONDITION, INCOMING_CONDITION, INCOMING_CONDITION);
    //================================

    static {
        LogUtil.d(TAG, RECENT_CALL_RECEIVED_SQL);
    }

    /**
     * 从数据库中得到最近的通话列表。
     * @return
     */
    public List<CallLogItemModel> getRecentCallLogItemList(int type) {
        List<CallLogItemModel> callLogList = new ArrayList<>();

        Cursor cursor = null;

        try{
            AndroidDatabase db = (AndroidDatabase) FlowManager.getDatabase(CallLogDatabase.class)
                .getWritableDatabase();

            if (type == TYPE_ALL) {
                cursor = db.rawQuery(RECENT_CALL_SQL, null);
            } else {
                if (type == CallLog.Calls.INCOMING_TYPE) {
                    cursor = db.rawQuery(RECENT_CALL_RECEIVED_SQL, null);
                } else if (type == CallLog.Calls.MISSED_TYPE) {
                    cursor = db.rawQuery(RECENT_CALL_MISSED_SQL, null);
                } else {
                    cursor = db.rawQuery(RECENT_CALL_MADE_SQL, null);
                }
            }

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
    private void getRecord(Cursor cursor) {
        /**
         * LitePal：注意getColumnIndex传入字符串使用小写，否则会找不到列名。
         * DBFlow： 注意getColumnIndex与表定义一样，区分大小写。
         */
        dateInMilliseconds = cursor.getString(cursor.getColumnIndex(TIME_COLUMN_NAME));
        contactsName = cursor.getString(cursor.getColumnIndex(CONTACTS_NAME_COLUMN_NAME));
        phoneNumber = cursor.getString(cursor.getColumnIndex(PHONE_NUMBER_COLUMN_NAME));
        duration = cursor.getString(cursor.getColumnIndex(DURATION_COLUMN_NAME));
        callType = cursor.getInt(cursor.getColumnIndex(TYPE_COLUMN_NAME));
        callCounts = cursor.getInt(cursor.getColumnIndex(CALL_COUNTS_COLUMN_NAME));

        String[] areaAndOperator = CallerAreaAndOperatorQueryUtil.callerLocQuery(phoneNumber);
        callerLoc = areaAndOperator[0];
        operator = areaAndOperator[1];

        if (TextUtils.isEmpty(callerLoc)) {
            callerLoc = CallerAreaAndOperatorQueryUtil.UNKOWN_AREA;
            operator = CallerAreaAndOperatorQueryUtil.UNKOWN_OPERATOR;
        }
    }

    /**
     * 根据读取的一行数据创建一个模型对象。
     * @return
     */
    private CallLogItemModel newCallLogModel() {
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
