package co.osp.base.businessservice.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

public class DateUtil {


    public static Map<String, Instant> getTheLastMonthOfCurrentQuarter(int year, int quarter)
    {
        Map<String, Instant> map = new HashMap<>();

        if( quarter == 1)
        {
            map.put("current", LocalDateTime.of(year, 3, 31, 23, 59, 59).toInstant(ZoneOffset.UTC));
            map.put("previous", LocalDateTime.of(year-1, 12, 31, 23, 59, 59).toInstant(ZoneOffset.UTC));
        } else if (quarter == 2){
            map.put("current", LocalDateTime.of(year, 6, 30, 23, 59, 59).toInstant(ZoneOffset.UTC));
            map.put("previous", LocalDateTime.of(year, 3, 31, 23, 59, 59).toInstant(ZoneOffset.UTC));
        } else if (quarter == 3){
            map.put("current", LocalDateTime.of(year, 9, 30, 23, 59, 59).toInstant(ZoneOffset.UTC));
            map.put("previous", LocalDateTime.of(year, 6, 30, 23, 59, 59).toInstant(ZoneOffset.UTC));
        } else {
            map.put("current", LocalDateTime.of(year, 12, 31, 23, 59, 59).toInstant(ZoneOffset.UTC));
            map.put("previous", LocalDateTime.of(year, 9, 30, 23, 59, 59).toInstant(ZoneOffset.UTC));
        }

        return map;

    }

    public static Map<String, Instant> getTheFirstMonthOfCurrentQuarter(int year, int quarter)
    {
        Map<String, Instant> map = new HashMap<>();

        if(quarter == 1)
        {
            map.put("current", LocalDateTime.of(year, 1, 01, 0, 0, 0).toInstant(ZoneOffset.UTC));
            map.put("previous", LocalDateTime.of(year-1, 10, 01, 0, 0, 0).toInstant(ZoneOffset.UTC));
        } else if (quarter == 2){
            map.put("current", LocalDateTime.of(year, 4, 01, 0, 0, 0).toInstant(ZoneOffset.UTC));
            map.put("previous", LocalDateTime.of(year, 1, 01, 0, 0, 0).toInstant(ZoneOffset.UTC));
        } else if (quarter == 3){
            map.put("current", LocalDateTime.of(year, 7, 01, 0, 0, 0).toInstant(ZoneOffset.UTC));
            map.put("previous", LocalDateTime.of(year, 4, 01, 0, 0, 0).toInstant(ZoneOffset.UTC));
        } else {
            map.put("current", LocalDateTime.of(year, 10, 01, 0, 0, 0).toInstant(ZoneOffset.UTC));
            map.put("previous", LocalDateTime.of(year, 7, 01, 0, 0, 0).toInstant(ZoneOffset.UTC));
        }

        return map;

    }

    public static String getQ(int month)
    {
        if (month >= 1 && month <= 3)
        {
            return ""+1;
        } else if (month >= 4 && month <= 6)
        {
            return ""+2;
        }else if (month >= 7 && month <= 9)
        {
            return ""+3;
        }else{
            return ""+4;
        }
    }

    public static Instant getTheLastMonthOfAnyQuarter(Instant time)
    {
        int month = LocalDateTime.ofInstant(time, ZoneOffset.UTC).getMonth().getValue();
        int year = LocalDateTime.ofInstant(time, ZoneOffset.UTC).getYear();

        if( 1 <= month && month <= 3)
        {
            return LocalDateTime.of(year, 3, 31, 23, 59, 59).toInstant(ZoneOffset.UTC);
        } else if (4 <= month && month <= 6){
            return LocalDateTime.of(year, 6, 30, 23, 59, 59).toInstant(ZoneOffset.UTC);
        } else if (7 <= month && month <= 9){
            return LocalDateTime.of(year, 9, 30, 23, 59, 59).toInstant(ZoneOffset.UTC);
        } else {
            return LocalDateTime.of(year, 12, 31, 23, 59, 59).toInstant(ZoneOffset.UTC);
        }

    }

    public static Boolean compareDateToTheFirstDayOfQuarter(Instant time)
    {
        int month = LocalDateTime.ofInstant(time, ZoneOffset.UTC).getMonth().getValue();
        int day = LocalDateTime.ofInstant(time, ZoneOffset.UTC).getDayOfMonth();

        if(day == 1 && month == 1)
        {
            return true;
        } else if(day == 1 && month == 4)
        {
            return true;
        } else if(day == 1 && month == 7)
        {
            return true;
        } else if (day == 1 && month == 10){
            return true;
        }

        return false;
    }

    public static Integer[] getLastHalfYear(Integer month, Integer year) {
        Integer[] result = new Integer[2];
        result[1] = year;
        if (month < 6) {
            result[1] = year - 1;
            result[0] = 2;
        } else if (month == 12){
            result[0] = 2;
        } else {
            result[0] = 1;
        }
        return result;
    }

    public static String getLastDayOfFirstMonthNextQuarter(String currentQuarter, int year) {
        switch (currentQuarter) {
            case "1":
                return "30/4/" + year;
            case "2":
                return "31/7/" + year;
            case "3":
                return "31/10/" + year;
            default:
                return "31/1/" + (year + 1);
        }
    }

    public static String getLastDayOfFirstMonthNextQuarterOsp(String currentQuarter, int year) {
        switch (currentQuarter) {
            case "1":
                return year + "-04-30";
            case "2":
                return year + "-08-31";
            case "3":
                return year + "-10-31";
            default:
                return (year + 1) + "-01-31";
        }
    }

    public static String convertQuarterToString(Integer quarter) {
        switch (quarter) {
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            default:
                return "IV";
        }
    }
}
