package com.sanislo.lostandfound.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by root on 27.12.16.
 */

public class DateUtils {
    public final String TAG = DateUtils.class.getSimpleName();

    public static String getDateText(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("Y.M.d: H:mm");
        return dateFormat.format(new Date(timestamp));
    }
}
