package com.call.log.infinity.database;

import com.call.log.infinity.MyApplication;
import com.call.log.infinity.R;

/**
 * Created by Yong on 2017/3/20.
 */

public class DatabaseConstant {

    //某些情况，来电不能正常显示号码，对方通过特殊工具拨出？
    public static final String UNKOWN_PHONE_NUMBER = MyApplication.getContext().getString(R.string.unkownPhoneNumber);
}
