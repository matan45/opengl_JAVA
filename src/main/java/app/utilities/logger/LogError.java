package app.utilities.logger;

import java.io.IOException;

public final class LogError extends Logger {

    public static void println(String log) {
        try {
            logArray.write("ERROR:".getBytes());
            logArray.write("\t".getBytes());
            logArray.write(log.getBytes());
            logArray.write("\n".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
