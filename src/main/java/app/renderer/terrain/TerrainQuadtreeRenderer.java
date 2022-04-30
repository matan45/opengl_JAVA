package app.renderer.terrain;

import app.math.OLVector3f;
import app.math.components.Camera;
import app.renderer.OpenGLObjects;
import app.renderer.Textures;
import app.renderer.fog.Fog;
import app.renderer.ibl.SkyBox;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
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

    private boolean wireframe;
    private boolean isActive;

    private float displacementFactor;

    private Fog fog;
    private final SkyBox skyBox;

    private static final int WIDTH = 8192;
    private static final int LENGTH = 8192;

    public TerrainQuadtreeRenderer(OpenGLObjects openGLObjects, Textures textures, Camera camera, SkyBox skyBox) {

        this.textures = textures;
        shaderTerrainQuadtree = new ShaderTerrainQuadtree(Paths.get("src\\main\\resources\\shaders\\terrain\\quadtree.glsl"));

        terrainQuadtree = new TerrainQuadtree(camera, shaderTerrainQuadtree);
        vao = openGLObjects.loadToVAO(quadData, quadPatchInd);

        wireframe = false;
        displacementFactor = 200f;

        this.camera = camera;
        this.skyBox = skyBox;
    }

    public void init(Path path) {
        texture = textures.loadTexture(path);

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
            glEnable(GL_CULL_FACE);
            glCullFace(GL_BACK);
            shaderTerrainQuadtree.start();
            shaderTerrainQuadtree.loadViewPort(camera.getViewPort());
            shaderTerrainQuadtree.loadViewMatrix(camera.getViewMatrix());
            shaderTerrainQuadtree.loadProjectionMatrix(camera.getProjectionMatrix());
            shaderTerrainQuadtree.loadCameraPosition(camera.getPosition());

            shaderTerrainQuadtree.loadToggleWireframe(wireframe);
            shaderTerrainQuadtree.loadTerrainHeightOffset(displacementFactor);

            if (fog != null) {
                shaderTerrainQuadtree.loadIsFog(true);
                shaderTerrainQuadtree.loadFogColor(fog.getFogColor());
                shaderTerrainQuadtree.loadSightRange(fog.getSightRange());
            } else
                shaderTerrainQuadtree.loadIsFog(false);

            glPatchParameteri(GL_PATCH_VERTICES, 4);

            glBindVertexArray(vao);
            glEnableVertexAttribArray(0);

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture);

            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_CUBE_MAP, skyBox.getIrradianceMap());

            terrainQuadtree.terrainCreateTree(0, 0, 0, WIDTH, LENGTH);
            terrainQuadtree.terrainRender();

            glDisableVertexAttribArray(0);
            glBindVertexArray(0);

            shaderTerrainQuadtree.stop();
            glDisable(GL_CULL_FACE);
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

    public void setFog(Fog fog) {
        this.fog = fog;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

}
