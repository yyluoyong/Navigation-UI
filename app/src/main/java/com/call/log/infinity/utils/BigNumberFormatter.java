package com.call.log.infinity.utils;

import android.support.annotation.NonNull;
import java.text.DecimalFormat;

/**
 * Created by Yong on 2017/3/26.
 */

/**
 * 将正数数字格式化为英文缩写。
 */
public class BigNumberFormatter {

    static final String KILO = "K";
    static final String MEGA = "M";
    static final String GIGA = "G";
    static final String TERA = "T";
    static final String PETA = "P";
    static final String EXA = "E";
    static final String BRONTO = "B";

    static final String MORE_CHAR = "+";

    static final float A_KILO = 1.e+3f;
    static final float A_MEGA = 1.e+6f;
    static final float A_GIGA = 1.e+9f;
    static final float A_TERA = 1.e+12f;
    static final float A_PETA = 1.e+15f;
    static final float A_EXA = 1.e+18f;
    static final float A_BRONTO = 1.e+21f;

    /**
     * 格式化数字，例如：1.3K+
     * @param number
     * @return
     */
    public static final String format(@NonNull double number) {
        DecimalFormat mFormat = new DecimalFormat("###,###,###,##0.0");

        if (number >= A_BRONTO) {
            return mFormat.format(number / A_BRONTO) + BRONTO + MORE_CHAR;
        } else if (number >= A_EXA) {
            return mFormat.format(number / A_EXA) + EXA + MORE_CHAR;
        } else if (number >= A_PETA) {
            return mFormat.format(number / A_PETA) + PETA + MORE_CHAR;
        } else if (number >= A_TERA) {
            return mFormat.format(number / A_TERA) + TERA + MORE_CHAR;
        } else if (number >= A_GIGA) {
            return mFormat.format(number / A_GIGA) + GIGA + MORE_CHAR;
        } else if (number >= A_MEGA) {
            return mFormat.format(number / A_MEGA) + MEGA + MORE_CHAR;
        } else if (number >= A_KILO) {
            return mFormat.format(number / A_KILO) + KILO + MORE_CHAR;
        }

        return String.valueOf(Math.round(number));
    }
}
