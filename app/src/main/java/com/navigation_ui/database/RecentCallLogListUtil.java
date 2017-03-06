package com.navigation_ui.database;

import android.database.Cursor;
import android.text.TextUtils;

import com.navigation_ui.MyApplication;
import com.navigation_ui.R;
import com.navigation_ui.model.CallLogItemModel;
import com.navigation_ui.utils.CallerLocQueryUtil;
import com.navigation_ui.utils.LogUtil;
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

    private static final String UNKOWN_AREA = MyApplication.getContext()
        .getString(R.string.UnkownArea);
    private static final String UNKOWN_OPERATOR = MyApplication.getContext()
        .getString(R.string.UnkownOperator);

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

    //查询每个联系人指定通话类型的通话
    private static String RECENT_CALL_TYPE_SQL = "select tb_1.*, tb_2." + CALL_COUNTS_COLUMN_NAME
        + " from (select a.* from " + DATABASE_TABLE_NAME + " as a where "
        + TYPE_COLUMN_NAME + " = ? and " + TIME_COLUMN_NAME
        + " = (select max(" + TIME_COLUMN_NAME + ") from " + DATABASE_TABLE_NAME
        + " where " + TYPE_COLUMN_NAME + " = ? and " + CONTACTS_NAME_COLUMN_NAME
        + " = a." + CONTACTS_NAME_COLUMN_NAME + ") order by a." + TIME_COLUMN_NAME + ") "
        + "as tb_1 "
        + "join "
        + "(select " + CONTACTS_NAME_COLUMN_NAME + ", count(" + CONTACTS_NAME_COLUMN_NAME
        + ") as " + CALL_COUNTS_COLUMN_NAME + " from " + DATABASE_TABLE_NAME + " where "
        + TYPE_COLUMN_NAME + " = ? " +  " group by " + CONTACTS_NAME_COLUMN_NAME + ") "
        + "as tb_2 "
        + "on tb_1." + CONTACTS_NAME_COLUMN_NAME + " = tb_2." + CONTACTS_NAME_COLUMN_NAME
        + " order by tb_1." + TIME_COLUMN_NAME + " desc";

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
                String typeToStr = String.valueOf(type);
                cursor = db
                    .rawQuery(RECENT_CALL_TYPE_SQL, new String[]{typeToStr, typeToStr, typeToStr});
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

        String[] areaAndOperator = CallerLocQueryUtil.callerLocQuery(phoneNumber);
        callerLoc = areaAndOperator[0];
        operator = areaAndOperator[1];

        if (TextUtils.isEmpty(callerLoc)) {
            callerLoc = UNKOWN_AREA;
            operator = UNKOWN_OPERATOR;
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
