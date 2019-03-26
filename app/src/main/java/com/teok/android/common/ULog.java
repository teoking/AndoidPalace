package com.teok.android.common;

import android.util.Log;

import timber.log.Timber;

/**
 * An encapsulation of {@link Log} for uniform format of the log output.
 */
public class ULog {
    private static final String APP_TAG = "AndroidPalace";
    public static final boolean DEBUG = true;
    private static final String FORMAT = "%s:  %s";

    public static void v(String tag, String msg) {
        if (DEBUG) {
            Timber.v(reform(tag, msg));
        }
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            Timber.v(tr, reform(tag, msg));
        }
    }

    public static void e(String tag, String msg) {
        if (DEBUG) {
            Timber.tag(APP_TAG).e(reform(tag, msg));
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            Timber.tag(APP_TAG).e(tr, reform(tag, msg));
        }
    }

    public static void i(String tag, String msg) {
        if (DEBUG) {
            Timber.tag(APP_TAG).i(reform(tag, msg));
        }
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            Timber.tag(APP_TAG).i(reform(tag, msg));
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            Timber.tag(APP_TAG).d(reform(tag, msg));
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            Timber.tag(APP_TAG).d(tr, reform(tag, msg));
        }
    }

    public static void d(String tag, String format, Object... args) {
        if (DEBUG) {
            Timber.tag(tag).d(format, args);
        }
    }

    public static void w(String tag, String msg) {
        if (DEBUG) {
            Timber.tag(APP_TAG).w(reform(tag, msg));
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            Timber.tag(APP_TAG).w(tr, reform(tag, msg));
        }
    }

    private static String reform(String tag, String msg) {
        return String.format(FORMAT, tag, msg);
    }
}