package app.renderer.terrain;

import app.math.OLVector3f;
import app.math.components.Camera;
import app.renderer.OpenGLObjects;
import app.renderer.Textures;

import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL40.GL_PATCH_VERTICES;
import static org.lwjgl.opengl.GL40.glPatchParameteri;

public class TerrainQuadtreeRenderer {

    private final TerrainQuadtree terrainQuadtree;
    private final ShaderTerrainQuadtree shaderTerrainQuadtree;
    private final Textures textures;
    private final Camera camera;

    private static final float[] quadData = {
            // Vert 1
            -1.0f, 0.0f, -1.0f, 1.0f,    // Position
            // Vert 2
            1.0f, 0.0f, -1.0f, 1.0f,        // Position
            // Vert 3
            1.0f, 0.0f, 1.0f, 1.0f,        // Position
            // Vert 4
            -1.0f, 0.0f, 1.0f, 1.0f,        // Position
    };

    private static final int[] quadPatchInd = {0, 1, 2, 3};

    private final int vao;
    private int texture;

    private String path;

    boolean wireframe;
    boolean isActive;

    float displacementFactor;

    private static final int WIDTH = 4096;
    private static final int LENGTH = 4096;

    public TerrainQuadtreeRenderer(OpenGLObjects openGLObjects, Textures textures, Camera camera) {

        this.textures = textures;
        shaderTerrainQuadtree = new ShaderTerrainQuadtree(Paths.get("src\\main\\resources\\shaders\\terrain\\quadtree.glsl"));

        terrainQuadtree = new TerrainQuadtree(camera, shaderTerrainQuadtree);
        vao = openGLObjects.loadToVAO(quadData, quadPatchInd);

        wireframe = false;
        displacementFactor = 40f;

        this.camera = camera;
    }

    public void init(String path) {
        texture = textures.loadTexture(path);

        this.path = path;

        shaderTerrainQuadtree.start();
        shaderTerrainQuadtree.loadTexHighMap();
        shaderTerrainQuadtree.loadTerrainWidth(WIDTH);
        shaderTerrainQuadtree.loadTerrainLength(LENGTH);
        OLVector3f origin = new OLVector3f(WIDTH / 2.0f, 0.0f, LENGTH / 2.0f);
        shaderTerrainQuadtree.loadTerrainOrigin(origin);
        shaderTerrainQuadtree.stop();

    }

    public void render() {
        if (isActive) {
            shaderTerrainQuadtree.start();
            shaderTerrainQuadtree.loadToggleWireframe(wireframe);
            shaderTerrainQuadtree.loadViewPort(camera.getViewPort());
            shaderTerrainQuadtree.loadTerrainHeightOffset(displacementFactor);
            shaderTerrainQuadtree.loadViewMatrix(camera.getViewMatrix());
            shaderTerrainQuadtree.loadProjectionMatrix(camera.getProjectionMatrix());

            glPatchParameteri(GL_PATCH_VERTICES, 4);

            glBindVertexArray(vao);
            glEnableVertexAttribArray(0);

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture);

            terrainQuadtree.terrainCreateTree(0, 0, 0, WIDTH, LENGTH);
            terrainQuadtree.terrainRender();

            glDisableVertexAttribArray(0);
            glBindVertexArray(0);

            shaderTerrainQuadtree.stop();
        }

    }

    public float getDisplacementFactor() {
        return displacementFactor;
    }

    public void setDisplacementFactor(float displacementFactor) {
        this.displacementFactor = displacementFactor;
    }

    public void setWireframe(boolean wireframe) {
        this.wireframe = wireframe;
    }

    public float getRenderDepth() {
        return terrainQuadtree.getRenderDepth();
    }

    public float getNumTerrainNodes() {
        return terrainQuadtree.getNumTerrainNodes();
    }


    public void setActive(boolean active) {
        isActive = active;
    }

    public String getPath() {
        return path;
    }
}
