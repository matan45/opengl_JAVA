package app.renderer.terrain;

import app.math.OLVector3f;
import app.renderer.OpenGLObjects;
import app.renderer.Textures;

import java.awt.image.BufferedImage;

public class Terrain {

    private final Textures textures;
    private final OpenGLObjects openGLObjects;

    private static final int SIZE = 1024;
    private static final int MAX_HEIGHT = 40;
    private static final int MAX_PIXEL_COLOUR = 256 * 256 * 256;
    private float[][] heights;

    public Terrain(Textures textures, OpenGLObjects openGLObjects) {
        this.textures = textures;
        this.openGLObjects = openGLObjects;
    }


    private float getHeight(int x, int y, BufferedImage image) {
        if (x < 0 || x >= image.getHeight() || y < 0 || y >= image.getWidth())
            return 0;
        float height = image.getRGB(x, y);
        height += MAX_PIXEL_COLOUR / 2f;
        height /= MAX_PIXEL_COLOUR / 2f;
        height *= MAX_HEIGHT;
        return height;
    }

    private OLVector3f calculateNormal(int x, int y, BufferedImage image) {
        float heightL = getHeight(x - 1, y, image);
        float heightR = getHeight(x + 1, y, image);
        float heightD = getHeight(x, y - 1, image);
        float heightU = getHeight(x, y + 1, image);
        OLVector3f normal = new OLVector3f(heightL - heightR, 2f, heightD - heightU);
        normal.normalize();
        return normal;
    }
}
