package com.call.log.infinity.model;

/**
 * Created by Yong on 2017/3/18.
 */

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 搜索信息模型。
 */
public class SearchModel implements Parcelable {
    private String contactsName = null;
    private String phoneNumber = null;
    private long dateStart = -1L;
    private long dateEnd = -1L;
    private int durationMin = -1;
    private int durationMax = -1;
    private int callType = 0;

    public SearchModel() {
    }

    public SearchModel(Parcel source) {
        contactsName = source.readString();
        phoneNumber = source.readString();
        dateStart = source.readLong();
        dateEnd = source.readLong();
        durationMin = source.readInt();
        durationMax = source.readByte();
        callType = source.readInt();
    }

    @Override
    public String toString() {
        String format = "name:%1$s, phone:%2$s, date:%3$d --> %4$d, duration:%5$d --> %6$d, type:%7$d";
        return String.format(format, contactsName, phoneNumber, dateStart, dateEnd,
            durationMin, durationMax, callType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(contactsName);
        dest.writeString(phoneNumber);
        dest.writeLong(dateStart);
        dest.writeLong(dateEnd);
        dest.writeInt(durationMin);
        dest.writeInt(durationMax);
        dest.writeInt(callType);
    }

    public final static Creator<SearchModel> CREATOR = new Creator<SearchModel>() {
        @Override
        public SearchModel createFromParcel(Parcel source) {
            return new SearchModel(source);
        }

        @Override
        public SearchModel[] newArray(int size) {
            return new SearchModel[size];
        }
    };

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

    public long getDateStart() {
        return dateStart;
    }

    public void setDateStart(long dateStart) {
        this.dateStart = dateStart;
    }

    public long getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(long dateEnd) {
        this.dateEnd = dateEnd;
    }

    public int getDurationMin() {
        return durationMin;
    }

    public void setDurationMin(int durationMin) {
        this.durationMin = durationMin;
    }

    public int getDurationMax() {
        return durationMax;
    }

    public void setDurationMax(int durationMax) {
        this.durationMax = durationMax;
    }

    public int getCallType() {
        return callType;
    }

    public void setCallType(int callType) {
        this.callType = callType;
    }
}
