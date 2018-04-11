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
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/**
 * 时区转换
 * 
 * @since 2.0
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
public class DateUtils {
	public static String formatyyyyMMddHHmmss(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}

	public static String formatyyyy_MM_dd(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}

	public static String formatyyyyMM(Date date) {
		return new SimpleDateFormat("yyyyMM").format(date);
	}

	public static String formatyyyyMMdd(Date date) {
		return new SimpleDateFormat("yyyyMMdd").format(date);
	}

	public static String formatyyyyMMdd(LocalDate date) {
		return DateTimeFormatter.ofPattern("yyyyMMdd").format(date);
	}

	public static Date parseyyyyMMddHHmmss(String source) throws ParseException {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(source);
	}

	public static Date parseyyyyMMdd(String source) throws ParseException {
		return new SimpleDateFormat("yyyy-MM-dd").parse(source);
	}

	public static String formatyyyy_MM_dd(LocalDate date) {
		return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(date);
	}

	/**
	 * 这方法名应该是写反了
	 */
	public static Date cvtToGmt(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		format.setCalendar(new GregorianCalendar(new SimpleTimeZone(0, "GMT")));
		try {
			date = format.parse(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
		} catch (Exception e) {}
		return date;
		// return new Date(date.getTime() +
		// TimeZone.getDefault().getRawOffset());
	}

	/**
	 * 所以我也就反着来了
	 */
	public static Date gmtToCvt(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("GMT")); // 设置时区为GMT
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return sdf.parse(format.format(date));
		} catch (ParseException e) {
			throw new RuntimeException("DateUtils.gmtToCvt()");
		}
	}

	public static Date calStartTime(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return format.parse(format.format(date));
		} catch (ParseException e) {
			throw new RuntimeException("DateUtils.calStartTime()");
		}
	}

	public static Date calEndTime(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(format.format(date) + " 23:59:59");
		} catch (ParseException e) {
			throw new RuntimeException("DateUtils.calStartTime()");
		}
	}

	/**
	 * 计算每个月的开始时间.
	 */
	public static Date calFirstDay(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		try {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(format.format(date) + "-01 00:00:00");
		} catch (ParseException e) {
			throw new RuntimeException("DateUtils.calFirstDay()");
		}
	}

	/**
	 * 计算每个月的结束时间.
	 */
	public static Date calLastDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		final int lastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		try {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(format.format(date) + "-" + lastDay + " 23:59:59");
		} catch (ParseException e) {
			throw new RuntimeException("DateUtils.calLastDay()");
		}
	}

	public static Date addMonth(Date today, int month) {
		Calendar c = Calendar.getInstance();
		c.setTime(today);
		c.add(Calendar.MONTH, month);
		return c.getTime();
	}

	public static Date addDay(Date today, int day) {
		Calendar c = Calendar.getInstance();
		c.setTime(today);
		c.set(Calendar.DATE, c.get(Calendar.DATE) + day);
		return c.getTime();
	}

	public static LocalDate toLocalDate(Date date) {
		Instant instant = date.toInstant();
		ZoneId zone = ZoneId.systemDefault();
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
		return localDateTime.toLocalDate();
	}

	public static void main(String[] args) {
		Date today = new Date();
		System.out.println(calFirstDay(today));
		System.out.println(calLastDay(today));
	}
}