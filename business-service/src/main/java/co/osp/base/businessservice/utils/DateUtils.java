package co.osp.base.businessservice.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static String today(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }
    public static String today(String format){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }
    public static String timeNow() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }
    public static String timeNowInstant(Instant instant, String format) {
        Date date = Date.from(instant);
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }
    public static String timeNow(String format) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }
    public static String currentYear()
    {
        Calendar calendar = Calendar.getInstance();
        return String.valueOf(calendar.get(Calendar.YEAR));
    }
    public static String lastDateOfQuarter(Long year, Long quarter)
    {
        if (year==null) year = Long.valueOf(DateUtils.currentYear());
        if (quarter==null) quarter = 1L;
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate ld = LocalDate.parse("" + year + "-01-01", formatter);
        // Then create a new date with new quarter * 3 and last day of month
        return "" + ld.withMonth(quarter.intValue() * 3).with(TemporalAdjusters.lastDayOfMonth());
    }
    public static String firstDateOfQuarter(Long year, Long quarter)
    {
        if (year==null) year = Long.valueOf(DateUtils.currentYear());
        if (quarter==null) quarter = 1L;
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate ld = LocalDate.parse("" + year + "-01-01", formatter);
        // Then create a new date with new quarter * 3 and last day of month
        return "" + ld.withMonth(quarter.intValue() * 3 - 2).with(TemporalAdjusters.firstDayOfMonth());
    }
    public static String nextDay(Long year, Long quarter, Long numbNextDay)
    {
        Long nextQuarter = quarter + 1;
        String nextMonth = (nextQuarter * 3 - 2)>=10?("" + (nextQuarter * 3 - 2)):("0" + (nextQuarter * 3 - 2));;
        if (quarter == 4) {
            nextQuarter = 1L;
            year++;
            nextMonth = (nextQuarter * 3 - 2)>=10?("" + (nextQuarter * 3 - 2)):("0" + (nextQuarter * 3 - 2));
        }
        String firstNextQuarter = "" + year + "-" + nextMonth + "-01";
//System.out.println("=========== " + firstNextQuarter);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(firstNextQuarter);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DATE, numbNextDay.intValue());
            Date nextDate = c.getTime();
            return (sdf.format(nextDate));
        }catch(ParseException pe){
            return null;
        }
    }
    public static String lastDateOfFirstMonthOfNextQuarter(Long year, Long quarter)
    {
        if (quarter==4) {
            year = year++;
        }else{
            quarter++;
        }
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate ld = LocalDate.parse("" + year + "-01-01", formatter);
        // Then create a new date with new quarter * 3 and last day of month
        return "" + ld.withMonth(quarter.intValue() * 3 - 2).with(TemporalAdjusters.lastDayOfMonth());
    }
    public static String convertFormat(String input, String inputFormat, String outputFormat)
    {
        if (input==null) return null;
        try {
            SimpleDateFormat inputSdf = new SimpleDateFormat(inputFormat);
            Date date = inputSdf.parse(input);
            SimpleDateFormat outputSdf = new SimpleDateFormat(outputFormat);
            return outputSdf.format(date);
        }catch(ParseException pe){
            return null;
        }
    }
    public static String convertFormatTime(String time, String fromFormat, String toFormat)
    {
        try {
            Date date = new SimpleDateFormat(fromFormat).parse(time);
            SimpleDateFormat outputSdf = new SimpleDateFormat(toFormat);
            return outputSdf.format(date);
        }catch(Exception e){}

        return null;
    }
     public static int getYear(Date date) {
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        return cl.get(Calendar.YEAR);
    }
    public static Date getAddDate(int year, int months, int days) {
        Calendar cl = Calendar.getInstance();
        cl.set(Calendar.YEAR, year);
        cl.set(Calendar.MONTH, months);
        cl.set(Calendar.DAY_OF_MONTH, days);

        return cl.getTime();
    }
//    public static String lastDateOfFirstMonthNextQuarter(Long quarter, Long year)
//    {
//
//    }

    public static int getDay (Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getMonth (Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

}
