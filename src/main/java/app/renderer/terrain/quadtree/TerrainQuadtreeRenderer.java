package app.renderer.terrain.quadtree;

import app.math.components.Camera;
import app.renderer.OpenGLObjects;
import app.renderer.Textures;

import java.nio.file.Paths;

public class TerrainQuadtreeRenderer {

    TerrainQuadtree terrainQuadtree;
    ShaderTerrainQuadtree shaderTerrainQuadtree;
    OpenGLObjects openGLObjects;
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

    int quadPatchInd[] = {0, 1, 2, 3};

    int vao;

    public TerrainQuadtreeRenderer(OpenGLObjects openGLObjects, Textures textures, Camera camera) {

        this.textures = textures;
        shaderTerrainQuadtree = new ShaderTerrainQuadtree(Paths.get("src\\main\\resources\\shaders\\terrain\\quad\\quadtree.glsl"));

        terrainQuadtree = new TerrainQuadtree(camera, shaderTerrainQuadtree);
        vao = openGLObjects.loadToVAO(quadData, quadPatchInd);
    }
}
