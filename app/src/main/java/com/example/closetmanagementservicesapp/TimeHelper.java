package com.example.closetmanagementservicesapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeHelper {
    public static SimpleDateFormat dateFormat;
    public static SimpleDateFormat hourFormat;
    public static SimpleDateFormat hourMinuteFormat;

    static {
        TimeZone KoreaTime = TimeZone.getTimeZone("Asia/Seoul");
        dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        hourFormat = new SimpleDateFormat("HH", Locale.KOREA);
        hourMinuteFormat = new SimpleDateFormat("HH:mm", Locale.KOREA);

        dateFormat.setTimeZone(KoreaTime);
        hourFormat.setTimeZone(KoreaTime);
        hourMinuteFormat.setTimeZone(KoreaTime);
    }

    public static String getDate() {
        return dateFormat.format(new Date());
    }

    public static String getTime() {
        return hourFormat.format(new Date()) + "00";
    }

    public static String getFormattedTime() {
        return hourMinuteFormat.format(new Date());
    }
}