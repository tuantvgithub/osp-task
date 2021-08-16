package co.osp.base.filemodule.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static String now(String format){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }
}
