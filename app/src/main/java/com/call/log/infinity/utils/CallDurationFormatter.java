package com.call.log.infinity.utils;

import com.call.log.infinity.MyApplication;
import com.call.log.infinity.R;

/**
 * Created by Yong on 2017/2/26.
 */

public class CallDurationFormatter {

    //一个小时包含的毫秒数
    private final static int A_HOUR_IN_SECOND = 3600;
    //一分钟包含的毫秒数
    private final static int A_MINUTE_IN_SECOND = 60;

    private final static String HOUR_NAME = MyApplication.getContext().getString(R.string.hourName);
    private final static String MINUTE_NAME = MyApplication.getContext().getString(R.string.minuteName);
    private final static String SECOND_NAME = MyApplication.getContext().getString(R.string.secondName);

    public static String format(String duration) {
        return format(Integer.parseInt(duration));
    }

    public static String format(int duration) {
        StringBuilder stringBuilder = new StringBuilder();

        int hours = getHours(duration);
        int restSeconds = duration - hours * A_HOUR_IN_SECOND;
        int minutes = getMinutes(restSeconds);
        restSeconds = restSeconds - minutes * A_MINUTE_IN_SECOND;

        if (hours > 0) {
            stringBuilder.append(hours).append(HOUR_NAME).append(minutes).append(MINUTE_NAME)
                .append(restSeconds).append(SECOND_NAME);

            return stringBuilder.toString();
        }

        if (minutes > 0) {
            stringBuilder.append(minutes).append(MINUTE_NAME).append(restSeconds).append(SECOND_NAME);
            return stringBuilder.toString();
        }

        stringBuilder.append(restSeconds).append(SECOND_NAME);
        return stringBuilder.toString();
    }

    private static int getMinutes(int seconds) {
        return seconds / A_MINUTE_IN_SECOND;
    }

    private static int getHours(int seconds) {
        return seconds / A_HOUR_IN_SECOND;
    }
}
