package app.renderer.terrain.quadtree;

import app.math.OLVector3f;
import app.math.components.Camera;
import app.renderer.OpenGLObjects;
import app.renderer.Textures;
import app.utilities.logger.LogInfo;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL40.GL_PATCH_VERTICES;
import static org.lwjgl.opengl.GL40.glPatchParameteri;

public class TerrainQuadtreeRenderer {

    TerrainQuadtree terrainQuadtree;
    ShaderTerrainQuadtree shaderTerrainQuadtree;

    Textures textures;

    float[] quadData = {
            // Vert 1
            -1.0f, 0.0f, -1.0f, 1.0f,    // Position

            // Vert 2
            1.0f, 0.0f, -1.0f, 1.0f,        // Position

            // Vert 3
            1.0f, 0.0f, 1.0f, 1.0f,        // Position

            // Vert 4
            -1.0f, 0.0f, 1.0f, 1.0f,        // Position
    };

    int[] quadPatchInd = {0, 1, 2, 3};

    int vao;
    int texture;

    Camera camera;

    String path;

    boolean wireframe;
    boolean isActive;

    public TerrainQuadtreeRenderer(OpenGLObjects openGLObjects, Textures textures, Camera camera) {

        this.textures = textures;
        shaderTerrainQuadtree = new ShaderTerrainQuadtree(Paths.get("src\\main\\resources\\shaders\\terrain\\quad\\quadtree.glsl"));

        terrainQuadtree = new TerrainQuadtree(camera, shaderTerrainQuadtree);
        vao = openGLObjects.loadToVAO(quadData, quadPatchInd);

        wireframe = false;

        this.camera = camera;
    }

    public void init(String path) {
        texture = textures.heightMap(path);

        this.path = path;

        shaderTerrainQuadtree.start();
        shaderTerrainQuadtree.loadTexHighMap();
        shaderTerrainQuadtree.loadTerrainHeightOffset(1000f);
        shaderTerrainQuadtree.loadTerrainWidth(10000f);
        shaderTerrainQuadtree.loadTerrainLength(10000f);
        OLVector3f origin = new OLVector3f(10000 / 2.0f, 0.0f, 10000 / 2.0f);
        shaderTerrainQuadtree.loadTerrainOrigin(origin);
        shaderTerrainQuadtree.stop();

    }

    public void render() {
        if (isActive) {
            shaderTerrainQuadtree.start();
            shaderTerrainQuadtree.loadViewMatrix(camera.getViewMatrix());
            shaderTerrainQuadtree.loadProjectionMatrix(camera.getProjectionMatrix());
            shaderTerrainQuadtree.loadToggleWireframe(wireframe);
            shaderTerrainQuadtree.loadViewPort(camera.getViewPort());

            glPatchParameteri(GL_PATCH_VERTICES, 4);

            glBindVertexArray(vao);
            glEnableVertexAttribArray(0);

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture);

            terrainQuadtree.terrainCreateTree(new OLVector3f(), 10000, 10000);
            terrainQuadtree.terrainRender();

            glDisableVertexAttribArray(0);
            glBindVertexArray(0);

            shaderTerrainQuadtree.stop();
        }

    }

    public boolean isWireframe() {
        return wireframe;
    }

    public void setWireframe(boolean wireframe) {
        this.wireframe = wireframe;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getPath() {
        return path;
    }
}
