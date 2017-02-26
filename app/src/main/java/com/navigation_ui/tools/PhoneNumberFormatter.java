package com.navigation_ui.tools;

/**
 * Created by Yong on 2017/2/12.
 */


/**
 * 将电话号码格式化，手机号变为344，座机电话号码变为433
 */
public class PhoneNumberFormatter {

    //默认分隔符是空格
    public static final String DELIMITER = " ";

    /**
     * 默认分隔符是空格
     * @param number
     * @return
     */
    public static String phoneNumberFormat(String number) {
        return format(number, " ");
    }

    /**
     * 添加设置的分隔符，将电话号码格式化
     * @param phoneNum
     * @param delimiter
     * @return
     */
    private static String format(String phoneNum, String delimiter) {

        if (phoneNum.length() < 6) { //长度小于6位的号码直接返回
            return phoneNum;
        }

        StringBuilder sb = new StringBuilder();

        if (phoneNum.length() <= 7) { //座机号码，或者区域短号
            sb.append(phoneNum.substring(0,3))
                    .append(delimiter)
                    .append(phoneNum.substring(3,phoneNum.length()));
        }
        else if (phoneNum.length() == 11) { //标准手机号码，或者带区号的座机号码
            if (phoneNum.startsWith("1")) { //手机号码，格式化成：3 4 4
                sb.append(phoneNum.substring(0,3)).append(delimiter)
                        .append(phoneNum.substring(3,7)).append(delimiter)
                        .append(phoneNum.substring(7,phoneNum.length()));
            }
            else { //带区号的座机号码，格式化成：4 3 3
                sb.append(phoneNum.substring(0,4)).append(delimiter)
                        .append(phoneNum.substring(4,7)).append(delimiter)
                        .append(phoneNum.substring(7,phoneNum.length()));
            }
        }
        else { //其余长度的号码
            int counts = phoneNum.length() / 4;

            int i;
            for (i = 0; i < counts - 1; i++) {
                sb.append(phoneNum.substring(4*i, 4*(i+1))).append(delimiter);
            }

            if (4*(i+1) == phoneNum.length()) {
                sb.append(phoneNum.substring(4*i, 4*(i+1)));
            }
            else {
                sb.append(phoneNum.substring(4*i, 4*(i+1))).append(delimiter);
                sb.append(phoneNum.substring(4*(i+1), phoneNum.length()));
            }
        }

        return sb.toString();
    }
}
