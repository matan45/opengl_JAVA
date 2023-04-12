package app.renderer.debug.billboards;

import app.math.OLVector3f;
import app.renderer.OpenGLObjects;
import app.renderer.shaders.UniformsNames;

import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.glActiveTexture;
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
        shaderBillboards = new ShaderBillboards(Paths.get("resources\\shaders\\debug\\billboards.glsl"));
        shaderBillboards.bindBlockBuffer(UniformsNames.MATRICES.getUniformsName(), 0);
        shaderBillboards.start();
        shaderBillboards.connectTextureUnits();
        shaderBillboards.stop();
        vaoModel = openGLObjects.loadToVAO(squareVertices);
        this.imageIcon = imageIcon;
    }

    public void render(OLVector3f position) {
        shaderBillboards.start();
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
