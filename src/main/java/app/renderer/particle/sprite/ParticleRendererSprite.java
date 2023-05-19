package app.renderer.particle.sprite;

import app.renderer.OpenGLObjects;
import app.renderer.VaoModel;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.file.Paths;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class ParticleRendererSprite {
    private static final float[] VERTICES = {-0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f};
    private static final int MAX_INSTANCES = 10000;
    private static final int INSTANCE_DATA_LENGTH = 21;

    private static final FloatBuffer buffer = BufferUtils.createFloatBuffer(MAX_INSTANCES * INSTANCE_DATA_LENGTH);
    private int vbo;
    private final OpenGLObjects openGLObjects;
    private final ParticleShaderSprite particleShaderSprite;
    private VaoModel vaoModel;

    public ParticleRendererSprite(OpenGLObjects openGLObjects) {
        this.openGLObjects = openGLObjects;
        particleShaderSprite = new ParticleShaderSprite(Paths.get("src\\main\\resources\\shaders\\particle\\sprite\\particleSprite.glsl"));
        vaoModel = openGLObjects.loadToVAO(VERTICES, 2);
    }

    public void render(List<Particle> particles) {
        particleShaderSprite.start();
        glBindVertexArray(vaoModel.vaoID());
        glEnableVertexAttribArray(0);
        glDepthMask(false);
        for (Particle particle : particles) {
            particleShaderSprite.loadModelMatrix(particle.getTransform().getModelMatrix());
            glDrawArrays(GL_TRIANGLE_STRIP, 0, vaoModel.VertexCount());
        }

        //openGLObjects.updateVbo(vbo, particle.getPositions(), buffer);

        //glDrawArraysInstanced(GL_POINTS, 0, vaoModel.VertexCount(), particle.getSize());

        glDisableVertexAttribArray(0);

        glBindVertexArray(0);
        glDepthMask(true);
        particleShaderSprite.stop();
    }
}
