package app.renderer.terrain;

import app.math.components.Camera;
import app.math.components.OLTransform;
import app.renderer.OpenGLObjects;
import app.renderer.Textures;
import app.renderer.VaoModel;

import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL40.GL_PATCHES;

public class TerrainRenderer {

    private VaoModel model;
    private final Terrain terrain;
    private final ShaderTerrain shaderTerrain;
    private final Camera camera;
    private boolean wireframe;
    private String path;
    private float displacementFactor;
    private float tessellationFactor;
    private boolean isActive;
    private int displacementMap;
    private final Textures textures;
    private OLTransform olTransform;

    public TerrainRenderer(Textures textures, OpenGLObjects openGLObjects, Camera camera) {
        terrain = new Terrain(textures, openGLObjects);
        this.textures = textures;
        this.camera = camera;
        wireframe = false;
        displacementFactor = 2f;
        tessellationFactor = 0.75f;
        isActive = false;
        shaderTerrain = new ShaderTerrain(Paths.get("src\\main\\resources\\shaders\\terrain\\terrain.glsl"));

        shaderTerrain.start();
        shaderTerrain.connectTextureUnits();
        shaderTerrain.stop();
    }

    public void init(String path, OLTransform olTransform) {
        model = terrain.generateTerrain(path);
        displacementMap = textures.loadTexture(path);
        this.path = path;
        this.olTransform = olTransform;
    }

    public void render() {
        if (isActive) {
            if (wireframe)
                glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

            shaderTerrain.start();
            shaderTerrain.loadProjectionMatrix(camera.getProjectionMatrix());
            shaderTerrain.loadViewMatrix(camera.getViewMatrix());
            shaderTerrain.loadTessellationFactor(tessellationFactor);
            shaderTerrain.loadgDispFactor(displacementFactor);
            shaderTerrain.loadModelMatrix(olTransform.getModelMatrix());

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, displacementMap);

            glBindVertexArray(model.vaoID());
            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);
            glEnableVertexAttribArray(2);


            glDrawElements(GL_PATCHES, model.VertexCount(), GL_UNSIGNED_INT, 0);

            glDisableVertexAttribArray(0);
            glDisableVertexAttribArray(1);
            glDisableVertexAttribArray(2);
            glBindVertexArray(0);

            shaderTerrain.stop();

            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);//for normal mode
        }

    }

    public void setWireframe(boolean wireframe) {
        this.wireframe = wireframe;
    }

    public void setDisplacementFactor(float displacementFactor) {
        this.displacementFactor = displacementFactor;
    }

    public float getDisplacementFactor() {
        return displacementFactor;
    }

    public String getPath() {
        return path;
    }

    public float getTessellationFactor() {
        return tessellationFactor;
    }

    public void setTessellationFactor(float tessellationFactor) {
        this.tessellationFactor = tessellationFactor;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
