package app.renderer.texture;

import java.io.Serializable;

public record Image(float[] data,String fileName, int w, int h, int format, long id) implements Serializable {
}
