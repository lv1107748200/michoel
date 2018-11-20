package fm.qian.michael.utils;

import com.xxbm.sbecomlibrary.utils.CheckUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 * Created by bob on 2015/2/28.
 */
public class DateUtils {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_Y_M_D = "yyyy-M-dd";
    public static final String DATE_FORMAT_Y_MM_D = "yyyy-MM-dd";
    public static final String DATE_FORMAT_Y_M_D_H_M = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT_TILL_DAY_CURRENT_YEAR = "yyyy";
    public static final String DATE_FORMAT_TILL_DAY_CH = "MM-dd";
    public static final String DATE_FORMAT_YUE_RI = "MM月dd日";
    public static final String TURN_DATE_FORMAT_Y_M_D = "yyyy/M/dd";
    public static final String TURN_DATE_FORMAT_M_D_Y = "M/dd/yyyy";


    /**
     * 得到当前时间
     *
     * @param dateFormat 时间格式
     * @return 转换后的时间格式
     */
    public static String getStringToday(String dateFormat) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public static long getStringTodayLong() {
        Date currentTime = new Date();
        return currentTime.getTime();
    }

    /**
     * 得到当前时间戳
     *
     * @return 转换后的时间格式
     */
    public static String getStringToday() {
        Date currentTime = new Date();
        String dateString = String.valueOf(currentTime.getTime());
        return dateString;
    }

    /**
     * 将字符串型日期转换成日期
     *
     * @param dateStr    字符串型日期
     * @param dateFormat 日期格式
     * @return
     */
    public static Date stringToDate(String dateStr, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        try {
            return formatter.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Date日期转字符串
     * 2017-07-20 11:18:42
     *
     * @param date
     * @param dateFormat
     * @return
     */
    public static String dateToString(Date date, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(date);
    }

    /**
     * @param time
     * @param dateFormat
     * @return
     */
    public static String dateToStringToCw(String time, String dateFormat) {
        if(!CheckUtil.isEmpty(time)){
            return String.valueOf(stringToDate(time,dateFormat).getTime());
        }else {
           return "";
        }
    }

    /*
 * 将时间戳转换为时间
 */
    public static String stampToDate(String s, String f) {
        if (CheckUtil.isEmpty(s))
            return "";
        String res;
        long lt = new Long(s);
        Date date = new Date(lt);
        res = dateToString(date, f);
        return res;
    }

    public static String stampToDate(long s, String f) {
        String res;
        Date date = new Date(s);
        res = dateToString(date, f);
        return res;
    }

    /**
     * 两个时间点的间隔时长（分钟）
     *
     * @param before 开始时间
     * @param after  结束时间
     * @return 两个时间点的间隔时长（分钟）
     */
    public static long compareMin(Date before, Date after) {
        if (before == null || after == null) {
            return 0l;
        }
        long dif = 0;
        if (after.getTime() >= before.getTime()) {
            dif = after.getTime() - before.getTime();
        } else if (after.getTime() < before.getTime()) {
            dif = after.getTime() + 86400000 - before.getTime();
        }
        dif = Math.abs(dif);
        return dif / 60000;
    }

    /**
     * 获取指定时间间隔分钟后的时间
     *
     * @param date 指定的时间
     * @param min  间隔分钟数
     * @return 间隔分钟数后的时间
     */
    public static Date addMinutes(Date date, int min) {
        if (date == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, min);
        return calendar.getTime();
    }

    /**
     * 根据时间返回指定术语，自娱自乐，可自行调整
     *
     * @param hourday 小时
     * @return
     */
    public static String showTimeView(int hourday) {
        if (hourday >= 22 && hourday <= 24) {
            return "晚上";
        } else if (hourday >= 0 && hourday <= 6) {
            return "凌晨";
        } else if (hourday > 6 && hourday <= 12) {
            return "上午";
        } else if (hourday > 12 && hourday < 22) {
            return "下午";
        }
        return null;
    }

    //根据时间戳来判断
    public static String timeStamp(String dateStr) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.get(Calendar.DAY_OF_MONTH);
            long now = calendar.getTimeInMillis();
            Date date = new Date(new Long(dateStr));
            calendar.setTime(date);
            long past = calendar.getTimeInMillis();

            // 相差的秒数
            long time = (now - past) / 1000;

            StringBuffer sb = new StringBuffer();
            if (time > 0 && time < 60) { // 1小时内
                return sb.append(time + "秒前").toString();
            } else if (time > 60 && time < 3600) {
                return sb.append(time / 60 + "分钟前").toString();
            } else if (time >= 3600 && time < 3600 * 24) {
                return sb.append(time / 3600 + "小时前").toString();
            } else if (time >= 3600 * 24 && time < 3600 * 48) {
                return sb.append("昨天").toString();
            } else if (time >= 3600 * 48 && time < 3600 * 72) {
                return sb.append("前天").toString();
            } else if (time >= 3600 * 72) {
                return stampToDate(dateStr, DATE_FORMAT);
            }
            return stampToDate(dateStr, DATE_FORMAT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    //根据日期（2017-07-20 11:25:11） 来判断时间
    public static String timeLogic(String dateStr) {
        Calendar calendar = Calendar.getInstance();
        calendar.get(Calendar.DAY_OF_MONTH);
        long now = calendar.getTimeInMillis();
        Date date = stringToDate(dateStr, DATE_FORMAT);
        calendar.setTime(date);
        long past = calendar.getTimeInMillis();

        // 相差的秒数
        long time = (now - past) / 1000;

        StringBuffer sb = new StringBuffer();
        if (time > 0 && time < 60) { // 1小时内
            return sb.append(time + "秒前").toString();
        } else if (time > 60 && time < 3600) {
            return sb.append(time / 60 + "分钟前").toString();
        } else if (time >= 3600 && time < 3600 * 24) {
            return sb.append(time / 3600 + "小时前").toString();
        } else if (time >= 3600 * 24 && time < 3600 * 48) {
            return sb.append("昨天").toString();
        } else if (time >= 3600 * 48 && time < 3600 * 72) {
            return sb.append("前天").toString();
        } else if (time >= 3600 * 72) {
            return dateToString(dateStr, DATE_FORMAT);
        }
        return dateToString(dateStr, DATE_FORMAT);
    }

    public static String dateToString(String timeStr, String format) {
        // 判断是否是今年
        Date date = stringToDate(timeStr, format);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // 如果是今年的话，才去“xx月xx日”日期格式
        if (calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
            return dateToString(date, DATE_FORMAT_TILL_DAY_CURRENT_YEAR);
        }
        return dateToString(date, DATE_FORMAT_TILL_DAY_CH);
    }

    // 获取去年的年份
    public static int getLastYear() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -1);
        return c.get(Calendar.YEAR);
    }

    // 获取下一年年份
    public static int getNextYear() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, 1);
        return c.get(Calendar.YEAR);
    }

    // 获取上一个月的月份
    public static int getLastMonth() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        return c.get(Calendar.MONTH) + 1;
    }

    // 获取下一个月的月份
    public static int getNextMonth() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 1);
        return c.get(Calendar.MONTH) + 1;
    }

}
