package io.github.shshdxk.common;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.validator.routines.DateValidator;

import java.text.ParseException;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class TimeUtils {


    private static final String[] DATE_FORMATS = new String[] {
            "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ssX", "yyyy-MM-dd'T'HH:mm:ssXX", "yyyy-MM-dd'T'HH:mm:ssZZ",
            "yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd'T'HH:mm:ss.SSSX", "yyyy-MM-dd'T'HH:mm:ss.SSSXX", "yyyy-MM-dd'T'HH:mm:ss.SSSZZ",
            "yyyy-MM-dd", "yyyy-MM-ddX", "yyyy-MM-ddXX", "yyyy-MM-ddZZ", "yyyyMMddX", "yyyyMMddXX", "yyyyMMddZZ",
            "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd'T'HH:mm:ssX", "yyyy/MM/dd'T'HH:mm:ssXX", "yyyy/MM/dd'T'HH:mm:ssZZ",
            "yyyy/MM/dd HH:mm:ss.SSS", "yyyy/MM/dd'T'HH:mm:ss.SSSX", "yyyy/MM/dd'T'HH:mm:ss.SSSXX", "yyyy/MM/dd'T'HH:mm:ss.SSSZZ",
            "yyyy/MM/dd", "yyyy/MM/ddX", "yyyy/MM/ddXX", "yyyy/MM/ddZZ", "yyyyMMddHHmmss", "yyyy-MM", "yyyy", "yyyyMMdd", "yyyyMMddHHmm",
            "yyyy-MM-dd hh:mmaa", "yyyy-MM-dd hh:mm:ssaa", "MM dd yyyy hh:mmaa"};
    private static final String TIME_STAMP_FORMAT = "^\\d+$";

    @Getter
    public enum TimeUnit {
        DAY(86400000),
        HOUR(3600000),
        MINUTE(60000),
        SECOND(1000);

        private final long millisecond;

        TimeUnit(long millisecond) {
            this.millisecond = millisecond;
        }

    }

    /**
     * 时间转换成字符串
     * @param date Date
     * @param format 时间格式
     * @return String
     */
    public static String dateToString(Date date, String format) {
        return DateFormatUtils.format(date, format);
    }

    /**
     * 计算年龄 (年)
     * @param birthDay 生日
     * @return 年龄
     */
    public static int age(Date birthDay) {
        LocalDateTime beforeLT = date2LocalDateTime(birthDay);
        LocalDateTime afterLT = getNowLocalDateTime();
        return age(beforeLT, afterLT);
    }

    /**
     * 计算年龄 (年)
     * @param birthDay 生日
     * @return 年龄
     */
    public static int age(LocalDateTime birthDay) {
        LocalDateTime afterLT = getNowLocalDateTime();
        return age(birthDay, afterLT);
    }

    /**
     * 计算年龄 (年)
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 年龄
     */
    public static int age(Date startDate, Date endDate) {
        LocalDateTime beforeLT = date2LocalDateTime(startDate);
        LocalDateTime afterLT = date2LocalDateTime(endDate);
        return age(beforeLT, afterLT);
    }

    /**
     * 计算年龄 (年)
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 年龄
     */
    public static int age(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            return age(endDate, startDate);
        }

        int age = endDate.getYear() - startDate.getYear() - 1;
        Queue<Integer> before = Queues.newLinkedBlockingQueue();
        before.add(startDate.getMonthValue());
        before.add(startDate.getDayOfMonth());
        before.add(startDate.getHour());
        before.add(startDate.getMinute());
        before.add(startDate.getSecond());

        Queue<Integer> after = Queues.newLinkedBlockingQueue();
        after.add(endDate.getMonthValue());
        after.add(endDate.getDayOfMonth());
        after.add(endDate.getHour());
        after.add(endDate.getMinute());
        after.add(endDate.getSecond());

        age += one(before, after);
        return age;
    }

    /**
     * 获取当前时间
     * @return 当前时间
     */
    public static LocalDateTime getNowLocalDateTime() {
        return LocalDateTime.now();
    }

    /**
     * 计算时间差
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param unit 时间单位
     * @return 时间差
     */
    public static int intervalTime(Date startDate, Date endDate, TimeUnit unit) {
        if (startDate.compareTo(endDate) > 0) {
            return intervalTime(endDate, startDate, unit);
        }

        return (int) ((endDate.getTime() - startDate.getTime()) / unit.getMillisecond());
    }

    /**
     * 判断前一个集合是否比后一个集合大, 如果前一个集合大, 返回1, 否则返回0
     * @param before 前一个集合
     * @param after 后一个集合
     * @return 1 or 0
     */
    private static int one(Queue<Integer> before, Queue<Integer> after) {
        if (before.size() != after.size()) {
            return 0;
        }
        if (before.isEmpty()) {
            return 1;
        }
        int a = after.poll();
        int b = before.poll();
        if (a > b) {
            return 1;
        } else if (a == b) {
            return one(before, after);
        } else {
            return 0;
        }
    }

    /**
     * 时间转换成LocalDateTime
     * @param date 时间
     * @return 时间
     */
    public static LocalDateTime date2LocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault())
            .toLocalDateTime();
    }

    /**
     * 时间转换成LocalDate
     * @param date 时间
     * @return 日期
     */
    public static LocalDate date2LocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault())
            .toLocalDate();
    }

    /**
     * 时间转换成Date
     * @param localDateTime 时间
     * @return 时间
     */
    public static Date localDateTime2Date(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 判断两个时间段是否有交集, e1 > s2 && s1 < e2
     *
     * @param s1  第一段事件的开始时间
     * @param e1  第一段事件的结束时间
     * @param ses 第二段事件的开始和结束时间
     * @return true or false
     */
    public static boolean timeIsIntersection(Date s1, Date e1, List<Pair<Date, Date>> ses) {
        for (Pair<Date, Date> se : ses) {
            if (e1.getTime() > se.getLeft().getTime() && s1.getTime() < se.getRight().getTime()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断两个时间段是否有交集, e1 > s2 && s1 < e2
     *
     * @param s1  第一段事件的开始时间
     * @param e1  第一段事件的结束时间
     * @param s2  第二段事件的开始时间
     * @param e2  第二段事件的结束时间
     * @return true or false
     */
    public static boolean timeIsIntersection(Date s1, Date e1, Date s2, Date e2) {
        return e1.getTime() > s2.getTime() && s1.getTime() < e2.getTime();
    }

    /**
     * 判断当前时间是否在开始和结束时间所在天范围内
     * start = 2020-12-12 08:00:00, end = 2020-12-12 09:00:00,
     * 2020-12-12 02:00:00 = true,
     * 2020-12-12 18:00:00 = true
     * @param start 开始时间
     * @param end 结束时间
     * @return true or false
     */
    public static boolean isInDays(Date start, Date end) {
        long startTime = DateUtils.truncate(start, Calendar.DAY_OF_MONTH).getTime();
        long endTime = DateUtils.truncate(end, Calendar.DAY_OF_MONTH).getTime() + 3600000 * 24;
        long now = System.currentTimeMillis();
        return now >= startTime && now <= endTime;
    }

    /**
     * 获取时间段内所有天
     * @param start 开始时间
     * @param end 结束时间
     * @return 天
     */
    public static List<String> getDays(Date start, Date end) {
        List<String> days = Lists.newArrayList();
        long dayTime = start.getTime();
        long endTime = end.getTime();
        while (dayTime < endTime) {
            days.add(DateFormatUtils.format(new Date(dayTime), "yyyy-MM-dd"));
            dayTime += 3600000 * 24;
        }
        return days;
    }

    /**
     * 获取周几
     * @param date 时间
     * @return 周几
     */
    public static String getWeek(Date date) {
        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekDays[w];
    }

    /**
     * 获得某天最大时间 2020-02-19 xx to 2020-02-20 00:00:00
     * @param date 时间
     * @return 时间
     */
    public static Date getEndOfDay(Date date) {
        return DateUtils.addDays(getStartOfDay(date), 1);
    }

    /**
     * 获得某天最小时间 2020-02-17 00:00:00
     * @param date 时间
     * @return 时间
     */
    public static Date getStartOfDay(Date date) {
        return DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取某天所在周的最大时间
     * @param date 时间
     * @return 时间
     */
    public static Date getEndOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int w = calendar.get(Calendar.DAY_OF_WEEK);
        if (w != 1) {
            calendar.setTime(new Date(date.getTime() + 604800000));
        }
        calendar.set(Calendar.DAY_OF_WEEK, 1);
        return getEndOfDay(calendar.getTime());
    }

    /**
     * 获取某天所在周的最小时间
     * @param date 时间
     * @return 时间
     */
    public static Date getStartOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int w = calendar.get(Calendar.DAY_OF_WEEK);
        if (w == 1) {
            calendar.setTime(new Date(date.getTime() - 604800000));
        }
        calendar.set(Calendar.DAY_OF_WEEK, 2);
        return getStartOfDay(calendar.getTime());
    }


    /**
     * 获取某天所在月的最大时间
     * @param date 时间
     * @return 时间
     */
    public static Date getEndOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        return getStartOfDay(calendar.getTime());
    }

    /**
     * 获取某天所在月的最小时间
     * @param date 时间
     * @return 时间
     */
    public static Date getStartOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return getStartOfDay(calendar.getTime());
    }


    /**
     * 获取某天所在年的最大时间
     * @param date 时间
     * @return 时间
     */
    public static Date getEndOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
        return getStartOfDay(calendar.getTime());
    }

    /**
     * 获取某天所在年的最小时间
     * @param date 时间
     * @return 时间
     */
    public static Date getStartOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        return getStartOfDay(calendar.getTime());
    }

    /**
     * 获取明天的日期
     * @param date 时间
     * @return 时间
     */
    public static Date getTomorrowDate(LocalDate date) {
        ZonedDateTime zonedDateTime = date.atStartOfDay(ZoneId.systemDefault());
        Instant instant = zonedDateTime.toInstant();
        Date from = Date.from(instant);
        return DateUtils.addDays(from, 1);
    }

    /**
     * 获取昨天的日期
     * @param date 时间
     * @return 时间
     */
    public static Date getYesterdayDate(LocalDate date) {
        ZonedDateTime zonedDateTime = date.atStartOfDay(ZoneId.systemDefault());
        Instant instant = zonedDateTime.toInstant();
        Date from = Date.from(instant);
        return DateUtils.addDays(from, -1);
    }

    /**
     * 获取指定日期的开始时间
     * @param date 时间
     * @param dateUnit 时间单位
     * @return 时间
     */
    public static Date getStartDate(Date date, String dateUnit) {
        if (date == null || StringUtils.isBlank(dateUnit)) {
            return date;
        }
        LocalDateTime dateTime = date2LocalDateTime(date);
        LocalDateTime startDate = switch (dateUnit) {
            case "day" -> dateTime.with(LocalTime.MIN);
            case "week" -> dateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).with(LocalTime.MIN);
            case "month" -> dateTime.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
            default -> throw new IllegalArgumentException("Unknown date unit");
        };

        return localDateTime2Date(startDate);
    }

    /**
     * 获取指定日期的结束时间
     * @param date 时间
     * @param dateUnit 时间单位
     * @return 时间
     */
    public static Date getEndDate(Date date, String dateUnit) {

        if (date == null || StringUtils.isBlank(dateUnit)) {
            return date;
        }
        LocalDateTime dateTime = date2LocalDateTime(date);
        LocalDateTime endDate = switch (dateUnit) {
            case "day" -> dateTime.with(LocalTime.MAX);
            case "week" -> dateTime.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                    .with(LocalTime.MAX);
            case "month" -> dateTime.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
            default -> throw new IllegalArgumentException("Unknown date unit");
        };

        return localDateTime2Date(endDate);
    }

    /**
     * 将字符串转换成日期
     * @param dateTimeStr 支持的格式: 1689159935091, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ssX", "yyyy-MM-dd'T'HH:mm:ssXX", "yyyy-MM-dd'T'HH:mm:ssZZ",
     *         "yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd'T'HH:mm:ss.SSSX", "yyyy-MM-dd'T'HH:mm:ss.SSSXX", "yyyy-MM-dd'T'HH:mm:ss.SSSZZ",
     *         "yyyy-MM-dd", "yyyy-MM-ddX", "yyyy-MM-ddXX", "yyyy-MM-ddZZ"，"yyyyMMddHHmmss", "yyyyMMddHHmm", yyyy-MM-dd hh:mm:ssaa
     * @return 时间
     */
    public static Date convert(String dateTimeStr) {
        if (StringUtils.isBlank(dateTimeStr)) {
            return null;
        }
        dateTimeStr = dateTimeStr.trim();
        if (dateTimeStr.length() == 13 && dateTimeStr.matches(TIME_STAMP_FORMAT)) {
            try {
                return new Date(Long.parseLong(dateTimeStr));
            } catch (Exception e) {
                throw new RuntimeException(String.format("parser %s to Date fail", dateTimeStr));
            }
        }

        try {
            return convert(dateTimeStr, DATE_FORMATS);
        } catch (ParseException e) {
            throw new RuntimeException(String.format("parser %s to Date fail", dateTimeStr));
        }
    }

    /**
     * 将字符串转换成日期
     * @param dateTimeStr 时间字符串
     * @param parsePatterns 提供转换的时间格式
     * @return 时间
     */
    public static Date convert(String dateTimeStr, final String... parsePatterns) throws ParseException {
        for (String dateFormat : parsePatterns) {
            try {
                Date date;
                if (dateTimeStr.length() == 12) {
                    date = DateValidator.getInstance().validate(dateTimeStr, dateFormat, Locale.ENGLISH, TimeZone.getDefault());
                } else {
                    date = DateUtils.parseDate(dateTimeStr, Locale.ENGLISH, dateFormat);
                }
                if (date != null) {
                    return date;
                }
            } catch (Exception ignored) {
                // 这里不处理，失败进行下一个尝试
            }
        }
        throw new ParseException("Unable to parse the date: " + dateTimeStr, -1);
    }

    /**
     * 判断两个日期是否同一天
     * @param date1 时间1
     * @param date2 时间2
     * @return 是否同天
     */
    public static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 根据年月日创建时间
     * @param year 年
     * @param month 月
     * @param day 日
     * @return 时间
     */
    public static Date createDate(int year, int month, int day) {
        LocalDate localDate = LocalDate.of(year, month, day);
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

}
