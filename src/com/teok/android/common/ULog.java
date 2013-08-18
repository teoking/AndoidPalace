package com.teok.android.common;

import android.util.Log;

/**
 * An encapsulation of {@link Log} for uniform format of the log output.
 */
public class ULog {
    private static final String APP_TAG = "AndroidPalace";
    public static final boolean DEBUG = true;
    private static final String FORMAT = "%s:  %s";

    public static void v(String tag, String msg) {
        if (DEBUG) {
            Log.v(APP_TAG, reform(tag, msg));
        }
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            Log.v(APP_TAG, reform(tag, msg), tr);
        }
    }

    public static void e(String tag, String msg) {
        if (DEBUG) {
            Log.e(APP_TAG, reform(tag, msg));
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            Log.e(APP_TAG, reform(tag, msg), tr);
        }
    }

    public static void i(String tag, String msg) {
        if (DEBUG) {
            Log.i(APP_TAG, reform(tag, msg));
        }
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            Log.i(APP_TAG, reform(tag, msg));
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            Log.d(APP_TAG, reform(tag, msg));
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            Log.d(APP_TAG, reform(tag, msg), tr);
        }
    }

    public static void w(String tag, String msg) {
        if (DEBUG) {
            Log.w(APP_TAG, reform(tag, msg));
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            Log.w(APP_TAG, reform(tag, msg), tr);
        }
    }

    private static String reform(String tag, String msg) {
        return String.format(FORMAT, tag, msg);
    }
}