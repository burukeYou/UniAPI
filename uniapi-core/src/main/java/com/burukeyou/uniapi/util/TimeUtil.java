package com.burukeyou.uniapi.util;


import java.sql.Time;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


public class TimeUtil {

    public static final String NORM_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String PURE_DATETIME_PATTERN = "yyyyMMddHHmmss";

    public static final String PURE_DATE_PATTERN = "yyyyMMdd";


    public static final DateTimeFormatter NORM_DATETIME_FORMATTER = createFormatter(NORM_DATETIME_PATTERN);

    public static final DateTimeFormatter PURE_DATETIME_FORMATTER = createFormatter(PURE_DATETIME_PATTERN);

    public static final DateTimeFormatter PURE_DATE_FORMATTER = createFormatter(PURE_DATE_PATTERN);


    private TimeUtil(){}

    public static Date toDate(LocalDateTime localDateTime){
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    public static String timestamp2TimeStr(Long timestamp){
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone).toString();
    }

    public static Long toTimestamp(LocalDateTime localDateTime){
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static LocalDateTime toLocalDateTime(LocalDate localDate, Time time){
        return LocalDateTime.of(localDate, time.toLocalTime());

    }

    public static String formatNormal(Date date) {
       return formatNormal(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
    }

    public static String formatNormal(LocalDateTime time) {
        if (time == null){
            return "";
        }
        return NORM_DATETIME_FORMATTER.format(time);
    }

    public static String formatPure(LocalDate time) {
        if (time == null){
            return "";
        }
        return PURE_DATE_FORMATTER.format(time);
    }

    public static String formatPure(LocalDateTime time) {
        if (time == null){
            return "";
        }
       return PURE_DATETIME_FORMATTER.format(time);
    }

    public static List<String> formatNormal(List<LocalDateTime> tmpList) {
        return tmpList.stream().map(TimeUtil::formatNormal).collect(Collectors.toList());
    }

    public static String formatNormal(LocalDate time) {
        if (time == null){
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault()).withZone(ZoneId.systemDefault());
        return formatter.format(time);
    }

    public static String formatChina(LocalDate time) {
        if (time == null){
            return "";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日", Locale.getDefault()).withZone(ZoneId.systemDefault());
        return formatter.format(time);
    }


    public static DateTimeFormatter createFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
                .withZone(ZoneId.systemDefault());
    }



}
