/*
 * Copyright © 2016 qeng.cn All Rights Reserved.
 * 
 * 感谢您加入清源科技，不用多久，您就会升职加薪、当上总经理、出任CEO、迎娶白富美、从此走上人生巅峰
 * 除非符合本公司的商业许可协议，否则不得使用或传播此源码，您可以下载许可协议文件：
 * 
 * 		http://www.noark.xyz/qeng/LICENSE
 *
 * 1、未经许可，任何公司及个人不得以任何方式或理由来修改、使用或传播此源码;
 * 2、禁止在本源码或其他相关源码的基础上发展任何派生版本、修改版本或第三方版本;
 * 3、无论你对源代码做出任何修改和优化，版权都归清源科技所有，我们将保留所有权利;
 * 4、凡侵犯清源科技相关版权或著作权等知识产权者，必依法追究其法律责任，特此郑重法律声明！
 */
package cn.qeng.gm.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日历：周的工具方法 设定：每周一为一周的第一天
 *
 * @author 任成龙(chenglong.ren@qeng.net)
 */
public class WeekUtils {
	public static void main(String[] args) throws Exception {

		// System.out.println("开始时间: " + getStartDayOfWeekNo(2016, 1));
		// System.out.println("结束时间:" + getEndDayOfWeekNo(2016, 1));
		// System.out.println(getWeekString(new Date()));
//		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
//		System.out.println(sdf1.format(getEndTimeInWeek(sdf2.parse("2017-09-26"))));
//		System.out.println(sdf1.format(getStartTimeInWeek(sdf2.parse("2017-09-23"))));
	}

	public static String getWeekString(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		System.out.println(calendar.getTime());
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		int week = calendar.get(Calendar.WEEK_OF_YEAR);
		if (week == 1 && calendar.get(Calendar.MONTH) == 11) {
			return (calendar.get(Calendar.YEAR) + 1) + String.format("%02d", week);
		}
		return calendar.get(Calendar.YEAR) + String.format("%02d", week);
	}

	public static Date getStartTimeInWeek(Date date) {
		try {
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.setFirstDayOfWeek(Calendar.MONDAY);
			int day = calendar.get(Calendar.DAY_OF_WEEK);
			if(day == 1){
				calendar.add(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() - day - 7);
			}else{
				calendar.add(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() - day);
			}
			return sdf2.parse(sdf2.format(calendar.getTime()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Date getEndTimeInWeek(Date date) {
		try {
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.setFirstDayOfWeek(Calendar.MONDAY);
			int day = calendar.get(Calendar.DAY_OF_WEEK);
			if(day == 1){
				calendar.add(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() - day - 7 + 6);
			}else{
				calendar.add(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() - day + 6);
			}
			return sdf1.parse(sdf2.format(calendar.getTime()) + " 23:59:59");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * get first date of given month and year
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public String getFirstDayOfMonth(int year, int month) {
		String monthStr = month < 10 ? "0" + month : String.valueOf(month);
		return year + "-" + monthStr + "-" + "01";
	}

	/**
	 * get the last date of given month and year
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public String getLastDayOfMonth(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DATE, 1);
		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		return calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * get Calendar of given year
	 * 
	 * @param year
	 * @return
	 */
	private static Calendar getCalendarFormYear(int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		cal.set(Calendar.YEAR, year);
		return cal;
	}

	/**
	 * get start date of given week no of a year
	 * 
	 * @param year
	 * @param weekNo
	 * @return
	 */
	public static String getStartDayOfWeekNo(int year, int weekNo) {
		Calendar cal = getCalendarFormYear(year);
		cal.set(Calendar.WEEK_OF_YEAR, weekNo);
		return cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH);

	}

	/**
	 * get the end day of given week no of a year.
	 * 
	 * @param year
	 * @param weekNo
	 * @return
	 */
	public static String getEndDayOfWeekNo(int year, int weekNo) {
		Calendar cal = getCalendarFormYear(year);
		cal.set(Calendar.WEEK_OF_YEAR, weekNo);
		cal.add(Calendar.DAY_OF_WEEK, 6);
		return cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH);
	}
}
