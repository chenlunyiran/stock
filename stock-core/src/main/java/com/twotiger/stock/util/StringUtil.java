package com.twotiger.stock.util;

import java.math.BigDecimal;

/**
 * 
 * @Description: String工具类
 * @Author hansc
 * @Date 2014年9月11日 下午7:13:58
 */
public class StringUtil {

    /**
     * 判断当前对象是否为null
     * 若为string 并判断是否“”
     * 
     * @param obj
     * @return
     */
    public static boolean isBlank(Object obj) {
        if (obj == null || (obj instanceof String && "".equals((String) obj)))
            return true;
        return false;
    }

    /**
     * 判断字符是否为 NULL 或者 ""
     * 
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        return (null == str || "".equals(str.trim())) ? true : false;
    }

    /**
     * 格式化空
     * 
     * @param str
     * @return
     */
    public static String fmtNull(String str) {
        return null == str ? "" : str;
    }

    /**
     * 是否符合规范数字
     * 1.是否为空
     * 2.是否为数字
     * 3.是否超过最大值:2147483647
     * 
     * @param str
     * @return
     */
    public static boolean isNum(String str) {
        if (isBlank(str)) {
            return false;
        }

        if (!str.matches("[0-9]+")) {
            return false;
        }

        BigDecimal num = new BigDecimal(str);
        BigDecimal intMax = new BigDecimal(Integer.MAX_VALUE);
        if (num.compareTo(intMax) == 1) {
            return false;
        }

        return true;
    }

    /**
     * 是否符合规范long类型数字
     * 1.是否为空
     * 2.是否为数字
     * 
     * @param str
     * @return
     */
    public static boolean isLongNum(String str) {
        if (isBlank(str)) {
            return false;
        }

        if (!str.matches("[0-9]+")) {
            return false;
        }

        return true;
    }

    /**
     * 马赛克
     * @param str
     * @return
     */
    public static String maskMobile(String str) {
        return str.substring(0, 3) + "****" + str.substring(7);
    }

    /**
     * 统计某个字符串出现的个数
     * 
     * @param str
     * @param key
     * @return
     */
    public static int getSubString(String str, String key) {
        int count = 0;
        int index = 0;
        while ((index = str.indexOf(key, index)) != -1) {
            index = index + key.length();
            count++;
        }
        return count;
    }

}
