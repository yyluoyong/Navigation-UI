package com.call.log.infinity.utils;

/**
 * Created by Yong on 2017/2/19.
 */

import com.call.log.infinity.MyApplication;
import com.call.log.infinity.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 将通话发生时间格式化，得到如：今天 09:23，昨天 15:11 等
 */
public class CallDateFormatter {

    //一天包含的毫秒数
    public static final long A_DAY_IN_MILLISECOND = 86400000L;
    //一个小时包含的毫秒数
    public static final long A_HOUR_IN_MILLISECOND = 3600000L;
    //一分钟包含的毫秒数
    public static final long A_MINUTE_IN_MILLISECOND = 60000L;
    //一秒钟包含的毫秒数
    public static final long A_SECOND_IN_MILLISECOND = 1000L;

    public static final String YEAR_MONTH_DAY = MyApplication.getContext().getString(R.string.yearMonthDay);
    public static final String MONTH_DAY_HOUR_MINUTE = MyApplication.getContext().getString(R.string.monthDayHourMinute);
    public static final String HOUR_MINUTE = MyApplication.getContext().getString(R.string.hourMinute);

    public static String format(String dateInMillis) {
        return format(Long.parseLong(dateInMillis));
    }

    public static String format(long dateInMillis) {

        StringBuilder result = new StringBuilder();

        long currentDateMillis = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();

        //dateInMillis与当前时间的时间差（单位：毫秒）
        long pastTimeMillis = currentDateMillis - dateInMillis;

        //今年已过去的时间转化为毫秒
        long currentYearPastMillis;
        //今天已过去的时间转化为毫秒
        long currentDayPastMillis;

        currentDayPastMillis = calendar.get(Calendar.HOUR_OF_DAY) * A_HOUR_IN_MILLISECOND
            + calendar.get(Calendar.MINUTE) * A_MINUTE_IN_MILLISECOND
            + calendar.get(Calendar.SECOND) * A_SECOND_IN_MILLISECOND
            + calendar.get(Calendar.MILLISECOND);

        currentYearPastMillis = ( calendar.get(Calendar.DAY_OF_YEAR) - 1 ) * A_DAY_IN_MILLISECOND
                + currentDayPastMillis;

        if ( pastTimeMillis <= currentYearPastMillis ) {
            //今年
            if ( pastTimeMillis <= currentDayPastMillis ) {
                //今天 几点:几分
                SimpleDateFormat dayFormat = new SimpleDateFormat(HOUR_MINUTE);
                String todayCall = MyApplication.getContext().getString(R.string.todayCall);
                return String.format(todayCall, dayFormat.format(new Date(dateInMillis)));
            }

            if ( pastTimeMillis <= currentDayPastMillis + A_DAY_IN_MILLISECOND) {
                //昨天 几点:几分
                SimpleDateFormat dayFormat = new SimpleDateFormat(HOUR_MINUTE);
                String yesterdayCall = MyApplication.getContext().getString(R.string.yesterdayCall);
                return String.format(yesterdayCall, dayFormat.format(new Date(dateInMillis)));
            }

            //几月几日 几点:几分
            SimpleDateFormat monthFormat = new SimpleDateFormat(MONTH_DAY_HOUR_MINUTE);

            return result.append(monthFormat.format(new Date(dateInMillis))).toString();
        }

        //多少年几月几日
        SimpleDateFormat yearFormat = new SimpleDateFormat(YEAR_MONTH_DAY);

        return result.append(yearFormat.format(new Date(dateInMillis))).toString();
    }
}
