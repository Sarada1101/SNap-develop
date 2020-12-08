package com.example.snap_develop;

import timber.log.Timber;

public class MyDebugTree extends Timber.DebugTree {

    public final static String START_LOG = "START ";
    public final static String INPUT_LOG = "INPUT ";
    public final static String RETURN_LOG = "RETURN ";
    public final static String SUCCESS_LOG = "SUCCESS ";
    public final static String FAILURE_LOG = "FAILURE ";

    @Override
    protected String createStackElementTag(StackTraceElement element) {
        return String.format("[C:%s] [M:%s] [L:%s]",
                element.getClassName(),
                element.getMethodName(),
                element.getLineNumber());
    }
}
