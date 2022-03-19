package app.renderer.terrain.quadtree;

import app.math.OLVector3f;
import app.math.components.Camera;
import app.renderer.HeightMapTextureData;
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
    HeightMapTextureData texture;

    Camera camera;

    public TerrainQuadtreeRenderer(OpenGLObjects openGLObjects, Textures textures, Camera camera) {

        this.textures = textures;
        shaderTerrainQuadtree = new ShaderTerrainQuadtree(Paths.get("src\\main\\resources\\shaders\\terrain\\quad\\quadtree.glsl"));

        terrainQuadtree = new TerrainQuadtree(camera, shaderTerrainQuadtree);
        vao = openGLObjects.loadToVAO(quadData, quadPatchInd);

        this.camera = camera;
    }

    public void init(String path) {
        texture = textures.heightMap(path, 3);

        shaderTerrainQuadtree.start();
        shaderTerrainQuadtree.loadTexHighMap(texture.image());
        shaderTerrainQuadtree.loadTerrainHeightOffset(40f);
        shaderTerrainQuadtree.loadTerrainWidth(texture.width());
        shaderTerrainQuadtree.loadTerrainLength(texture.height());
        OLVector3f origin = new OLVector3f(texture.width() / 2.0f, 0.0f, texture.height() / 2.0f);
        shaderTerrainQuadtree.loadTerrainOrigin(origin);
        shaderTerrainQuadtree.stop();
    }

    public void render() {
        shaderTerrainQuadtree.start();
        shaderTerrainQuadtree.loadViewMatrix(camera.getViewMatrix());
        shaderTerrainQuadtree.loadProjectionMatrix(camera.getProjectionMatrix());

        glPatchParameteri(GL_PATCH_VERTICES, 4);

        glBindVertexArray(vao);
        glEnableVertexAttribArray(0);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture.image());

        terrainQuadtree.terrainCreateTree(new OLVector3f(), texture.width(), texture.height());
        terrainQuadtree.terrainRender();

        glDisableVertexAttribArray(0);
        glBindVertexArray(0);

        shaderTerrainQuadtree.stop();


    }
}
