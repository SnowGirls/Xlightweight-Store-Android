package com.tesla.modules.store.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class TlStoreLogger {

    private static final String TAG = TlStoreLogger.class.getSimpleName();

    private static final SimpleDateFormat _df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);

    /// default simple logger
    public static Logger logger = log -> android.util.Log.i(TAG, "[" + _df.format(new Date()) + "]: " + log);

    /// default verbose logger
    public static LoggerVerbose loggerVerbose = (level, module, file, line, function, message) -> {
        String log = "[" + _df.format(new Date()) + "]: " + file + ":" + line + " " + function + " " + message;
        switch (level) {
            case DEBUG:
                android.util.Log.d(TAG, log);
                break;
            case INFO:
                android.util.Log.i(TAG, log);
                break;
            case WARN:
                android.util.Log.w(TAG, log);
                break;
            case ERROR:
            case FATAL:
                android.util.Log.e(TAG, log);
                break;
        }
    };


    /**
     * simple log
     */
    public static interface Logger {
        void log(String message);
    }

    public static void log(String message) {
        if (logger == null) return;
        logger.log(message);
    }

    /**
     * verbose log
     */
    public static enum Level {
        NONE(0), DEBUG(1), INFO(2), WARN(3), ERROR(4), FATAL(5);

        int value;

        Level(int value) {
            this.value = value;
        }
    }

    public static interface LoggerVerbose {
        void log(Level level, String module, String file, int line, String function, String message);
    }

    public static void d(String message) {
        verbose(Level.DEBUG, message);
    }

    public static void i(String message) {
        verbose(Level.INFO, message);
    }

    public static void w(String message) {
        verbose(Level.WARN, message);
    }

    public static void e(String message) {
        verbose(Level.ERROR, message);
    }

    public static void e(Throwable e) {
        verbose(Level.ERROR, getThrowableMessage(e));
    }

    public static void f(String message) {
        verbose(Level.FATAL, message);
    }

    public static void f(Throwable e) {
        verbose(Level.FATAL, getThrowableMessage(e));
    }

    public static String getThrowableMessage(Throwable e) {
        return "\n" + e.getMessage() + "\n" + getStackTrace(e) + "\n";
    }

    public static String getStackTrace(Throwable e) {
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors, true));
        return errors.toString();
    }

    private static void verbose(Level level, String message) {
        if (loggerVerbose == null) {
            log(level.toString() + ":" + message);
            return;
        }
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length < 2) {
            log(level.toString() + ":" + message + "\n" + Arrays.toString(stackTrace));
            return;
        }
        StackTraceElement element = stackTrace[1];
        // String className = element.getClassName();
        String methodName = element.getMethodName();
        String fileName = element.getFileName();
        int lineNumber = element.getLineNumber();
        loggerVerbose.log(level, TAG, fileName, lineNumber, methodName, message);
    }

}
