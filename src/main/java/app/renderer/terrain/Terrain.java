package app.renderer.terrain;

import app.math.OLVector3f;
import app.renderer.HeightMapData;
import app.renderer.OpenGLObjects;
import app.renderer.Textures;
import app.renderer.VaoModel;

import java.awt.image.BufferedImage;

public record Terrain(Textures textures, OpenGLObjects openGLObjects) {

    private static final int SIZE = 512;
    private static final float MAX_HEIGHT = 40f; // is between -40 to 40
    private static final int MAX_PIXEL_COLOUR = 256 * 256 * 256;

    public VaoModel generateTerrain(String heightMap) {
        HeightMapData data = textures.getHeightMapData(heightMap);

        int vertexCount = data.height();
        int count = data.height() * data.weight();

        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6 * (vertexCount - 1) * (vertexCount - 1)];

        int vertexPointer = 0;
        for (int i = 0; i < data.height(); i++) {
            for (int j = 0; j < data.weight(); j++) {
                vertices[vertexPointer * 3] = (float) j / (vertexCount - 1) * SIZE;
                float height = getHeight(j, i, data.image());
                vertices[vertexPointer * 3 + 1] = height;
                vertices[vertexPointer * 3 + 2] = (float) i / (vertexCount - 1) * SIZE;
                OLVector3f normal = calculateNormal(j, i, data.image());
                normals[vertexPointer * 3] = normal.x;
                normals[vertexPointer * 3 + 1] = normal.y;
                normals[vertexPointer * 3 + 2] = normal.z;
                textureCoords[vertexPointer * 2] = (float) j / (vertexCount - 1);
                textureCoords[vertexPointer * 2 + 1] = (float) i / (vertexCount - 1);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for (int gz = 0; gz < data.height() - 1; gz++) {
            for (int gx = 0; gx < data.weight() - 1; gx++) {
                int topLeft = (gz * vertexCount) + gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz + 1) * vertexCount) + gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }

        return openGLObjects.loadToVAO(vertices, textureCoords, normals, indices);
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
