package app.renderer;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.glDeleteTextures;

public class Textures {
    List<Integer> textures = new ArrayList<Integer>();

    public void cleanUp() {
        for (int texture : textures) {
            glDeleteTextures(texture);
        }
    }
}
