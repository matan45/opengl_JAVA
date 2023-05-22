package app.renderer.particle.sprite;

import app.math.OLMatrix4f;
import app.renderer.OpenGLObjects;
import app.renderer.VaoModel;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.file.Paths;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL31.glDrawArraysInstanced;

public class ParticleRendererSprite {
    private static final float[] VERTICES = {-0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f};
    private static final int MAX_INSTANCES = 10000;
    //for the model matrix
    private static final int INSTANCE_DATA_LENGTH = 16;
    private static final FloatBuffer buffer = BufferUtils.createFloatBuffer(MAX_INSTANCES * INSTANCE_DATA_LENGTH);
    private final OpenGLObjects openGLObjects;
    private final int vbo;
    private final ParticleShaderSprite particleShaderSprite;
    private final VaoModel vaoModel;
    private int pointer;

    public ParticleRendererSprite(OpenGLObjects openGLObjects) {
        this.openGLObjects = openGLObjects;
        particleShaderSprite = new ParticleShaderSprite(Paths.get("src\\main\\resources\\shaders\\particle\\sprite\\particleSprite.glsl"));
        vaoModel = openGLObjects.loadToVAO(VERTICES, 2);
        vbo = openGLObjects.createEmptyVbo(INSTANCE_DATA_LENGTH * MAX_INSTANCES);
        openGLObjects.addInstanceAttribute(vaoModel.vaoID(), vbo, 1, 4, INSTANCE_DATA_LENGTH, 0);
        openGLObjects.addInstanceAttribute(vaoModel.vaoID(), vbo, 2, 4, INSTANCE_DATA_LENGTH, 4);
        openGLObjects.addInstanceAttribute(vaoModel.vaoID(), vbo, 3, 4, INSTANCE_DATA_LENGTH, 8);
        openGLObjects.addInstanceAttribute(vaoModel.vaoID(), vbo, 4, 4, INSTANCE_DATA_LENGTH, 12);
    }

    public void render(List<Particle> particles, int image) {
        particleShaderSprite.start();
        glBindVertexArray(vaoModel.vaoID());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);
        glEnableVertexAttribArray(4);
        glDepthMask(false);
        pointer = 0;
        float[] vboData = new float[particles.size() * INSTANCE_DATA_LENGTH];
        int number = 0;
        for (Particle particle : particles) {
            if (particle.isLife()) {
                fillModel(vboData, particle.getTransform().getModelMatrix());
                number++;
            }
        }

        openGLObjects.updateVbo(vbo, vboData, buffer);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, image);

        glDrawArraysInstanced(GL_TRIANGLE_STRIP, 0, vaoModel.VertexCount(), number);

        glBindTexture(GL_TEXTURE_2D, 0);

        glDepthMask(true);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(3);
        glDisableVertexAttribArray(4);

        glBindVertexArray(0);

        particleShaderSprite.stop();
    }

    private void fillModel(float[] vboData, OLMatrix4f model) {
        vboData[pointer++] = model.m00;
        vboData[pointer++] = model.m01;
        vboData[pointer++] = model.m02;
        vboData[pointer++] = model.m03;
        vboData[pointer++] = model.m10;
        vboData[pointer++] = model.m11;
        vboData[pointer++] = model.m12;
        vboData[pointer++] = model.m13;
        vboData[pointer++] = model.m20;
        vboData[pointer++] = model.m21;
        vboData[pointer++] = model.m22;
        vboData[pointer++] = model.m23;
        vboData[pointer++] = model.m30;
        vboData[pointer++] = model.m31;
        vboData[pointer++] = model.m32;
        vboData[pointer++] = model.m33;
    }
}
