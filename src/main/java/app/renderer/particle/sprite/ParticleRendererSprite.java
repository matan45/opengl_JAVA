package app.renderer.particle.sprite;

import app.math.components.OLTransform;
import app.renderer.OpenGLObjects;
import app.renderer.VaoModel;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL31C.glDrawArraysInstanced;

public class ParticleRendererSprite {
    private static final float[] VERTICES = {-0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f};
    private static final int MAX_INSTANCES = 10000;
    private static final int INSTANCE_DATA_LENGTH = 21;

    private static final FloatBuffer buffer = BufferUtils.createFloatBuffer(MAX_INSTANCES * INSTANCE_DATA_LENGTH);
    private Particle particle;
    private int vbo;
    private final OpenGLObjects openGLObjects;
    private OLTransform olTransform;
    private final ParticleShaderSprite particleShaderSprite;
    private VaoModel vaoModel;

    public ParticleRendererSprite(OpenGLObjects openGLObjects) {
        this.openGLObjects = openGLObjects;
        particleShaderSprite = new ParticleShaderSprite(Paths.get("src\\main\\resources\\shaders\\particle\\sprite\\particleSprite.glsl"));
    }

    public void init(OLTransform olTransform, Particle particle) {
        this.particle = particle;
        this.olTransform = olTransform;
        //vbo = openGLObjects.createEmptyVbo(particle.getSize() * 3);
        vaoModel = openGLObjects.loadToVAO(VERTICES, 2);
        openGLObjects.addInstanceAttribute(vaoModel.vaoID(), vbo, 1, 4, 21, 0);
    }

    public void update(float dt) {
        particle.update(dt);
    }

    public void render() {
        particleShaderSprite.start();
        particleShaderSprite.loadModelMatrix(olTransform.getModelMatrix());
        glBindVertexArray(vaoModel.vaoID());
        glEnableVertexAttribArray(0);
        glDepthMask(false);
        //openGLObjects.updateVbo(vbo, particle.getPositions(), buffer);

        //glDrawArraysInstanced(GL_POINTS, 0, vaoModel.VertexCount(), particle.getSize());

        glDisableVertexAttribArray(0);

        glBindVertexArray(0);
        glDepthMask(true);
        particleShaderSprite.stop();
    }
}
