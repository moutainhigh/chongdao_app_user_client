package com.chongdao.client.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Description TODO
 * @Author onlineS
 * @Date 2019/7/10
 * @Version 1.0
 **/
public class StatisticalDateUtil {
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        System.out.println("当天24点时间：" + getTimesNight().toLocaleString());
        System.out.println("当前时间：" + new Date().toLocaleString());
        System.out.println("当天0点时间：" + getTimesMorning().toLocaleString());
        System.out.println("昨天0点时间：" + getYesterdayMorning().toLocaleString());
        System.out.println("近7天时间：" + getWeekFromNow().toLocaleString());
        System.out.println("本周周一0点时间：" + getTimesWeekMorning().toLocaleString());
        System.out.println("本周周二0点时间: " + getTimesTuesdayMorning().toLocaleString());
        System.out.println("本周周日0点时间: " + getTimesSunMorning().toLocaleString());
        System.out.println("本周六0点时间: " + getTimesSaturdayMorning().toLocaleString()) ;
        System.out.println("本周周日24点时间：" + getTimesWeekNight().toLocaleString());
        System.out.println("本月初0点时间：" + getTimesMonthMorning().toLocaleString());
        System.out.println("本月未24点时间：" + getTimesMonthNight().toLocaleString());
        System.out.println("上月初0点时间：" + getLastMonthStartMorning().toLocaleString());
        System.out.println("本季度开始点时间：" + getCurrentQuarterStartTime().toLocaleString());
        System.out.println("本季度结束点时间：" + getCurrentQuarterEndTime().toLocaleString());
        System.out.println("本年开始点时间：" + getCurrentYearStartTime().toLocaleString());
        System.out.println("本年结束点时间：" + getCurrentYearEndTime().toLocaleString());
        System.out.println("上年开始点时间：" + getLastYearStartTime().toLocaleString());
    }

    /**
     * 获得当天0点时间
     * @return
     */
    public static Date getTimesMorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }


    /**
     * 获得当天24点时间
     * @return
     */
    public static Date getTimesNight() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获得昨天0点时间
     * @return
     */
    public static Date getYesterdayMorning() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getTimesMorning().getTime() - 3600 * 24 * 1000);
        return cal.getTime();
    }

    /**
     * 获得当天近7天时间
     * @return
     */
    public static Date getWeekFromNow() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getTimesMorning().getTime() - 3600 * 24 * 1000 * 7);
        return cal.getTime();
    }

    /**
     * 获得本周一0点时间
     * @return
     */
    public static Date getTimesWeekMorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime();
    }

    public static Date getTimesTuesdayMorning() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getTimesWeekMorning());
        cal.add(Calendar.DAY_OF_WEEK, 1);
        return cal.getTime();
    }

    public static Date getTimesWednesdayMorning() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getTimesWeekMorning());
        cal.add(Calendar.DAY_OF_WEEK, 2);
        return cal.getTime();
    }

    public static Date getTimesThursdayMorning() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getTimesWeekMorning());
        cal.add(Calendar.DAY_OF_WEEK, 3);
        return cal.getTime();
    }

    public static Date getTimesFridayMorning() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getTimesWeekMorning());
        cal.add(Calendar.DAY_OF_WEEK, 4);
        return cal.getTime();
    }

    public static Date getTimesSaturdayMorning() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getTimesWeekMorning());
        cal.add(Calendar.DAY_OF_WEEK, 5);
        return cal.getTime();
    }

    public static Date getTimesSunMorning() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getTimesWeekMorning());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        return cal.getTime();
    }

    /**
     * 获得本周日24点时间
     * @return
     */
    public static Date getTimesWeekNight() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getTimesWeekMorning());
        cal.add(Calendar.DAY_OF_WEEK, 7);
        return cal.getTime();
    }

    /**
     * 获得本月第一天0点时间
     * @return
     */
    public static Date getTimesMonthMorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    /**
     * 获得本月最后一天24点时间
     * @return
     */
    public static Date getTimesMonthNight() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 24);
        return cal.getTime();
    }

    public static Date getLastMonthStartMorning() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getTimesMonthMorning());
        cal.add(Calendar.MONTH, -1);
        return cal.getTime();
    }

    /**
     * 获取当前季度的开始时间
     * @return
     */
    public static Date getCurrentQuarterStartTime() {
        Calendar c = Calendar.getInstance();
        int currentMonth = c.get(Calendar.MONTH) + 1;
        SimpleDateFormat longSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
        Date now = null;
        try {
            if (currentMonth >= 1 && currentMonth <= 3)
                c.set(Calendar.MONTH, 0);
            else if (currentMonth >= 4 && currentMonth <= 6)
                c.set(Calendar.MONTH, 3);
            else if (currentMonth >= 7 && currentMonth <= 9)
                c.set(Calendar.MONTH, 4);
            else if (currentMonth >= 10 && currentMonth <= 12)
                c.set(Calendar.MONTH, 9);
            c.set(Calendar.DATE, 1);
            now = longSdf.parse(shortSdf.format(c.getTime()) + " 00:00:00");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }

    /**
     * 当前季度的结束时间，即2012-03-31 23:59:59
     *
     * @return
     */
    public static Date getCurrentQuarterEndTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getCurrentQuarterStartTime());
        cal.add(Calendar.MONTH, 3);
        return cal.getTime();
    }

    /**
     * 获取当前年度开始时间
     * @return
     */
    public static Date getCurrentYearStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.YEAR));
        return cal.getTime();
    }

    /**
     * 获取当前年度结束时间
     * @return
     */
    public static Date getCurrentYearEndTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getCurrentYearStartTime());
        cal.add(Calendar.YEAR, 1);
        return cal.getTime();
    }

    /**
     * 获取上一年度开始时间
     * @return
     */
    public static Date getLastYearStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getCurrentYearStartTime());
        cal.add(Calendar.YEAR, -1);
        return cal.getTime();
    }
}
