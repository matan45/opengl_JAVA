package app.utilities.logger;

import java.io.IOException;

public final class LogInfo extends Logger {

    public static void println(String log) {
        try {
            logArray.write("INFO".getBytes());
            logArray.write("\t".getBytes());
            logArray.write(log.getBytes());
            logArray.write("\n".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
