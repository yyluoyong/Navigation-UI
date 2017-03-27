package com.call.log.infinity.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.QueryModel;
import com.raizlabs.android.dbflow.structure.BaseQueryModel;

/**
 * Created by Yong on 2017/3/27.
 */

@QueryModel(database = CallLogDatabase.class)
public class TotalDurationQueryModel extends BaseQueryModel {

    @Column
    public String contactsName;

    @Column
    public long totalDuration;

    @Override
    public String toString() {
        String format = "contactsName = %1$s, totalDuration = %2$d";
        return String.format(format, contactsName, totalDuration);
    }
}
