package app.renderer.terrain;

import app.math.MathUtil;
import app.math.OLVector2f;
import app.math.OLVector3f;
import app.renderer.HeightMapData;
import app.renderer.OpenGLObjects;
import app.renderer.Textures;
import app.renderer.VaoModel;

import java.awt.image.BufferedImage;

public class Terrain {

    private final Textures textures;
    private final OpenGLObjects openGLObjects;

    int x = 0;
    int z = -1 * SIZE;

    private static final int SIZE = 1024;
    private static final int MAX_HEIGHT = 40;
    private static final int MAX_PIXEL_COLOUR = 256 * 256 * 256;
    private float[][] heights;

    public Terrain(Textures textures, OpenGLObjects openGLObjects) {
        this.textures = textures;
        this.openGLObjects = openGLObjects;
    }

    public VaoModel generateTerrain(String heightMap) {
        HeightMapData data = textures.getHeightMapData(heightMap);

        int vertexCount = data.height();
        heights = new float[vertexCount][vertexCount];
        int count = vertexCount * vertexCount;

        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6 * (vertexCount - 1) * (vertexCount - 1)];

        int vertexPointer = 0;
        for (int i = 0; i < vertexCount; i++) {
            for (int j = 0; j < vertexCount; j++) {
                vertices[vertexPointer * 3] = (float) j / (vertexCount - 1) * SIZE;
                float height = getHeight(j, i, data.image());
                heights[j][i] = height;
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
        for (int gz = 0; gz < vertexCount - 1; gz++) {
            for (int gx = 0; gx < vertexCount - 1; gx++) {
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

    //for the ray cast
    public float getHeightOfTerrain(float worldX, float worldZ) {
        float terrainX = worldX - this.x;
        float terrainZ = worldZ - this.z;
        float gridSquareSize = SIZE / ((float) heights.length - 1);
        int gridX = (int) Math.floor(terrainX / gridSquareSize);
        int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
        if (gridX >= heights.length - 1 || gridZ >= heights.length - 1 || gridX < 0 || gridZ < 0)
            return 0;
        float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
        float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
        float answer;
        if (xCoord <= (1 - zCoord)) {
            answer = MathUtil.barryCentric(new OLVector3f(0, heights[gridX][gridZ], 0),
                    new OLVector3f(1, heights[gridX + 1][gridZ], 0), new OLVector3f(0, heights[gridX][gridZ + 1], 1),
                    new OLVector2f(xCoord, zCoord));
        } else {
            answer = MathUtil.barryCentric(new OLVector3f(1, heights[gridX + 1][gridZ], 0),
                    new OLVector3f(1, heights[gridX + 1][gridZ + 1], 1), new OLVector3f(0, heights[gridX][gridZ + 1], 1),
                    new OLVector2f(xCoord, zCoord));
        }
        return answer;

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
