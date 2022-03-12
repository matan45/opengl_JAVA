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
    private float factor;
    private boolean isActive;
    private int displacementMap;
    private final Textures textures;
    private OLTransform olTransform;

    public TerrainRenderer(Textures textures, OpenGLObjects openGLObjects, Camera camera) {
        terrain = new Terrain(textures, openGLObjects);
        this.textures = textures;
        this.camera = camera;
        wireframe = false;
        factor = 2f;
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
            shaderTerrain.loadCameraPosition(camera.getPosition());
            shaderTerrain.loadgDispFactor(factor);
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

    public void setFactor(float factor) {
        this.factor = factor;
    }

    public float getFactor() {
        return factor;
    }

    public String getPath() {
        return path;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
