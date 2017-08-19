package com.call.log.infinity.utils;

/**
 * Created by Yong on 2017/2/12.
 */


/**
 * 将电话号码格式化，手机号变为344，座机电话号码变为433
 */
public class PhoneNumberFormatter {

    //默认分隔符是空格
    public static final String DELIMITER = " ";

    //号码长度少于最小长度将不处理
    private static final int MINIMUN_LENGTH = 6;
    //座机号码长度
    private static final int TELPHONE_LENGTH = 7;
    //标准电话号码长度
    private static final int NORMAL_LENGTH = 11;

    //某些地区的固话长度，比如杭州
    private static final int NORMAL_LENGTH_MIN = 10;

    //手机号码以1开头
    private static final String CELLPHONE_NUMBER_START = "1";

    //将号码格式化用到的截取位数
    private static final int THREE = 3;
    private static final int FOUR = 4;
    private static final int SIX = 6;
    private static final int SEVEN = 7;

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

        if (phoneNum.length() < MINIMUN_LENGTH) { //长度小于6位的号码直接返回
            return phoneNum;
        }

        StringBuilder sb = new StringBuilder();

        if (phoneNum.length() <= TELPHONE_LENGTH) { //座机号码，或者区域短号
            sb.append(phoneNum.substring(0,THREE))
                    .append(delimiter)
                    .append(phoneNum.substring(THREE, phoneNum.length()));
        }
        else if (phoneNum.length() == NORMAL_LENGTH_MIN) { //长度为10位的号码
            boolean formatDone = false;
            for (String areaCode : TelephoneAreaCodeUtil.THREE_DIGIT_AREA_CODE) {
                if (phoneNum.startsWith(areaCode)) { //3位区号固定电话，格式化：3 4 3
                    sb.append(phoneNum.substring(0, THREE)).append(delimiter)
                        .append(phoneNum.substring(THREE, SEVEN)).append(delimiter)
                        .append(phoneNum.substring(SEVEN, phoneNum.length()));
                    formatDone = true;
                    break;
                }
            }

            if (!formatDone) { //4位区号，格式化：4 3 3
                sb.append(phoneNum.substring(0, FOUR)).append(delimiter)
                    .append(phoneNum.substring(FOUR, SEVEN)).append(delimiter)
                    .append(phoneNum.substring(SEVEN, phoneNum.length()));
            }
        }
        else if (phoneNum.length() == NORMAL_LENGTH) { //长度为11位的号码 //标准手机号码，或者带区号的座机号码
            if (phoneNum.startsWith(CELLPHONE_NUMBER_START)) { //手机号码，格式化成：3 4 4
                sb.append(phoneNum.substring(0, THREE)).append(delimiter)
                        .append(phoneNum.substring(THREE, SEVEN)).append(delimiter)
                        .append(phoneNum.substring(SEVEN, phoneNum.length()));
            }
            else { //带区号的座机号码，格式化成：4 3 3 或 3 4 4
                boolean formatDone = false;
                for (String areaCode : TelephoneAreaCodeUtil.THREE_DIGIT_AREA_CODE) {
                    if (phoneNum.startsWith(areaCode)) { //3位区号固定电话，格式化：3 4 4
                        sb.append(phoneNum.substring(0, THREE)).append(delimiter)
                            .append(phoneNum.substring(THREE, SEVEN)).append(delimiter)
                            .append(phoneNum.substring(SEVEN, phoneNum.length()));
                        formatDone = true;
                        break;
                    }
                }

                if (!formatDone) { //4位区号，格式化：4 3 4
                    sb.append(phoneNum.substring(0, FOUR)).append(delimiter)
                        .append(phoneNum.substring(FOUR, SEVEN)).append(delimiter)
                        .append(phoneNum.substring(SEVEN, phoneNum.length()));
                }
            }
        }
        else { //其余长度的号码，格式为：4 4 4 ...
            int counts = phoneNum.length() / FOUR;

            int i;
            for (i = 0; i < counts - 1; i++) {
                sb.append(phoneNum.substring(FOUR*i, FOUR*(i+1))).append(delimiter);
            }

            if (FOUR*(i+1) == phoneNum.length()) {
                sb.append(phoneNum.substring(FOUR*i, FOUR*(i+1)));
            }
            else {
                sb.append(phoneNum.substring(FOUR*i, FOUR*(i+1))).append(delimiter);
                sb.append(phoneNum.substring(FOUR*(i+1), phoneNum.length()));
            }
        }

        return sb.toString();
    }
}
