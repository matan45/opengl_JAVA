package app.utilities;

import java.io.File;
import java.util.Optional;

public class FileUtil {
    private FileUtil() {
    }

    public static Optional<String> getFileExtension(File file) {
        String path = file.getAbsolutePath();
        return Optional.of(path)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(path.lastIndexOf(".") + 1));

    }
}
