package com.navigation_ui.model;


/**
 * Created by Yong on 2017/2/6.
 */

/**
 * RecyclerView的适配类，对应布局文件 calllog_item.xml
 */
public class CallLogItemModel {

    //姓名
    private String contactsName;

    //电话号码
    private String phoneNumber;

    //日期
    private String dateInMilliseconds;

    //通话次数
    private int callCounts;

    //通话时间，秒
    private String duration;

    //类型
    private int callType;

    //归属地
    private String callerLoc;

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

    public String getDateInMilliseconds() {
        return dateInMilliseconds;
    }

    public void setDateInMilliseconds(String dateInMilliseconds) {
        this.dateInMilliseconds = dateInMilliseconds;
    }

    public int getCallCounts() {
        return callCounts;
    }

    public void setCallCounts(int callCounts) {
        this.callCounts = callCounts;
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
                " " + duration + " " + callType + " " + callerLoc + " " + callCounts;
    }
}
