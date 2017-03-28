package com.call.log.infinity.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.QueryModel;
import com.raizlabs.android.dbflow.structure.BaseQueryModel;

/**
 * Created by Yong on 2017/3/28.
 */

@QueryModel(database = CallLogDatabase.class)
public class LongResultQueryModel extends BaseQueryModel {

    @Column
    public long longResult;
}
