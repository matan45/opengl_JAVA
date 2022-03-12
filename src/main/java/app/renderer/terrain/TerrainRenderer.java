package app.renderer.terrain;

import app.renderer.OpenGLObjects;
import app.renderer.Textures;
import app.renderer.VaoModel;

import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL40.GL_PATCHES;

public class TerrainRenderer {

    VaoModel model;
    Terrain terrain;
    ShaderTerrain shaderTerrain;

    public TerrainRenderer(Textures textures, OpenGLObjects openGLObjects) {
        terrain = new Terrain(textures, openGLObjects);
        shaderTerrain = new ShaderTerrain(Paths.get("src\\main\\resources\\shaders\\terrain\\terrain.glsl"));
    }

    public void init(String path) {
        model = terrain.generateTerrain(path);
    }

    public void render() {

        glPolygonMode(GL_FRONT_AND_BACK, GL_TRIANGLES);

        shaderTerrain.start();

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
