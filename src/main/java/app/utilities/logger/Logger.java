package app.utilities.logger;

import java.io.ByteArrayOutputStream;

public sealed class Logger permits LogError, LogInfo {
    static ByteArrayOutputStream logArray;

    protected Logger() {
    }

    public static void init() {
        logArray = new ByteArrayOutputStream();
    }

    public static String outputLog() {
        return logArray.toString();
    }

    public static void clear() {
        logArray.reset();
    }
}
