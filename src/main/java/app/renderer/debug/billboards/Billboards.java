package app.renderer.debug.billboards;

import app.math.OLVector3f;
import app.math.components.Camera;
import app.renderer.OpenGLObjects;

import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class Billboards {
    //TODO batch render
    private final ShaderBillboards shaderBillboards;
    private final int vaoModel;
    private final int imageIcon;

    private static final float[] squareVertices = {
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            -0.5f, 0.5f, 0.0f,
            0.5f, 0.5f, 0.0f,
    };

    public Billboards(OpenGLObjects openGLObjects, int imageIcon) {
        shaderBillboards = new ShaderBillboards(Paths.get("src\\main\\resources\\shaders\\debug\\billboards.glsl"));
        shaderBillboards.start();
        shaderBillboards.connectTextureUnits();
        shaderBillboards.stop();
        vaoModel = openGLObjects.loadToVAO(squareVertices);
        this.imageIcon = imageIcon;
    }

    public void render(Camera camera, OLVector3f position) {
        shaderBillboards.start();
        shaderBillboards.loadViewMatrix(camera.getViewMatrix());
        shaderBillboards.loadProjectionMatrix(camera.getProjectionMatrix());
        shaderBillboards.loadCenterPosition(position);

        glBindVertexArray(vaoModel);
        glEnableVertexAttribArray(0);

        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, imageIcon);

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

        glDisableVertexAttribArray(0);
        glBindVertexArray(0);

        shaderBillboards.stop();
    }
}
