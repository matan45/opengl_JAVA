package app.renderer.texture;

import java.io.Serializable;
import java.nio.FloatBuffer;

public record Image(FloatBuffer data, int w, int h, int format, long id) implements Serializable {
}
