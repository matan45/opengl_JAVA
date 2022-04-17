package app.utilities.logger;

import java.io.IOException;
import java.time.LocalTime;

public final class LogError extends Logger {

    public static void println(Object log) {
        LocalTime time = LocalTime.now();
        String value = time.format(format);
        try {
            logArray.write(value.getBytes());
            logArray.write("\t".getBytes());
            logArray.write("ERROR:".getBytes());
            logArray.write("\t".getBytes());
            logArray.write(log.toString().getBytes());
            logArray.write("\n".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
