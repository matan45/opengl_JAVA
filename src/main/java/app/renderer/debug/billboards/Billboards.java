package app.renderer.debug.billboards;

import app.math.OLVector3f;
import app.math.components.Camera;
import app.renderer.OpenGLObjects;
import app.utilities.ArrayUtil;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class Billboards {

    private ShaderBillboards shaderBillboards;
    private int vaoModel;

    private static final float[] squareVertices = {
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            -0.5f, 0.5f, 0.0f,
            0.5f, 0.5f, 0.0f,
    };

    public Billboards(OpenGLObjects openGLObjects) {
        shaderBillboards = new ShaderBillboards(Paths.get("src\\main\\resources\\shaders\\debug\\billboards.glsl"));

        vaoModel = openGLObjects.loadToVAO(squareVertices);
    }

    public void render(Camera camera, OLVector3f position) {
        shaderBillboards.start();
        shaderBillboards.loadViewMatrix(camera.getViewMatrix());
        shaderBillboards.loadProjectionMatrix(camera.getProjectionMatrix());
        shaderBillboards.loadCenterPosition(position);

        glBindVertexArray(vaoModel);
        glEnableVertexAttribArray(0);

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

        glDisableVertexAttribArray(0);
        glBindVertexArray(0);

        shaderBillboards.stop();
    }
}
