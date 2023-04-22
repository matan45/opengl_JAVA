package app.utilities;

import app.utilities.resource.ResourceManager;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Objects;
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

    public static String getFileNameWithoutExtension(File file) {
        String name = file.getName();
        return name.replaceFirst("[.][^.]+$", "");
    }

    public static Path absolutePath(String relativePath){
        try {
            return Path.of(Objects.requireNonNull(FileUtil.class.getClassLoader().getResource(relativePath)).toURI().getPath().substring(1)).toAbsolutePath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
