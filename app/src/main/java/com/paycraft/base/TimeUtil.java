package com.paycraft.base;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtil {
    public static final String EVOUCHER_SERVER_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    public static final String EXPENSE_FROM_BACKEND_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    public static final String EXPENSE_TO_BACKEND_DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";
    public static final String EXPENSE_DISPLAY_DATE_FORMAT = "dd/MM/yyyy hh:mm aa";
    public static final String EXPENSE_DISPLAY_ONLY_DATE_FORMAT = "dd/MM/yyyy";


    public static final String REPORT_DISPLAY = "dd-MM-yyyy";
    public static final String FORMAT_ONLY_DATE = "yyyy-MM-dd";
    public static final String FORMAT_ONLY_TIME = "hh:mm a";

    public static final String REPORT_COMMENT = "dd/MM/yy hh:mm a";

    public static final String ORDER_FORMAT = "dd MMM yyyy";

    public static String getFormattedDate(String format, Calendar calendar) {
        SimpleDateFormat serverFormat = new SimpleDateFormat(format, Locale.getDefault());
        return serverFormat.format(calendar.getTime());
    }

    public static String getTimeFromTImePicker(int hour, int min) {
        String format;
        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }
        return new StringBuilder().append(String.format("%02d", hour)).append(":").append(String.format("%02d", min))
                .append(" ").append(format).toString();
    }

    public static String getTodayDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat(FORMAT_ONLY_DATE);
        return df.format(c);
    }

    public static String getOrderFormat(Date date) {
        SimpleDateFormat df = new SimpleDateFormat(ORDER_FORMAT);
        return df.format(date);
    }

    public static String formatToFormat(String sourceFormat, String dateString, String targetFormat) {
        try {
            SimpleDateFormat dt = new SimpleDateFormat(sourceFormat);
            Date date = null;
            try {
                date = dt.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat dt1 = new SimpleDateFormat(targetFormat);
            return dt1.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    public static String millisToDateFormat(long millis, String format) {
        Date date = new Date(millis);
        DateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }


    public static long getFormattedDateToMillis(String format, String rawDate) {
        if (Validator.isNullOrEmpty(rawDate))
            return System.currentTimeMillis();

        SimpleDateFormat formatter = new SimpleDateFormat(format);
        try {
            formatter.parse(rawDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return System.currentTimeMillis();
        }
        return formatter.getCalendar().getTimeInMillis();
    }


    public static String localToUtf(String source, String inputFormat, String outputFormat) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(inputFormat);
            df.setTimeZone(TimeZone.getDefault());
            Date date = df.parse(source);
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            df.applyPattern(outputFormat);
            return df.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    public static String utfToLocal(String source, String inputFormat, String outputFormat) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(inputFormat);
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = df.parse(source);
            df.setTimeZone(TimeZone.getDefault());
            df.applyPattern(outputFormat);
            return df.format(date);
        } catch (Exception e) {
            return "";
        }
    }
}
