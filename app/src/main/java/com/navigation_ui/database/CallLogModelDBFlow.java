package com.navigation_ui.database;

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
 */
@Table(database = CallLogDatabase.class)
public class CallLogModelDBFlow extends BaseModel {
    /**
     * 通话发生时间，单位：毫秒
     * 作为唯一主键，标识每条记录
     */
    @PrimaryKey
    @Unique
    private String dateInMilliseconds;

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
    private String duration;

    /**
     * 通话类型：CallLog.Calls.INCOMING_TYPE，CallLog.Calls.OUTGOING_TYPE，
     * CallLog.Calls.OUTGOING_TYPE。
     */
    @Column
    private int callType;

    /**
     * 电话号码的归属地
     */
    @Column
    private String callerLoc;

    public String getDateInMilliseconds() {
        return dateInMilliseconds;
    }

    public void setDateInMilliseconds(String dateInMilliseconds) {
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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
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

    @Override
    public String toString() {
        return  contactsName + " " + phoneNumber + " " + dateInMilliseconds +
                " " + duration + " " + callType + " " + callerLoc;
    }
}

