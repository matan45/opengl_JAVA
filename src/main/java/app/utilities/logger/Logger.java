package app.utilities.logger;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

public sealed class Logger permits LogError, LogInfo {
    protected static ByteArrayOutputStream logArray;
    protected static DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm:ss");

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
