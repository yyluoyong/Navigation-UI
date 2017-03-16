package com.call.log.infinity.model;


/**
 * Created by Yong on 2017/2/6.
 */

import com.call.log.infinity.utils.CallDateFormatter;
import com.call.log.infinity.utils.CallDurationFormatter;
import com.call.log.infinity.utils.PhoneNumberFormatter;

/**
 * RecyclerView的适配类，对应布局文件 main_calllog_item.xml
 */
public class CallLogItemModel {

    /**
     * 布局文件可访问的是格式化之后的自带，如：phoneNumberFormat、dateFormat、durationFormat
     */

    //姓名
    private String contactsName;

    //电话号码
    private String phoneNumber;

    //电话号码格式化
    private String phoneNumberFormat;

    //日期
    private long dateInMilliseconds;

    //日期格式化
    private String dateFormat;

    //通话次数
    private int callCounts;

    //通话时间，秒
    private int duration;

    //通话时间格式化
    private String durationFormat;

    //类型
    private int callType;

    //归属地
    private String callerLoc;

    //运营商
    private String operator;

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
        setPhoneNumberFormat(PhoneNumberFormatter.phoneNumberFormat(phoneNumber));
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumberFormat() {
        return phoneNumberFormat;
    }

    private void setPhoneNumberFormat(String phoneNumberFormat) {
        this.phoneNumberFormat = phoneNumberFormat;
    }

    public void setDateInMilliseconds(long dateInMilliseconds) {
        setDateFormat(CallDateFormatter.format(dateInMilliseconds));
        this.dateInMilliseconds = dateInMilliseconds;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    private void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public int getCallCounts() {
        return callCounts;
    }

    public void setCallCounts(int callCounts) {
        this.callCounts = callCounts;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        setDurationFormat(CallDurationFormatter.format(duration));
        this.duration = duration;
    }

    public String getDurationFormat() {
        return durationFormat;
    }

    private void setDurationFormat(String durationFormat) {
        this.durationFormat = durationFormat;
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
            "callType=%5$d, callCounts=%6$d, callerLoc=%7$s, operator=%8$s";

        return String.format(format, dateInMilliseconds, contactsName,
            phoneNumber, duration, callType, callCounts, callerLoc, operator);
    }
}
