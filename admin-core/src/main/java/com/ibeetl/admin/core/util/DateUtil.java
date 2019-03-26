package com.ibeetl.admin.core.util;

import org.apache.commons.lang3.time.FastDateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;

public class DateUtil {
    
    public static final Date MAX_DATE;

    private static final HashMap<String, FastDateFormat> formatStyle = new HashMap<>();

    static {
        LocalDateTime localDateTime = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
        MAX_DATE = localDateTimeToDateConverter(localDateTime);

    }

    static {
        formatStyle.put("yyyy-MM-dd",FastDateFormat.getInstance("yyyy-MM-dd"));
        formatStyle.put("yyyy-MM-dd HH:mm:ss",FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss"));
        formatStyle.put("yyyy年MM月dd日", FastDateFormat.getInstance("yyyy年MM月dd日"));
        formatStyle.put("yyyy年MM月dd日HH时mm分ss秒", FastDateFormat.getInstance("yyyy年MM月dd日HH时mm分ss秒"));
        formatStyle.put("/yyyy/MM/dd/",FastDateFormat.getInstance("/yyyy/MM/dd/"));
        formatStyle.put("yyyyMMddHHmmssSSS", FastDateFormat.getInstance("yyyyMMddHHmmssSSS"));
    }



    private static FastDateFormat fdfShort = formatStyle.get("yyyy-MM-dd");
    private static FastDateFormat fdfLong = formatStyle.get("yyyy-MM-dd HH:mm:ss");



    public static String now() {
        return fdfShort.format(new Date());
    }
    
    public static String now(String format) {
        if (formatStyle.containsKey(format)){
            return formatStyle.get(format).format(new Date());
        }else {
            return new SimpleDateFormat(format).format(new Date());
        }
    }

    public static final String L_FMT = "yyyyMMddHHmmssSSS";

    public static String getSystemTime(String format) {
        return formatStyle.get(format).format(new Date());
    }

    public static long getSystemTimeForLong() {
        return Long.parseLong(getSystemTime(L_FMT));
    }

    /**
     * 获取当前时间多少分钟后的时间
     * @param minutes
     * @return
     */
    public static Date getDateForMinutesLater(int minutes){
        long currentTime = System.currentTimeMillis();
        currentTime = currentTime+ minutes*60*1000;
        Date resultDate = new Date(currentTime);
        return resultDate;
    }


    public static Date parseStrToDate(String dateStr, String pattern) throws ParseException{
        FastDateFormat fastDateFormat = formatStyle.get(pattern);
        if (fastDateFormat == null){
            fastDateFormat = FastDateFormat.getInstance(pattern);
        }
        return  fastDateFormat.parse(dateStr);
    }


    //一些转换类
    private static Date localDateTimeToDateConverter(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private static LocalDateTime DateToLocalDateTimeConverter(Date date){
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }


}
