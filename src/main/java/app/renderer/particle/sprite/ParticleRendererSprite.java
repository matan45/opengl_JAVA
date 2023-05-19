package app.renderer.particle.sprite;

import app.math.components.OLTransform;
import app.renderer.OpenGLObjects;
import app.renderer.VaoModel;

import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL31C.glDrawArraysInstanced;

public class ParticleRendererSprite {
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

    public void init(OLTransform olTransform, Particle particle){
        this.particle = particle;
        this.olTransform = olTransform;
        vbo = openGLObjects.createEmptyVbo(particle.getSize() * 3);
        vaoModel = openGLObjects.loadToVAO(particle.getPositions(), 3);
    }

    public void update(float dt) {
        particle.update(dt);
    }

    public void render() {
        particleShaderSprite.start();
        particleShaderSprite.loadModelMatrix(olTransform.getModelMatrix());
        glBindVertexArray(vaoModel.vaoID());
        glEnableVertexAttribArray(0);

        openGLObjects.updateVbo(vbo, particle.getPositions(), particle.getParticleBuffer());

        glDrawArraysInstanced(GL_POINTS, 0, vaoModel.VertexCount(), particle.getSize());

        glDisableVertexAttribArray(0);

        glBindVertexArray(0);

        particleShaderSprite.stop();
    }
}
