package com.example.snap_develop.util;

public class LogUtil {
    public static String getClassName() {
        return Thread.currentThread().getStackTrace()[3].getClassName();
    }

    public static String getLogMessage() {
        StackTraceElement thread = Thread.currentThread().getStackTrace()[3];
        return String.format("%s: %s", thread.getMethodName(), thread.getLineNumber());
    }
}
