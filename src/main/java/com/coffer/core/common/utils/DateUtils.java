/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.coffer.core.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.coffer.businesses.modules.allocation.AllocationConstant;

/**
 * 日期工具类, 继承org.apache.commons.lang.time.DateUtils类
 * 
 * @author ThinkGem
 * @version 2014-4-15
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

	private static String[] parsePatterns = { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
			"yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM", "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss",
			"yyyy.MM.dd HH:mm", "yyyy.MM", "yyyyMMdd" };

	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd）
	 */
	public static String getDate() {
		return getDate("yyyy-MM-dd");
	}

	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String getDate(String pattern) {
		return DateFormatUtils.format(new Date(), pattern);
	}

	/**
	 * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String formatDate(Date date, Object... pattern) {

		if (date == null) {
			return "";
		}
		String formatDate = null;
		if (pattern != null && pattern.length > 0) {
			formatDate = DateFormatUtils.format(date, pattern[0].toString());
		} else {
			formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
		}
		return formatDate;
	}

	/**
	 * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String formatDateTime(Date date) {
		return formatDate(date, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * @author chengshu
	 * @version 2015/05/07
	 * 
	 * @Description 查询用日期格式化
	 * @param date
	 *            待转换日期
	 * @return 查询用日期
	 */
	public static String foramtSearchDate(Date date) {
		if (null == date) {
			return "";

		} else {
			return DateUtils.formatDate(date, AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS);
		}
	}

	/**
	 * 得到当前时间字符串 格式（HH:mm:ss）
	 */
	public static String getTime() {
		return formatDate(new Date(), "HH:mm:ss");
	}

	/**
	 * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String getDateTime() {
		return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm）
	 */
	public static String getDateTimeMin() {
		return formatDate(new Date(), "yyyy-MM-dd HH:mm");
	}

	/**
	 * 得到当前年份字符串 格式（yyyy）
	 */
	public static String getYear() {
		return formatDate(new Date(), "yyyy");
	}

	/**
	 * 得到当前月份字符串 格式（MM）
	 */
	public static String getMonth() {
		return formatDate(new Date(), "MM");
	}

	/**
	 * 得到当天字符串 格式（dd）
	 */
	public static String getDay() {
		return formatDate(new Date(), "dd");
	}

	/**
	 * 得到当前星期字符串 格式（E）星期几
	 */
	public static String getWeek() {
		return formatDate(new Date(), "E");
	}

	/**
	 * 日期型字符串转化为日期 格式 { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
	 * "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy.MM.dd",
	 * "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm" }
	 */
	public static Date parseDate(Object str) {
		if (str == null) {
			return null;
		}
		try {
			return parseDate(str.toString(), parsePatterns);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 获取过去的天数
	 * 
	 * @param date
	 * @return
	 */
	public static long pastDays(Date date) {
		long t = new Date().getTime() - date.getTime();
		return t / (24 * 60 * 60 * 1000);
	}

	/**
	 * 获取过去的小时
	 * 
	 * @param date
	 * @return
	 */
	public static long pastHour(Date date) {
		long t = new Date().getTime() - date.getTime();
		return t / (60 * 60 * 1000);
	}

	/**
	 * 获取过去的分钟
	 * 
	 * @param date
	 * @return
	 */
	public static long pastMinutes(Date date) {
		long t = new Date().getTime() - date.getTime();
		return t / (60 * 1000);
	}

	/**
	 * 转换为时间（天,时:分:秒.毫秒）
	 * 
	 * @param timeMillis
	 * @return
	 */
	public static String formatDateTime(long timeMillis) {
		long day = timeMillis / (24 * 60 * 60 * 1000);
		long hour = (timeMillis / (60 * 60 * 1000) - day * 24);
		long min = ((timeMillis / (60 * 1000)) - day * 24 * 60 - hour * 60);
		long s = (timeMillis / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		long sss = (timeMillis - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000 - min * 60 * 1000 - s * 1000);
		return (day > 0 ? day + "," : "") + hour + ":" + min + ":" + s + "." + sss;
	}

	/**
	 * 获取两个日期之间的天数
	 * 
	 * @param before
	 * @param after
	 * @return
	 */
	public static double getDistanceOfTwoDate(Date before, Date after) {
		long beforeTime = before.getTime();
		long afterTime = after.getTime();
		return (afterTime - beforeTime) / (1000 * 60 * 60 * 24);
	}

	/**
	 * @author Clark
	 * @date 2014年11月5日
	 * 
	 * @Description
	 * @param date
	 * @return
	 */
	public static Date getDateStart(Date date) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date = sdf.parse(formatDate(date, "yyyy-MM-dd") + " 00:00:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * @author Clark
	 * @date 2014年11月5日
	 * 
	 * @Description
	 * @param date
	 * @return
	 */
	public static Date getDateEnd(Date date) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date = sdf.parse(formatDate(date, "yyyy-MM-dd") + " 23:59:59");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 
	 * Title: getDateStartOrEnd
	 * <p>
	 * Description: 获取查询开始时间
	 * </p>
	 * 
	 * @author: lihe
	 * @param 得到传入时间的开始或结束时间,如:
	 *            patternStart:"yyyy-MM-dd HH:mm",则patternEnd:":00"或":59"
	 * @return Date 返回类型
	 */
	public static Date getDateStartOrEnd(Date date, String patternStart, String patternEnd) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date = sdf.parse(formatDate(date, patternStart) + patternEnd);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 
	 * Title: getLastMonthFromNow
	 * <p>
	 * Description: 获取距今时间的过去日期
	 * </p>
	 * 
	 * @param time:"week","month","month3","year"
	 * @author: lihe
	 * @return Date 返回类型 yyyy-MM-dd HH:mm:ss
	 */
	public static Date getLastMonthFromNow(String time) {
		Date date = new Date();
		if (StringUtils.isNotBlank(time)) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date); // 设置为当前时间
			switch (time) {
			case "week":
				calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 7); // 设置为过去七天
				break;
			case "month":
				calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1); // 设置为过去一个月
				break;
			case "month3":
				calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 3); // 设置为过去三个月
				break;
			case "year":
				calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1); // 设置为过去一年
				break;
			default:
				break;
			}
			date = calendar.getTime();
		}
		return getDateStart(date);
	}

	/**
	 * 
	 * Title: getDateStart
	 * <p>
	 * Description: 获取今年的第一天
	 * </p>
	 * 
	 * @date 2019年07月18日
	 * @author: lihe
	 * @param date
	 * @return Date 返回类型
	 */
	public static Date getYearStart(Date date) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date = sdf.parse(DateFormatUtils.format(date, "yyyy-01-01") + " 00:00:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * @param args
	 * @throws ParseException
	 */
	public static void main(String[] args) throws ParseException {
		// System.out.println(formatDate(parseDate("2010/3/6")));
		// System.out.println(getDate("yyyy年MM月dd日 E"));
		// long time = new Date().getTime()-parseDate("2012-11-19").getTime();
		// System.out.println(time/(24*60*60*1000));
	}

	/**
	 * 得到当前日期和时间字符串 格式（yyyyMMddHHmmssSSS）
	 */
	public static String getDateTimeAll() {
		return formatDate(new Date(), "yyyyMMddHHmmssSSS");
	}

	/**
	 * 得到当前日期和时间字符串 格式（yyyyMMddHHmmssSSSSSS）
	 */
	public static String getCurrentMillisecond() {
		return formatDate(new Date(), "yyyyMMddHHmmssSSSSSS");
	}

	/**
	 * 给指定日期增加天数
	 * 
	 * @param date
	 *            日期
	 * @param i
	 *            想要增加的天数
	 * @return
	 */
	public static Date addDate(Date date, int i) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, i);
		return cal.getTime();
	}

	/**
	 * 比较日期大小
	 * 
	 * @param date1
	 *            日期1
	 * @param date2
	 *            日期2
	 * @return -1:date1小/1:date2小/0:date1等于date2
	 */
	public static int compareDate(Date date1, Date date2) {

		// 全部是空，返回相等
		if (null == date1 && null == date2) {
			return 0;
		}

		// date1是空，返回date1小
		if (null == date1) {
			return -1;
		}

		// date2是空，返回date2小
		if (null == date2) {
			return 1;
		}

		// date1小于date2
		if (date1.getTime() < date2.getTime()) {
			return -1;

			// date1大于date2
		} else if (date1.getTime() > date2.getTime()) {
			return 1;

			// date1登记date2
		} else {
			return 0;
		}
	}

	/**
	 * 获取精确到秒的时间戳 （秒值）
	 * 
	 * @author qph 2017-05-04添加
	 * @param date
	 * 
	 * @return
	 */
	public static String getSecondTimestampTwo(Date date) {
		if (null == date) {
			return "0";
		}
		/*
		 * Calendar c = Calendar.getInstance(); String second=
		 * String.valueOf(c.get(Calendar.SECOND));
		 */
		String timestamp = String.valueOf(date.getTime() / 1000);
		return timestamp;
		// return second;
	}

	/**
	 * 两个时间相差距离多少天多少小时多少分多少秒
	 * 
	 * @author GJ 2020-03-06添加
	 * @param str1
	 *            时间参数 1 格式：1990-01-01 12:00:00
	 * @param str2
	 *            时间参数 2 格式：2009-01-01 12:00:00
	 * @return String 返回值为：xx天xx小时xx分xx秒
	 */
	public static String getDistanceTime(String str1, String str2) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date one;
		Date two;
		long day = 0;
		long hour = 0;
		long min = 0;
		long sec = 0;
		try {
			one = df.parse(str1);
			two = df.parse(str2);
			long time1 = one.getTime();
			long time2 = two.getTime();
			long diff;
			if (time1 < time2) {
				diff = time2 - time1;
			} else {
				diff = time1 - time2;
			}
			day = diff / (24 * 60 * 60 * 1000);
			hour = (diff / (60 * 60 * 1000) - day * 24);
			min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
			sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return day + "天" + hour + "小时" + min + "分" + sec + "秒";
	}
	
	/**
	 * 
	 * Title: getDateStart
	 * <p>
	 * Description: 获取今年的最后一天
	 * </p>
	 * 
	 * @date 2020年1月7日
	 * @author: WQJ
	 * @param date
	 * @return Date 返回类型
	 */
	public static Date getYearEnd(Date date) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date = sdf.parse(DateFormatUtils.format(date, "yyyy-12-31") + " 23:59:59");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 字符串时间戳转日期
	 *
	 * @param timestamp
	 * @return
	 * @author yinkai
	 */
	public static Date parseTimestampToDate(String timestamp) {
		if (timestamp == null || "".equals(timestamp)) {
			return null;
		}
		try {
			long tmp = Long.parseLong(timestamp);
			return new Date(tmp);
		} catch (NumberFormatException e) {
			return null;
		}

	}
}
