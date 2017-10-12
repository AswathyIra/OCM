package com.ocm.framework;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;


import com.ocm.OCMApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static android.text.format.DateFormat.getDateFormat;
import static android.text.format.DateFormat.getTimeFormat;

public final class Utility {
    private static final String UtcDateFormat = "yyyy-MM-dd HH:mm:ssZ";
    private static final long seconds = 1000;
    private static final long minutes = seconds * 60;
    private static final long hours = minutes * 60;
    private static final long days = hours * 24;


    public static String updateTitle(CharSequence title) {
        return "    " + title;
    }

    public static String updateSubTitle(CharSequence subTitle) {
        return "      " + subTitle;
    }

    public static boolean isNullOrEmpty(String text) {
        //TODO: fix server to not send "null" as value of NULL object.
        return text == null || text.trim().isEmpty() || text.trim().equals("null");
    }

    public static String fileDateFormat(Date date) {
        return new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss").format(date);
    }

    public static String displayDateTimeFormat(Date displayDate) {
        return String.format("%s", DateFormat.format("MMM dd, hh:mm a", displayDate));
    }

    public static String displayDateTime12hrFormat(Date date) {
        return new SimpleDateFormat("MM/dd/yyyy hh:mm a").format(date);
    }

    public static String displayTimeFormat(Date displayDate) {
        return getTimeFormat(OCMApp.getApp().getApplicationContext()).format(displayDate);
    }

    public static String displayDateFormat(Date displayDate) {
        return getDateFormat(OCMApp.getApp().getApplicationContext()).format(displayDate);
    }

    public static String formatSameDayTime(Date then) {
        if (then == null) return "";

        if (DateUtils.isToday(then.getTime())) {
            return displayTimeFormat(then);
        }

        return displayDateTimeFormat(then);
    }

    public static String getTimestampFrom(Date dateTime) {
        long differenceBetweenMSFTAndUNIXTicks = 621355968000000000L,
                ticksPerMillisecond = 10000;

        return String.valueOf((dateTime.getTime() * ticksPerMillisecond) + differenceBetweenMSFTAndUNIXTicks);
    }

    public static String getCurrentTimestamp() {
        return getTimestampFrom(new Date());
    }

    public static boolean canConnectToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) OCMApp.getApp().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    public static boolean isCameraPresent() {
        return (Camera.getNumberOfCameras() > 0);
    }

    public static TimeZone getDeviceTimeZone() {
        return TimeZone.getDefault();
    }





    public static String getResourceString(int resourceId) {
        return OCMApp.getApp().getResources().getString(resourceId);
    }

    public static String getResourcePluralString(int resourceId, int count) {
        return OCMApp.getApp().getResources().getQuantityString(resourceId, count, count);
    }

    public static int getResourceColor(int resourceId) {
        return OCMApp.getApp().getResources().getColor(resourceId);
    }

    public static Date getDateWithoutTime(Date date) {
        Calendar instance = Calendar.getInstance();

        instance.setTime(date);
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);

        return instance.getTime();
    }

    /**
     * Compares only dates without the time part.
     *
     * @param date1
     * @param date2
     * @return an {int < 0} if date1 is less than date2, {0} if
     * they are equal, and an {int > 0} if this date1 is greater than date2.
     */    public static int CompareDatesWithoutTime(Date date1, Date date2) {
        return getDateWithoutTime(date1).compareTo(getDateWithoutTime(date2));
    }


    public static boolean IsSameAsDeviceTimeZone(TimeZone timeZone) {
        TimeZone deviceTimeZone = Utility.getDeviceTimeZone();
        if (timeZone.getRawOffset() == deviceTimeZone.getRawOffset()) {
            if (timeZone.useDaylightTime() && deviceTimeZone.useDaylightTime()) {
                return (timeZone.getDSTSavings() == deviceTimeZone.getDSTSavings());
            } else if (timeZone.useDaylightTime() || deviceTimeZone.useDaylightTime()) {
                return false;
            }
            return true;
        }
        return false;
    }


    public static String getNullableString(JSONObject jsonObject, String key) throws JSONException {
        if (jsonObject.isNull(key))
            return null;
        return jsonObject.getString(key);
    }

    public static boolean isDateBefore(Date sourceDate, Date compareDate) {
        return compareDate.getTime() - sourceDate.getTime() > 0;
    }



    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static class Storage {
        private static String getStoragePath() {
            return OCMApp.getApp().getExternalFilesDir(null).getPath();
        }

        public static File getFile(String relativePath) {
            return new File(getStoragePath(), relativePath);
        }


        public static String getPath(String... paths) {
            if (paths.length == 0) return null;
            File file = new File(paths[0]);
            for (int i = 1; i < paths.length; i++) {
                file = new File(file, paths[i]);
            }
            return file.getPath();
        }





        public static void deleteDirectory(File directory, boolean recursive) {
            final File[] files = directory.listFiles();
            if (files == null)
                return;

            for (File file : files) {
                if (file.isDirectory() && recursive) {
                    deleteDirectory(file, true);
                }
                file.delete();
            }
            directory.delete();
        }

        public static double round(double value, int places) {
            if (places < 0) throw new IllegalArgumentException();

            long factor = (long) Math.pow(10, places);
            value = value * factor;
            long tmp = Math.round(value);
            return (double) tmp / factor;
        }

        public static String getSortedString(List<Long> keys) {
            Long[] longKeys = new Long[keys.size()];
            keys.toArray(longKeys);
            Arrays.sort(longKeys);
            return getString(longKeys);
        }

        private static String getString(Long[] keys) {
            String val = "";
            for (Long key : keys) {
                val = val + " " + key.toString();
            }
            return val;
        }

        public static List<Long> join(List<Long> set1, List<Long> set2) {
            ArrayList<Long> set = new ArrayList(set1);
            set.addAll(set2);
            return set;
        }

        public static List<Long> getValues(String concatValues) {
            String[] keys = concatValues.split(" ");
            List<Long> values = new ArrayList<>();
            for (String key : keys) {
                if (key.trim().equalsIgnoreCase("")) continue;
                values.add(Long.valueOf(key.trim()));
            }
            return values;
        }



        public static void setStatusBarColor(Window window, Context context, int color) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(ContextCompat.getColor(context, color));
            }
        }

    }

}