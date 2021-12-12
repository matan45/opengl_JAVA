package app.utilities.logger;

import java.io.ByteArrayOutputStream;

abstract sealed class Logger permits LogError, LogInfo {
    static ByteArrayOutputStream logArray;

    protected Logger() {
    }

    public static void init() {
        logArray = new ByteArrayOutputStream();
    }

    public static String outputLog() {
        String result = logArray.toString();
        logArray.reset();
        return result;
    }
}
