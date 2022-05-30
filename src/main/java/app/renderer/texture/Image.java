package app.renderer.texture;

import java.io.Serializable;

public record Image(byte[] data, int w, int h, int format, long uuid) implements Serializable {
}
