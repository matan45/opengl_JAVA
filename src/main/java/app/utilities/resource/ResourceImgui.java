package app.utilities.resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class ResourceImgui {

    protected byte[] loadAsByte(Path path) {
        try {
            return Files.readAllBytes(path.toAbsolutePath());
        } catch (IOException e) {
            //TODO custom excption handler
            e.printStackTrace();
        }
        return new byte[0];
    }
}
