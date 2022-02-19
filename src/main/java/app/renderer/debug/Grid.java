package app.renderer.debug;

import app.math.components.Camera;
import app.renderer.OpenGLObjects;
import app.utilities.ArrayUtil;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class Grid {
    private final List<Float> vertices;

    private final ShaderGrid shaderGrid;

    private final Camera camera;
    private final int vaoModel;

    private boolean render;

    public Grid(OpenGLObjects openGLObjects, Camera camera) {
        this.camera = camera;
        vertices = new ArrayList<>();
        shaderGrid = new ShaderGrid(Paths.get("src\\main\\resources\\shaders\\debug\\grid.glsl"));
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
            glEnable(GL_BLEND);
            glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
            shaderGrid.start();
            shaderGrid.loadViewMatrix(camera.getViewMatrix());
            shaderGrid.loadProjectionMatrix(camera.getProjectionMatrix());

            glBindVertexArray(vaoModel);
            glEnableVertexAttribArray(0);

            glDrawArrays(GL_TRIANGLES, 0, 6);

            glDisableVertexAttribArray(0);
            glBindVertexArray(0);

            shaderGrid.stop();
            glDisable(GL_BLEND);
        }
    }

    public void setRender(boolean render) {
        this.render = render;
    }
}
