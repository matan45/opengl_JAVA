package app.utilities.logger;

import java.io.IOException;
import java.time.LocalTime;

public final class LogInfo extends Logger {

    public static void println(String log) {
        LocalTime time = LocalTime.now();
        String value = time.format(format);
        try {
            logArray.write(value.getBytes());
            logArray.write("\t".getBytes());
            logArray.write("INFO:".getBytes());
            logArray.write("\t".getBytes());
            logArray.write(log.getBytes());
            logArray.write("\n".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
