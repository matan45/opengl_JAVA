package app.renderer.debug;

import app.math.components.Camera;
import app.renderer.OpenGLObjects;
import app.utilities.ArrayUtil;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class Grid {
    List<Float> vertices;

    ShaderGrid shaderGrid;

    OpenGLObjects openGLObjects;
    Camera camera;
    int vaoModel;

    boolean render;

    public Grid(OpenGLObjects openGLObjects, Camera camera) {
        this.openGLObjects = openGLObjects;
        this.camera = camera;
        vertices = new ArrayList<>();
        shaderGrid = new ShaderGrid(Paths.get("src\\main\\resources\\shaders\\debug\\grid.glsl"));
        render = true;
        init();
        vaoModel = openGLObjects.loadToVAO(ArrayUtil.listToArray(vertices));
    }

    public void init() {

        vertices.add(1f);
        vertices.add(1f);
        vertices.add(0f);

        vertices.add(-1f);
        vertices.add(-1f);
        vertices.add(0f);

        vertices.add(-1f);
        vertices.add(1f);
        vertices.add(0f);

        vertices.add(-1f);
        vertices.add(-1f);
        vertices.add(0f);

        vertices.add(1f);
        vertices.add(1f);
        vertices.add(0f);

        vertices.add(1f);
        vertices.add(-1f);
        vertices.add(0f);
    }

    public void render() {
        if (render) {
            shaderGrid.start();
            shaderGrid.loadViewMatrix(camera.getViewMatrix());
            shaderGrid.loadProjectionMatrix(camera.getProjectionMatrix());

            glBindVertexArray(vaoModel);
            glEnableVertexAttribArray(0);

            glDrawArrays(GL_TRIANGLES, 0, 6);

            glDisableVertexAttribArray(0);
            glBindVertexArray(0);

            shaderGrid.stop();
        }
    }

    public void setRender(boolean render) {
        this.render = render;
    }
}
