package app.renderer.debug.grid;

import app.math.components.Camera;
import app.renderer.OpenGLObjects;
import app.renderer.framebuffer.Framebuffer;

import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class Grid {

    private final ShaderGrid shaderGrid;

    private final Camera camera;
    private final int vaoModel;

    private boolean render;

    private static final float SIZE = 1.0f;

    private static final float[] quadData = {
            SIZE, SIZE,
            -SIZE, -SIZE,
            -SIZE, SIZE,
            -SIZE, -SIZE,
            SIZE, SIZE,
            SIZE, -SIZE
    };

    public Grid(OpenGLObjects openGLObjects, Framebuffer framebuffer, Camera camera) {
        this.camera = camera;
        shaderGrid = new ShaderGrid(Paths.get("src\\main\\resources\\shaders\\debug\\grid.glsl"));
        vaoModel = openGLObjects.loadToVAOVec2(quadData);
        glViewport(0, 0, framebuffer.getWidth(), framebuffer.getHeight());
    }

    public void render() {
        if (render) {
            glEnable(GL_BLEND);
            glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
            shaderGrid.start();
            shaderGrid.loadViewMatrix(camera.getViewMatrix());
            shaderGrid.loadProjectionMatrix(camera.getProjectionMatrix());
            shaderGrid.loadFar(camera.getFar());
            shaderGrid.loadNear(camera.getNear());

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
