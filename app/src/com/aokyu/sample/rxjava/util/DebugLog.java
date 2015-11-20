package com.aokyu.sample.rxjava.util;

import com.aokyu.sample.rxjava.BuildConfig;

import android.util.Log;

/**
 * Static utility methods for the debug logging.
 * Logs are provided in the following format:
 * <p>
 * [TAG](XXXX): [CLASS_NAME]#[METHOD_NAME]-[LINE_NUMBER]
 * </p>
 */
public class DebugLog {

    /**
     * The logcat tag.
     */
    private static final String TAG = BuildConfig.APPLICATION_ID;

    /**
     * Whether the logcat for the application is enabled.
     */
    private static final boolean LOG_ENABLED = BuildConfig.DEBUG;

    public static final class StackIndex {
        private StackIndex() {}

        /**
         * The index of the caller.
         */
        public static final int SELF = 3;

        /**
         * The index of the parent for the caller.
         */
        public static final int PARENT = 4;
    }

    private DebugLog() {}

    private static String buildMessage(String message, int stackIndex) {
        Throwable throwable = new Throwable();
        StackTraceElement element = throwable.getStackTrace()[stackIndex];
        String className = element.getClassName();
        int start = className.lastIndexOf(".");
        String simpleClassName = className;
        if (start != -1) {
            simpleClassName = className.substring(start + 1);
        }

        String methodName = element.getMethodName();
        int lineNumber = element.getLineNumber();

        String header = new StringBuilder()
                .append(simpleClassName)
                .append("#")
                .append(methodName)
                .append("()")
                .append("-[")
                .append(lineNumber)
                .append("]")
                .toString();

        if (message != null) {
            return message.isEmpty() ? header : header + ": " + message;
        } else {
            return header;
        }

    }

    public static void printStackTrace(int level, String tag) {
        Throwable throwable = new Throwable();
        StackTraceElement[] elements = throwable.getStackTrace();

        int size = elements.length;
        for (int i = 1; i < size; i++) {
            StackTraceElement element = elements[i];
            String fileName = element.getFileName();
            int lineNumber = element.getLineNumber();
            String className = element.getClassName();
            String methodName = element.getMethodName();

            String message = new StringBuilder()
                    .append(fileName)
                    .append("(")
                    .append(lineNumber)
                    .append("): ")
                    .append(className)
                    .append("#")
                    .append(methodName)
                    .append("()")
                    .toString();

            Log.println(level, tag, message);
        }
    }

    public static int d(String message) {
        return d(TAG, message, StackIndex.SELF);
    }

    public static int d(String message, int stackIndex) {
        return d(TAG, message, stackIndex);
    }

    public static int d(String tag, String message) {
        return d(tag, message, StackIndex.SELF);
    }

    private static int d(String tag, String message, int stackIndex) {
        if (LOG_ENABLED) {
            return Log.d(tag, buildMessage(message, stackIndex));
        } else {
            return 0;
        }
    }

    public static int e(String message) {
        return e(TAG, message, StackIndex.SELF);
    }

    public static int e(String tag, String message) {
        return e(tag, message, StackIndex.SELF);
    }

    private static int e(String tag, String message, int stackIndex) {
        if (LOG_ENABLED) {
            return Log.e(tag, buildMessage(message, stackIndex));
        } else {
            return Log.e(tag, message);
        }
    }

    public static int i(String message) {
        return i(TAG, message, StackIndex.SELF);
    }

    public static int i(String tag, String message) {
        return i(tag, message, StackIndex.SELF);
    }

    private static int i(String tag, String message, int stackIndex) {
        if (LOG_ENABLED) {
            return Log.i(tag, buildMessage(message, stackIndex));
        } else {
            return 0;
        }
    }

    public static int v(String message) {
        return v(TAG, message, StackIndex.SELF);
    }

    public static int v(String tag, String message) {
        return v(tag, message, StackIndex.SELF);
    }

    private static int v(String tag, String message, int stackIndex) {
        if (LOG_ENABLED) {
            return Log.v(tag, buildMessage(message, stackIndex));
        } else {
            return 0;
        }
    }

    public static int w(String message) {
        return w(TAG, message, StackIndex.SELF);
    }

    public static int w(String tag, String message) {
        return w(tag, message, StackIndex.SELF);
    }

    private static int w(String tag, String message, int stackIndex) {
        if (LOG_ENABLED) {
            return Log.w(tag, buildMessage(message, stackIndex));
        } else {
            return 0;
        }
    }
}