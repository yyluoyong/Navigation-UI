package com.call.log.infinity.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Yong on 2017/2/5.
 */

/**
 * 每条通话记录包含的信息。
 * DBFlow使用存储的模型。
 * 注意：该模型的列名被用于数据库查询，若修改列名，可能会使其他类中的查询函数出错。
 */
@Table(database = CallLogDatabase.class)
public class CallLogModelDBFlow extends BaseModel {
    /**
     * 通话发生时间，单位：毫秒
     * 作为唯一主键，标识每条记录
     */
    @PrimaryKey
    @Unique
    private long dateInMilliseconds;

    /**
     * 联系人姓名
     */
    @Column
    private String contactsName;

    /**
     * 电话号码
     */
    @Column
    private String phoneNumber;

    /**
     * 通话时间，单位：秒
     */
    @Column
    private int duration;

    /**
     * 通话类型：CallLog.Calls.INCOMING_TYPE，CallLog.Calls.OUTGOING_TYPE，
     * CallLog.Calls.OUTGOING_TYPE。
     */
    @Column
    private int callType;

    /**
     * 电话号码的归属地
     * 注：该属性并非一层不变，因此不存储到数据库中。
     */
    private String callerLoc;

    /**
     * 运营商
     * 注：该属性并非一层不变，因此不存储到数据库中。
     */
    private String operator;

    public long getDateInMilliseconds() {
        return dateInMilliseconds;
    }

    public void setDateInMilliseconds(long dateInMilliseconds) {
        this.dateInMilliseconds = dateInMilliseconds;
    }

    public String getContactsName() {
        return contactsName;
    }

    public void setContactsName(String contactsName) {
        this.contactsName = contactsName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getCallType() {
        return callType;
    }

    public void setCallType(int callType) {
        this.callType = callType;
    }

    public String getCallerLoc() {
        return callerLoc;
    }

    public void setCallerLoc(String callerLoc) {
        this.callerLoc = callerLoc;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        String format = "date=%1$d, name=%2$s, number=%3$s, duration=%4$d, " +
            "callType=%5$d, callerLoc=%6$s, operator=%7$s";

        return String.format(format, dateInMilliseconds, contactsName,
            phoneNumber, duration, callType, callerLoc, operator);
    }
}

