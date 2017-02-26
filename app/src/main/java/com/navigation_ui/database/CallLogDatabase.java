package com.navigation_ui.database;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by Yong on 2017/2/5.
 */

/**
 * 设置数据库的名字和版本号。
 * 该数据库用来保存从系统获得的通话记录，对应模型CallLogModelDBFlow.class。
 */
@Database(name = CallLogDatabase.NAME, version = CallLogDatabase.VERSION)
public class CallLogDatabase {

    public static final String NAME = "CallLogDatabase"; // we will add the .db extension

    public static final int VERSION = 1;
}